package com.example.routing;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class DynamicRoutingExample extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Route 1: Basic dynamic routing based on message content
 from("jetty:http://localhost:8080/orders")
 .log("Received order: ${body}")
 .choice()
 .when(jsonpath("$.priority[?(@ == 'HIGH')]"))
 .log("Processing HIGH priority order")
 .to("direct:highPriorityProcessor")
 .when(jsonpath("$.priority[?(@ == 'MEDIUM')]"))
 .log("Processing MEDIUM priority order")
 .to("direct:mediumPriorityProcessor")
 .when(jsonpath("$.priority[?(@ == 'LOW')]"))
 .log("Processing LOW priority order")
 .to("direct:lowPriorityProcessor")
 .otherwise()
 .log("Processing DEFAULT priority order")
 .to("direct:defaultProcessor")
 .end()
 .setBody(constant("Order processed successfully"));

 // Route 2: Dynamic routing based on customer type
 from("jetty:http://localhost:8080/customers")
 .log("Received customer request: ${body}")
 .choice()
 .when(jsonpath("$.customerType[?(@ == 'PREMIUM')]"))
 .log("Routing to premium customer service")
 .to("direct:premiumService")
 .when(jsonpath("$.customerType[?(@ == 'STANDARD')]"))
 .log("Routing to standard customer service")
 .to("direct:standardService")
 .when(jsonpath("$.customerType[?(@ == 'BASIC')]"))
 .log("Routing to basic customer service")
 .to("direct:basicService")
 .otherwise()
 .log("Routing to default customer service")
 .to("direct:defaultService")
 .end()
 .setBody(constant("Customer request processed"));

 // Processor routes for orders
 from("direct:highPriorityProcessor")
 .log("HIGH Priority: Expedited processing")
 .delay(1000) // Simulate processing time
 .setHeader("ProcessingTime", constant("1 second"))
 .setHeader("Priority", constant("HIGH"));

 from("direct:mediumPriorityProcessor")
 .log("MEDIUM Priority: Standard processing")
 .delay(3000)
 .setHeader("ProcessingTime", constant("3 seconds"))
 .setHeader("Priority", constant("MEDIUM"));

 from("direct:lowPriorityProcessor")
 .log("LOW Priority: Batch processing")
 .delay(5000)
 .setHeader("ProcessingTime", constant("5 seconds"))
 .setHeader("Priority", constant("LOW"));

 from("direct:defaultProcessor")
 .log("DEFAULT Priority: Standard processing")
 .delay(3000)
 .setHeader("ProcessingTime", constant("3 seconds"))
 .setHeader("Priority", constant("DEFAULT"));

 // Service routes for customers
 from("direct:premiumService")
 .log("Premium Service: VIP treatment")
 .setHeader("ServiceLevel", constant("PREMIUM"))
 .setHeader("ResponseTime", constant("Immediate"));

 from("direct:standardService")
 .log("Standard Service: Regular treatment")
 .setHeader("ServiceLevel", constant("STANDARD"))
 .setHeader("ResponseTime", constant("Within 24 hours"));

 from("direct:basicService")
 .log("Basic Service: Standard treatment")
 .setHeader("ServiceLevel", constant("BASIC"))
 .setHeader("ResponseTime", constant("Within 48 hours"));

 from("direct:defaultService")
 .log("Default Service: Standard treatment")
 .setHeader("ServiceLevel", constant("DEFAULT"))
 .setHeader("ResponseTime", constant("Within 24 hours"));
 }

 public static void main(String[] args) throws Exception {
 Main main = new Main();
 main.addRouteBuilder(new DynamicRoutingExample());
 main.run(args);
 }
}
