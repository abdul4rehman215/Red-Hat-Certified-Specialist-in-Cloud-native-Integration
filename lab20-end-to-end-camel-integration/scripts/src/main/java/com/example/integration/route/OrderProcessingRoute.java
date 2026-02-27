package com.alnafi.integration.route;

import com.alnafi.integration.model.Order;
import com.alnafi.integration.model.OrderItem;
import com.alnafi.integration.processor.CsvOrderProcessor;
import com.alnafi.integration.processor.CustomerEnrichmentProcessor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jdbc.JdbcComponent;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OrderProcessingRoute extends RouteBuilder {
 private static final Logger logger = LoggerFactory.getLogger(OrderProcessingRoute.class);

 @Override
 public void configure() throws Exception {
 // Configure database connection
 configureDatabase();

 // Configure CSV data format
 CsvDataFormat csvFormat = new CsvDataFormat();
 csvFormat.setUseMaps(true);
 csvFormat.setHeader(new String[]{"order_number", "customer_code", "product_code", "quantity", "unit_price"});

 // Global error handler
 errorHandler(deadLetterChannel("file:data/error")
 .maximumRedeliveries(3)
 .redeliveryDelay(5000)
 .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.WARN));

 // Main order processing route
 from("file:data/input?move=../processed&moveFailed=../error")
 .routeId("order-file-processor")
 .log("Processing order file: ${header.CamelFileName}")
 .unmarshal(csvFormat)
 .process(new CsvOrderProcessor())
 .to("direct:enrich-customer")
 .to("direct:enrich-products")
 .to("direct:validate-inventory")
 .to("direct:route-order")
 .log("Order processing completed: ${body.orderNumber}");

 // Customer enrichment route
 from("direct:enrich-customer")
 .routeId("customer-enrichment")
 .log("Enriching customer data for: ${body.customerCode}")
 .enrich("direct:lookup-customer", (original, resource) -> {
 original.setProperty("customerData", resource.getIn().getBody());
 return original;
 })
 .process(new CustomerEnrichmentProcessor());

 // Customer lookup route
 from("direct:lookup-customer")
 .routeId("customer-lookup")
 .setBody(simple("SELECT customer_name, email, phone, address FROM customers WHERE customer_code = '${body.customerCode}'"))
 .to("jdbc:dataSource")
 .log("Customer lookup result: ${body}");

 // Product enrichment route
 from("direct:enrich-products")
 .routeId("product-enrichment")
 .log("Enriching product details for order: ${body.orderNumber}")
 .process(exchange -> {
 Order order = exchange.getIn().getBody(Order.class);

 for (OrderItem item : order.getItems()) {
 // Look up product in DB
 String sql = "SELECT product_name, unit_price FROM products WHERE product_code = '" + item.getProductCode() + "'";
 exchange.getIn().setBody(sql);
 Exchange dbResult = exchange.getContext().createProducerTemplate().request("jdbc:dataSource", exchange);

 @SuppressWarnings("unchecked")
 List<Map<String, Object>> rows = (List<Map<String, Object>>) dbResult.getIn().getBody(List.class);
 if (rows == null || rows.isEmpty()) {
 throw new IllegalStateException("Product not found: " + item.getProductCode());
 }

 Map<String, Object> product = rows.get(0);
 item.setProductName((String) product.get("product_name"));

 // ensure unit price from DB if CSV is missing/zero
 if (item.getUnitPrice() == null || item.getUnitPrice().doubleValue() <= 0) {
 item.setUnitPrice((java.math.BigDecimal) product.get("unit_price"));
 }
 item.calculateLineTotal();
 }

 // recalc total after enrichment
 java.math.BigDecimal total = order.getItems().stream()
 .map(OrderItem::getLineTotal)
 .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
 order.setTotalAmount(total);

 exchange.getIn().setBody(order);
 });

 // Inventory validation route
 from("direct:validate-inventory")
 .routeId("inventory-validation")
 .log("Validating inventory for order: ${body.orderNumber}")
 .split(simple("${body.items}")).shareUnitOfWork()
 .setHeader("productCode", simple("${body.productCode}"))
 .setHeader("requiredQty", simple("${body.quantity}"))
 .toD("http://localhost:8080/inventory/check?throwExceptionOnFailure=true&bridgeEndpoint=true&productCode=${header.productCode}")
 .unmarshal().json()
 .choice()
 .when(simple("${body[availableQuantity]} >= ${header.requiredQty}"))
 .log("Inventory available for ${header.productCode}: ${body[availableQuantity]}")
 .otherwise()
 .log("Insufficient inventory for ${header.productCode}")
 .throwException(new IllegalStateException("Insufficient inventory for " + "${header.productCode}"))
 .end()
 .end() // end split
 .log("Inventory validated for order: ${body.orderNumber}");

 // Order routing based on business rules
 from("direct:route-order")
 .routeId("order-routing")
 .choice()
 .when(simple("${body.totalAmount} > 1000"))
 .log("High-value order: ${body.orderNumber}")
 .to("direct:high-value-processing")
 .when(simple("${body.totalAmount} > 500"))
 .log("Medium-value order: ${body.orderNumber}")
 .to("direct:medium-value-processing")
 .otherwise()
 .log("Standard order: ${body.orderNumber}")
 .to("direct:standard-processing")
 .end();

 // High-value order processing
 from("direct:high-value-processing")
 .routeId("high-value-processing")
 .log("Processing high-value order: ${body.orderNumber}")
 .to("direct:save-order")
 .to("direct:send-priority-notification")
 .to("direct:generate-report");

 // Medium-value order processing
 from("direct:medium-value-processing")
 .routeId("medium-value-processing")
 .log("Processing medium-value order: ${body.orderNumber}")
 .to("direct:save-order")
 .to("direct:send-notification")
 .to("direct:generate-report");

 // Standard order processing
 from("direct:standard-processing")
 .routeId("standard-processing")
 .log("Processing standard order: ${body.orderNumber}")
 .to("direct:save-order")
 .to("direct:generate-report");

 // Save order to database (orders + order_items)
 from("direct:save-order")
 .routeId("save-order")
 .log("Saving order to database: ${body.orderNumber}")
 .process(exchange -> {
 Order order = exchange.getIn().getBody(Order.class);
 order.setStatus("PROCESSED");
 })
 // Insert into orders
 .setBody(simple("INSERT INTO orders (order_number, customer_id, total_amount, status, processed_at) " +
 "SELECT '${body.orderNumber}', customer_id, ${body.totalAmount}, '${body.status}', now() " +
 "FROM customers WHERE customer_code = '${body.customerCode}'"))
 .to("jdbc:dataSource")
 .log("Order header saved successfully")
 // Insert items (simple approach: one-by-one)
 .process(exchange -> {
 Order order = exchange.getIn().getBody(Order.class);
 for (OrderItem item : order.getItems()) {
 String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, line_total) " +
 "SELECT o.order_id, p.product_id, " + item.getQuantity() + ", " + item.getUnitPrice() + ", " + item.getLineTotal() +
 " FROM orders o, products p WHERE o.order_number = '" + order.getOrderNumber() + "' AND p.product_code = '" + item.getProductCode() + "'";
 exchange.getContext().createProducerTemplate().sendBody("jdbc:dataSource", sql);
 }
 });

 // Generate order report
 from("direct:generate-report")
 .routeId("generate-report")
 .log("Generating report for order: ${body.orderNumber}")
 .process(exchange -> {
 Order order = exchange.getIn().getBody(Order.class);
 StringBuilder report = new StringBuilder();
 report.append("ORDER CONFIRMATION\n");
 report.append("==================\n");
 report.append("Order Number: ").append(order.getOrderNumber()).append("\n");
 report.append("Customer: ").append(order.getCustomerName()).append("\n");
 report.append("Email: ").append(order.getCustomerEmail()).append("\n");
 report.append("Total Amount: $").append(order.getTotalAmount()).append("\n");
 report.append("Status: ").append(order.getStatus()).append("\n");
 report.append("\nITEMS:\n");
 order.getItems().forEach(item -> {
 report.append("- ").append(item.getProductCode())
 .append(" (").append(item.getProductName()).append(")")
 .append(" x").append(item.getQuantity())
 .append(" @ $").append(item.getUnitPrice())
 .append(" = $").append(item.getLineTotal()).append("\n");
 });

 exchange.getIn().setBody(report.toString());
 exchange.getIn().setHeader(Exchange.FILE_NAME,
 "order_" + order.getOrderNumber() + "_confirmation.txt");
 })
 .to("file:data/output")
 .log("Report generated: ${header.CamelFileName}");

 // Notification routes (file + MQ)
 from("direct:send-priority-notification")
 .routeId("priority-notification")
 .log("Sending priority notification for order: ${body.orderNumber}")
 .setBody(simple("PRIORITY: Order ${body.orderNumber} processed - Amount: $${body.totalAmount}"))
 .to("file:data/output?fileName=priority_notification_${date:now:yyyyMMdd_HHmmss}.txt")
 .to("jms:queue:order.notifications.priority");

 from("direct:send-notification")
 .routeId("standard-notification")
 .log("Sending standard notification for order: ${body.orderNumber}")
 .setBody(simple("Order ${body.orderNumber} processed - Amount: $${body.totalAmount}"))
 .to("file:data/output?fileName=notification_${date:now:yyyyMMdd_HHmmss}.txt")
 .to("jms:queue:order.notifications.standard");
 }

 private void configureDatabase() throws Exception {
 // Load database properties
 Properties props = new Properties();
 try (InputStream is = getClass().getClassLoader().getResourceAsStream("database.properties")) {
 if (is == null) {
 throw new IllegalStateException("database.properties not found in classpath");
 }
 props.load(is);
 }

 HikariConfig config = new HikariConfig();
 config.setJdbcUrl(props.getProperty("db.url"));
 config.setUsername(props.getProperty("db.username"));
 config.setPassword(props.getProperty("db.password"));
 config.setDriverClassName(props.getProperty("db.driver"));

 config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxPoolSize", "10")));
 config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
 config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
 config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
 config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000")));

 DataSource ds = new HikariDataSource(config);

 JdbcComponent jdbc = new JdbcComponent();
 jdbc.setDataSource(ds);

 getContext().addComponent("jdbc", jdbc);
 getContext().getRegistry().bind("dataSource", ds);

 logger.info("Database configured successfully");
 }
}
