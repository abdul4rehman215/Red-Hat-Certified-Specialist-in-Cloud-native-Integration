package com.example.routing;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdvancedDynamicRouting extends RouteBuilder {

 private final ObjectMapper objectMapper = new ObjectMapper();

 @Override
 public void configure() throws Exception {

 // Route 0: Basic priority routing (added so test script works end-to-end)
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

 // Route 0.1: Customer routing (added so test script works end-to-end)
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

 // Route 1: Category-based dynamic routing using external configuration
 from("jetty:http://localhost:8080/products")
 .log("Received product order: ${body}")
 .process(exchange -> {
 String body = exchange.getIn().getBody(String.class);
 JsonNode json = objectMapper.readTree(body);
 String category = json.get("category").asText();
 String targetRoute = ConfigurationService.getRouteForCategory(category);
 exchange.getIn().setHeader("targetRoute", targetRoute);
 exchange.getIn().setHeader("category", category);
 })
 .recipientList(header("targetRoute"))
 .setBody(constant("Product order routed successfully"));

 // Route 2: Multi-criteria dynamic routing
 from("jetty:http://localhost:8080/complex-orders")
 .log("Received complex order: ${body}")
 .process(exchange -> {
 String body = exchange.getIn().getBody(String.class);
 JsonNode json = objectMapper.readTree(body);

 String priority = json.get("priority").asText();
 double amount = json.get("amount").asDouble();
 String region = json.get("region").asText();
 String category = json.get("category").asText();

 // Set routing headers based on external configuration
 exchange.getIn().setHeader("priorityWeight",
 ConfigurationService.getPriorityWeight(priority));
 exchange.getIn().setHeader("isHighValue",
 ConfigurationService.isHighValueOrder(amount));
 exchange.getIn().setHeader("regionQueue",
 ConfigurationService.getProcessingQueue(region));
 exchange.getIn().setHeader("categoryRoute",
 ConfigurationService.getRouteForCategory(category));
 })
 .choice()
 .when(header("isHighValue").isEqualTo(true))
 .log("High value order - routing to premium processing")
 .to("direct:premiumProcessing")
 .when(header("priorityWeight").isLessThan(3))
 .log("High priority order - routing to express processing")
 .to("direct:expressProcessing")
 .otherwise()
 .log("Standard order - routing based on region and category")
 .recipientList(simple("${header.regionQueue},${header.categoryRoute}"))
 .end()
 .setBody(constant("Complex order processed"));

 // Route 3: Time-based dynamic routing (using external config values)
 from("jetty:http://localhost:8080/time-sensitive")
 .log("Received time-sensitive request: ${body}")
 .choice()
 .when(simple("${date:now:HH} >= " + ConfigurationService.getBusinessHoursStart()
         + " && ${date:now:HH} < " + ConfigurationService.getBusinessHoursEnd()))
 .log("Business hours - routing to regular processing")
 .to("direct:businessHoursProcessor")
 .when(simple("${date:now:HH} >= " + ConfigurationService.getBusinessHoursEnd()
         + " && ${date:now:HH} < " + ConfigurationService.getEveningHoursEnd()))
 .log("Evening hours - routing to evening shift")
 .to("direct:eveningShiftProcessor")
 .otherwise()
 .log("Off hours - routing to automated processing")
 .to("direct:automatedProcessor")
 .end()
 .setBody(constant("Time-sensitive request processed"));

 // Category processors
 from("direct:electronicsProcessor")
 .log("Processing electronics order")
 .setHeader("ProcessingDepartment", constant("Electronics"))
 .setHeader("EstimatedDelivery", constant("2-3 business days"));

 from("direct:clothingProcessor")
 .log("Processing clothing order")
 .setHeader("ProcessingDepartment", constant("Clothing"))
 .setHeader("EstimatedDelivery", constant("3-5 business days"));

 from("direct:booksProcessor")
 .log("Processing books order")
 .setHeader("ProcessingDepartment", constant("Books"))
 .setHeader("EstimatedDelivery", constant("1-2 business days"));

 from("direct:foodProcessor")
 .log("Processing food order")
 .setHeader("ProcessingDepartment", constant("Food"))
 .setHeader("EstimatedDelivery", constant("Same day"));

 from("direct:defaultCategoryProcessor")
 .log("Processing general order")
 .setHeader("ProcessingDepartment", constant("General"))
 .setHeader("EstimatedDelivery", constant("3-7 business days"));

 // Priority processors
 from("direct:premiumProcessing")
 .log("Premium processing for high-value order")
 .setHeader("ProcessingType", constant("Premium"))
 .setHeader("AssignedAgent", constant("Senior Agent"));

 from("direct:expressProcessing")
 .log("Express processing for high-priority order")
 .setHeader("ProcessingType", constant("Express"))
 .setHeader("AssignedAgent", constant("Express Team"));

 // Regional queues
 from("direct:northRegionQueue")
 .log("Processing in North region")
 .setHeader("ProcessingRegion", constant("North"));

 from("direct:southRegionQueue")
 .log("Processing in South region")
 .setHeader("ProcessingRegion", constant("South"));

 from("direct:eastRegionQueue")
 .log("Processing in East region")
 .setHeader("ProcessingRegion", constant("East"));

 from("direct:westRegionQueue")
 .log("Processing in West region")
 .setHeader("ProcessingRegion", constant("West"));

 from("direct:defaultRegionQueue")
 .log("Processing in default region")
 .setHeader("ProcessingRegion", constant("Central"));

 // Time-based processors
 from("direct:businessHoursProcessor")
 .log("Business hours processing")
 .setHeader("ProcessingShift", constant("Day Shift"));

 from("direct:eveningShiftProcessor")
 .log("Evening shift processing")
 .setHeader("ProcessingShift", constant("Evening Shift"));

 from("direct:automatedProcessor")
 .log("Automated processing")
 .setHeader("ProcessingShift", constant("Automated"));

 // Processor routes for orders
 from("direct:highPriorityProcessor")
 .log("HIGH Priority: Expedited processing")
 .delay(1000)
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
 main.addRouteBuilder(new AdvancedDynamicRouting());
 main.run(args);
 }
}
