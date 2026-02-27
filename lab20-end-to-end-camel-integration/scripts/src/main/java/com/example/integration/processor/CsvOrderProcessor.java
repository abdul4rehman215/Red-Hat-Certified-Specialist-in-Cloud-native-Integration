package com.alnafi.integration.processor;

import com.alnafi.integration.model.Order;
import com.alnafi.integration.model.OrderItem;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvOrderProcessor implements Processor {
 private static final Logger logger = LoggerFactory.getLogger(CsvOrderProcessor.class);

 @Override
 public void process(Exchange exchange) throws Exception {
 @SuppressWarnings("unchecked")
 List<Map<String, String>> csvData = exchange.getIn().getBody(List.class);

 if (csvData == null || csvData.isEmpty()) {
 throw new IllegalArgumentException("CSV data is empty");
 }

 // Group CSV rows by order number
 String orderNumber = null;
 String customerCode = null;
 List<OrderItem> items = new ArrayList<>();

 for (Map<String, String> row : csvData) {
 if (orderNumber == null) {
 orderNumber = row.get("order_number");
 customerCode = row.get("customer_code");
 }

 // Create order item
 OrderItem item = new OrderItem();
 item.setProductCode(row.get("product_code"));
 item.setQuantity(Integer.parseInt(row.get("quantity")));
 item.setUnitPrice(new BigDecimal(row.get("unit_price")));
 item.calculateLineTotal();

 items.add(item);
 }

 // Create order
 Order order = new Order(orderNumber, customerCode);
 order.setItems(items);

 // Calculate total amount
 BigDecimal totalAmount = items.stream()
 .map(OrderItem::getLineTotal)
 .reduce(BigDecimal.ZERO, BigDecimal::add);
 order.setTotalAmount(totalAmount);

 logger.info("Processed order: {} with {} items, total: {}",
 orderNumber, items.size(), totalAmount);

 exchange.getIn().setBody(order);
 }
}
