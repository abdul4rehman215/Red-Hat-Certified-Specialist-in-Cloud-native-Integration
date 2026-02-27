package com.alnafi.camel.eip.rest;

import com.alnafi.camel.eip.splitter.Order;
import com.alnafi.camel.eip.splitter.OrderItem;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import java.util.Arrays;
import java.util.List;

public class EIPRestRoute extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Configure REST
 restConfiguration()
 .component("jetty")
 .host("0.0.0.0")
 .port(8080)
 .bindingMode(RestBindingMode.json);

 // REST API endpoints
 rest("/api/eip")
 .description("EIP Demo REST API")

 .post("/order")
 .description("Submit an order for EIP processing")
 .type(Order.class)
 .to("direct:processRestOrder")

 .get("/sample")
 .description("Generate a sample order")
 .to("direct:generateSampleOrder");

 // Process REST order
 from("direct:processRestOrder")
 .routeId("rest-order-processor")
 .log("Received order via REST API: ${body}")
 .process(exchange -> {
 Order order = exchange.getIn().getBody(Order.class);
 exchange.getIn().setHeader("originalOrderId", order.getOrderId());
 })
 .to("direct:splitOrder")
 .setBody(constant("Order submitted for processing"));

 // Generate sample order
 from("direct:generateSampleOrder")
 .routeId("sample-order-generator")
 .process(exchange -> {
 List<OrderItem> items = Arrays.asList(
 new OrderItem("P010", "USB-C Cable", 2, 9.99),
 new OrderItem("P011", "Headset", 1, 49.99),
 new OrderItem("P012", "Webcam", 1, 59.99),
 new OrderItem("P013", "Laptop Stand", 1, 24.99)
 );

 Order order = new Order("ORD-REST-" + System.currentTimeMillis(), "CUST-REST-001", items);
 exchange.getIn().setBody(order);
 exchange.getIn().setHeader("originalOrderId", order.getOrderId());
 })
 .log("Generated sample order via REST: ${body}")
 .to("direct:splitOrder")
 .setBody(simple("${body.orderId}"));
 }
}
