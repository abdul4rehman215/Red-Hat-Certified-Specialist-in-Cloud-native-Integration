package com.alnafi.camel.errorhandling;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class ErrorHandlingRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Global error handling with retry logic
        onException(RuntimeException.class)
                .log(LoggingLevel.WARN, "RuntimeException occurred: ${exception.message}")
                .maximumRedeliveries(3)
                .redeliveryDelay(2000) // 2 seconds delay between retries
                .backOffMultiplier(2)  // Exponential backoff
                .useExponentialBackOff()
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .retriesExhaustedLogLevel(LoggingLevel.ERROR)
                .handled(false); // Let the exception propagate after retries

        // Specific handling for IllegalArgumentException (validation errors)
        onException(IllegalArgumentException.class)
                .log(LoggingLevel.ERROR, "Validation error - no retry: ${exception.message}")
                .maximumRedeliveries(0) // No retries for validation errors
                .handled(true)
                .to("direct:validation-error-handler");

        // Main processing route
        from("direct:process-message")
                .routeId("main-processing-route")
                .log("Received message: ${body}")
                .bean(ErrorSimulationService.class, "processMessage")
                .log("Successfully processed: ${body}")
                .to("direct:success-handler");

        // Success handler
        from("direct:success-handler")
                .routeId("success-handler")
                .log("Message processing completed successfully: ${body}")
                .to("file:output/success?fileName=success-${date:now:yyyyMMdd-HHmmss}.txt");

        // Validation error handler
        from("direct:validation-error-handler")
                .routeId("validation-error-handler")
                .log("Handling validation error for message: ${body}")
                .setBody(simple("Validation failed for: ${body}"))
                .to("file:output/validation-errors?fileName=validation-error-${date:now:yyyyMMdd-HHmmss}.txt");
    }
}
