package com.alnafi.camel.eip.recipientlist;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipientListRoute extends RouteBuilder {

 private static final Logger log = LoggerFactory.getLogger(RecipientListRoute.class);

 @Override
 public void configure() throws Exception {

 // Recipient List route - dynamically routes messages to multiple endpoints
 from("direct:sendToRecipients")
 .routeId("recipient-list-router")
 .log("Determining recipients for order: ${body.orderId}")
 .recipientList(method(OrderRecipientListResolver.class, "resolveRecipients"))
 .parallelProcessing() // Process recipients in parallel
 .stopOnException() // Stop if any recipient fails
 .timeout(10000) // 10 second timeout
 .log("Message sent to all recipients for order: ${body.orderId}");

 // Order Processing System
 from("direct:orderProcessing")
 .routeId("order-processing-system")
 .log("ORDER PROCESSING: Processing order ${body.orderId}")
 .process(exchange -> {
 Thread.sleep(500); // Simulate processing time
 log.info("Order {} has been processed and saved to database",
 exchange.getIn().getBody().toString());
 });

 // Inventory Update System
 from("direct:inventoryUpdate")
 .routeId("inventory-update-system")
 .log("INVENTORY: Updating inventory for order ${body.orderId}")
 .process(exchange -> {
 Thread.sleep(300); // Simulate processing time
 log.info("Inventory updated for order {}",
 exchange.getIn().getBody().toString());
 });

 // Shipping Arrangement System
 from("direct:shippingArrangement")
 .routeId("shipping-arrangement-system")
 .log("SHIPPING: Arranging shipping for order ${body.orderId}")
 .process(exchange -> {
 Thread.sleep(700); // Simulate processing time
 log.info("Shipping arranged for order {}",
 exchange.getIn().getBody().toString());
 });

 // Finance Approval System
 from("direct:financeApproval")
 .routeId("finance-approval-system")
 .log("FINANCE: Processing finance approval for order ${body.orderId}")
 .process(exchange -> {
 Thread.sleep(1000); // Simulate processing time
 log.info("Finance approval completed for order {}",
 exchange.getIn().getBody().toString());
 });

 // Customer Notification System
 from("direct:customerNotification")
 .routeId("customer-notification-system")
 .log("NOTIFICATION: Sending notification for order ${body.orderId}")
 .process(exchange -> {
 Thread.sleep(200); // Simulate processing time
 log.info("Customer notification sent for order {}",
 exchange.getIn().getBody().toString());
 });

 // Analytics Processing System
 from("direct:analyticsProcessing")
 .routeId("analytics-processing-system")
 .log("ANALYTICS: Processing analytics data for order ${body.orderId}")
 .process(exchange -> {
 Thread.sleep(400); // Simulate processing time
 log.info("Analytics data processed for order {}",
 exchange.getIn().getBody().toString());
 });
 }
}
