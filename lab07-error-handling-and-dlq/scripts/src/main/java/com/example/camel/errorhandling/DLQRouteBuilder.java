package com.alnafi.camel.errorhandling;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class DLQRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Configure Dead Letter Channel for RuntimeException
        onException(RuntimeException.class)
                .log(LoggingLevel.WARN, "RuntimeException occurred: ${exception.message}")
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .backOffMultiplier(2)
                .useExponentialBackOff()
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .retriesExhaustedLogLevel(LoggingLevel.ERROR)
                .deadLetterUri("jms:queue:DLQ.RuntimeErrors")
                .deadLetterHandleNewException(false)
                .log(LoggingLevel.ERROR,
                        "Message sent to DLQ after ${property.CamelRedeliveryCounter} retries: ${body}")
                .handled(true);

        // Configure Dead Letter Channel for IllegalArgumentException
        onException(IllegalArgumentException.class)
                .log(LoggingLevel.ERROR, "Validation error: ${exception.message}")
                .maximumRedeliveries(0)
                .deadLetterUri("jms:queue:DLQ.ValidationErrors")
                .log(LoggingLevel.ERROR, "Validation error sent to DLQ: ${body}")
                .handled(true);

        // Configure Dead Letter Channel for any other Exception
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Unexpected exception: ${exception.message}")
                .maximumRedeliveries(2)
                .redeliveryDelay(500)
                .deadLetterUri("jms:queue:DLQ.UnexpectedErrors")
                .log(LoggingLevel.ERROR, "Unexpected error sent to DLQ: ${body}")
                .handled(true);

        // Input queue route
        from("jms:queue:input.messages")
                .routeId("input-message-processor")
                .log("Processing message from input queue: ${body}")
                .bean(ErrorSimulationService.class, "processMessage")
                .log("Message processed successfully: ${body}")
                .to("jms:queue:output.success");

        // Success queue consumer (for demonstration)
        from("jms:queue:output.success")
                .routeId("success-consumer")
                .log("Success: ${body}")
                .to("file:output/success?fileName=success-${date:now:yyyyMMdd-HHmmss-SSS}.txt");

        // DLQ monitoring routes
        from("jms:queue:DLQ.RuntimeErrors")
                .routeId("dlq-runtime-monitor")
                .log(LoggingLevel.ERROR, "DLQ Runtime Error: ${body}")
                .setHeader("ErrorType", constant("RuntimeError"))
                .setHeader("Timestamp", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .to("file:output/dlq/runtime-errors?fileName=runtime-error-${date:now:yyyyMMdd-HHmmss-SSS}.txt");

        from("jms:queue:DLQ.ValidationErrors")
                .routeId("dlq-validation-monitor")
                .log(LoggingLevel.ERROR, "DLQ Validation Error: ${body}")
                .setHeader("ErrorType", constant("ValidationError"))
                .setHeader("Timestamp", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .to("file:output/dlq/validation-errors?fileName=validation-error-${date:now:yyyyMMdd-HHmmssSSS}.txt");

        from("jms:queue:DLQ.UnexpectedErrors")
                .routeId("dlq-unexpected-monitor")
                .log(LoggingLevel.ERROR, "DLQ Unexpected Error: ${body}")
                .setHeader("ErrorType", constant("UnexpectedError"))
                .setHeader("Timestamp", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .to("file:output/dlq/unexpected-errors?fileName=unexpected-error-${date:now:yyyyMMdd-HHmmssSSS}.txt");
    }
}
