package com.alnafi.camel.eip.splitter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SplitterRoute extends RouteBuilder {

 private static final Logger log = LoggerFactory.getLogger(SplitterRoute.class);

 @Override
 public void configure() throws Exception {

 // Route to create sample order data
 from("timer:orderGenerator?period=10000&repeatCount=3")
 .routeId("order-generator")
 .log("Generating sample order...")
 .process(exchange -> {
 // Create sample order with multiple items
 List<OrderItem> items = Arrays.asList(
 new OrderItem("P001", "Laptop", 2, 999.99),
 new OrderItem("P002", "Mouse", 3, 29.99),
 new OrderItem("P003", "Keyboard", 2, 79.99),
 new OrderItem("P004", "Monitor", 1, 299.99)
 );

 Order order = new Order("ORD-" + System.currentTimeMillis(),
 "CUST-001", items);
 exchange.getIn().setBody(order);
 exchange.getIn().setHeader("originalOrderId", order.getOrderId());
 })
 .marshal().json(JsonLibrary.Jackson)
 .log("Created order: ${body}")
 .to("direct:splitOrder");

 // Splitter route - splits order into individual items
 from("direct:splitOrder")
 .routeId("order-splitter")
 .log("Splitting order into individual items...")
 .unmarshal().json(JsonLibrary.Jackson, Order.class)
 .split(simple("${body.items}"))
 .streaming() // Enable streaming for better memory usage
 .parallelProcessing() // Process splits in parallel
 .log("Processing item: ${body}")
 .process(exchange -> {
 OrderItem item = exchange.getIn().getBody(OrderItem.class);
 String originalOrderId = exchange.getIn().getHeader("originalOrderId", String.class);

 // Add correlation information for later aggregation
 exchange.getIn().setHeader("correlationId", originalOrderId);
 exchange.getIn().setHeader("itemId", item.getProductId());

 log.info("Split item - OrderID: {}, ProductID: {}, Product: {}",
 originalOrderId, item.getProductId(), item.getProductName());
 })
 .to("direct:processItem")
 .end()
 .log("Order splitting completed");

 // Individual item processing route
 from("direct:processItem")
 .routeId("item-processor")
 .log("Processing individual item: ${header.itemId}")
 .process(exchange -> {
 OrderItem item = exchange.getIn().getBody(OrderItem.class);

 // Simulate item processing (inventory check, pricing, etc.)
 Thread.sleep(1000); // Simulate processing time

 // Calculate total price for this item
 double totalPrice = item.getQuantity() * item.getPrice();
 exchange.getIn().setHeader("itemTotal", totalPrice);

 log.info("Processed item {} - Total: ${}", item.getProductId(), totalPrice);
 })
 .to("direct:aggregateItems");
 }
}
