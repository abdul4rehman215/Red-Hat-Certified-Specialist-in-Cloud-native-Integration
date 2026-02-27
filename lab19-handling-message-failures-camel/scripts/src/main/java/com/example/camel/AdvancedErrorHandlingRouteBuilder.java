package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.LoggingLevel;

public class AdvancedErrorHandlingRouteBuilder extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Global exception handling
 onException(IllegalArgumentException.class)
 .handled(true)
 .log("Validation error occurred: ${exception.message}")
 .setHeader("ErrorType", constant("VALIDATION_ERROR"))
 .process(new FallbackProcessor())
 .to("file:output/advanced/validation-errors");

 onException(RuntimeException.class)
 .handled(false)
 .maximumRedeliveries(2)
 .redeliveryDelay(1000)
 .log("Runtime error, retrying: ${exception.message}")
 .to("file:output/advanced/runtime-errors");

 onException(Exception.class)
 .handled(true)
 .log("General exception caught: ${exception.message}")
 .setHeader("ErrorType", constant("GENERAL_ERROR"))
 .to("file:output/advanced/general-errors");

 // Main processing route
 from("file:input/advanced?noop=true&delay=5000")
 .routeId("advanced-error-handling-route")
 .log("Processing message with advanced error handling: ${body}")
 .choice()
 .when(body().contains("VALIDATE"))
 .process(exchange -> {
 throw new IllegalArgumentException("Validation failed for: " +
 exchange.getIn().getBody(String.class));
 })
 .when(body().contains("RUNTIME"))
 .process(exchange -> {
 throw new RuntimeException("Runtime error for: " +
 exchange.getIn().getBody(String.class));
 })
 .when(body().contains("GENERAL"))
 .process(exchange -> {
 throw new Exception("General error for: " +
 exchange.getIn().getBody(String.class));
 })
 .otherwise()
 .process(new FailureSimulatorProcessor(0.3)) // 30% failure rate
 .end()
 .log("Message processed successfully: ${body}")
 .to("file:output/advanced/success");
 }
}
