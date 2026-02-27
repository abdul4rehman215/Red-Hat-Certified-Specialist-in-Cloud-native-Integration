package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.LoggingLevel;

public class RetryRouteBuilder extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Configure error handler with retry policy
 errorHandler(defaultErrorHandler()
 .maximumRedeliveries(3)
 .redeliveryDelay(2000)
 .retryAttemptedLogLevel(LoggingLevel.WARN)
 .retriesExhaustedLogLevel(LoggingLevel.ERROR)
 .logRetryAttempted(true)
 .logExhausted(true));

 // Route with retry strategy
 from("file:input/retry?noop=true&delay=5000")
 .routeId("retry-route")
 .log("Processing message with retry strategy: ${body}")
 .process(new FailureSimulatorProcessor(0.7)) // 70% failure rate
 .log("Message processed successfully: ${body}")
 .to("file:output/retry");
 }
}
