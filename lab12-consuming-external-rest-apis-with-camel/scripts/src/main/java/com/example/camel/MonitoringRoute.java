// src/main/java/com/example/camel/MonitoringRoute.java
package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class MonitoringRoute extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Monitoring route for API performance
 from("timer://monitor?period=30000")
 .routeId("monitoring-route")
 .log("=== API MONITORING CHECK ===")
 .process(exchange -> {
     long startTime = System.currentTimeMillis();
     exchange.setProperty("RequestStartTime", startTime);
 })
 .setHeader("StartTime", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
 .to("https://jsonplaceholder.typicode.com/posts/1?bridgeEndpoint=true")
 .process(exchange -> {
     long startTime = exchange.getProperty("RequestStartTime", Long.class);
     long responseTime = System.currentTimeMillis() - startTime;
     exchange.setProperty("ResponseTimeMs", responseTime);
 })
 .choice()
 .when(header("CamelHttpResponseCode").isEqualTo(200))
 .log("API Health Check: HEALTHY")
 .log("Response time (ms): ${exchangeProperty.ResponseTimeMs}")
 .otherwise()
 .log("API Health Check: UNHEALTHY - Status: ${header.CamelHttpResponseCode}")
 .end()
 .log("=== MONITORING CHECK COMPLETE ===");

 // Route to collect API statistics
 from("timer://stats?period=60000")
 .routeId("statistics-route")
 .log("=== COLLECTING API STATISTICS ===")
 .process(exchange -> {
 // Simulate collecting statistics
 System.out.println("Total API calls made: " + getContext().getRoutes().size());
 System.out.println("Active routes: " + getContext().getRoutes().size());
 System.out.println("Context status: " + getContext().getStatus());
 })
 .log("Statistics collection completed");
 }
}
