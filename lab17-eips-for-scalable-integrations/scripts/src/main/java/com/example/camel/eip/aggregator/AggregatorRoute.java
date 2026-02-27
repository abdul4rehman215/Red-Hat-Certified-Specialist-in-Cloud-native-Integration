package com.alnafi.camel.eip.aggregator;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregatorRoute extends RouteBuilder {

 private static final Logger log = LoggerFactory.getLogger(AggregatorRoute.class);

 @Override
 public void configure() throws Exception {

 // Aggregator route - combines split items back into complete orders
 from("direct:aggregateItems")
 .routeId("item-aggregator")
 .log("Aggregating item: ${header.itemId} for order: ${header.correlationId}")
 .aggregate(header("correlationId"), new OrderAggregationStrategy())
 .completionTimeout(5000) // Complete aggregation after 5 seconds
 .completionSize(4) // Complete when 4 items are aggregated (matches our sample)
 .log("Aggregation completed for order: ${header.correlationId}")
 .process(exchange -> {
 AggregatedOrder order = exchange.getIn().getBody(AggregatedOrder.class);
 order.setAggregationEndTime(System.currentTimeMillis());

 log.info("Order aggregation completed: {}", order);
 log.info("Items aggregated: {}", order.getItemCount());
 log.info("Total amount: ${}", order.getTotalAmount());
 log.info("Aggregation took: {}ms", order.getAggregationDuration());
 })
 .to("direct:finalizeOrder")
 .end();

 // Final order processing route
 from("direct:finalizeOrder")
 .routeId("order-finalizer")
 .log("Finalizing aggregated order: ${body.orderId}")
 .process(exchange -> {
 AggregatedOrder order = exchange.getIn().getBody(AggregatedOrder.class);

 // Apply business rules (discounts, taxes, etc.)
 if (order.getTotalAmount() > 1000) {
 double discount = order.getTotalAmount() * 0.1; // 10% discount
 order.setTotalAmount(order.getTotalAmount() - discount);
 log.info("Applied 10% discount: ${}", discount);
 }

 // Add tax
 double tax = order.getTotalAmount() * 0.08; // 8% tax
 order.setTotalAmount(order.getTotalAmount() + tax);

 log.info("Final order total with tax: ${}", order.getTotalAmount());
 })
 .to("direct:sendToRecipients");
 }
}
