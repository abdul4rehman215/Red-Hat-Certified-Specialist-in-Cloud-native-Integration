package com.alnafi.camel.eip.recipientlist;

import com.alnafi.camel.eip.aggregator.AggregatedOrder;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;

public class OrderRecipientListResolver {

 public static String resolveRecipients(Exchange exchange) {
 AggregatedOrder order = exchange.getIn().getBody(AggregatedOrder.class);
 List<String> recipients = new ArrayList<>();

 // Always send to order processing system
 recipients.add("direct:orderProcessing");

 // Send to inventory system if order contains physical items
 if (containsPhysicalItems(order)) {
 recipients.add("direct:inventoryUpdate");
 }

 // Send to shipping system for orders over $50
 if (order.getTotalAmount() > 50) {
 recipients.add("direct:shippingArrangement");
 }

 // Send to finance system for orders over $500
 if (order.getTotalAmount() > 500) {
 recipients.add("direct:financeApproval");
 }

 // Send to customer notification system
 recipients.add("direct:customerNotification");

 // Send to analytics system
 recipients.add("direct:analyticsProcessing");

 String recipientList = String.join(",", recipients);
 System.out.println("Recipients for order " + order.getOrderId() + ": " + recipientList);

 return recipientList;
 }

 private static boolean containsPhysicalItems(AggregatedOrder order) {
 // Simple logic - assume all items are physical for this demo
 return order.getItemCount() > 0;
 }
}
