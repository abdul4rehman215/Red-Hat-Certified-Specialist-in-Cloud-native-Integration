package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.LoggingLevel;

public class DeadLetterQueueRouteBuilder extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Configure error handler with dead letter queue
 errorHandler(deadLetterChannel("direct:dlq")
 .maximumRedeliveries(2)
 .redeliveryDelay(1000)
 .retryAttemptedLogLevel(LoggingLevel.WARN)
 .retriesExhaustedLogLevel(LoggingLevel.ERROR)
 .logRetryAttempted(true)
 .logExhausted(true)
 .useOriginalMessage());

 // Main processing route
 from("file:input/dlq?noop=true&delay=5000")
 .routeId("dlq-main-route")
 .log("Processing message with DLQ strategy: ${body}")
 .process(new FailureSimulatorProcessor(0.8)) // 80% failure rate
 .log("Message processed successfully: ${body}")
 .to("file:output/dlq/success");

 // Dead letter queue handler
 from("direct:dlq")
 .routeId("dlq-handler-route")
 .log("Message sent to DLQ: ${body}")
 .setHeader("FailureReason", simple("${exception.message}"))
 .setHeader("OriginalDestination", constant("file:output/dlq/success"))
 .setHeader("FailureTimestamp", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
 .to("file:output/dlq/failed");
 }
}
