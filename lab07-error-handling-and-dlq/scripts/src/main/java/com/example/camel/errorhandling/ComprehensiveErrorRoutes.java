package com.alnafi.camel.errorhandling;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ComprehensiveErrorRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Handle SecurityException (authentication errors) - no retries
        onException(SecurityException.class)
                .log(LoggingLevel.ERROR, "Security error - no retries: ${exception.message}")
                .maximumRedeliveries(0)
                .deadLetterUri("jms:queue:DLQ.SecurityErrors")
                .setHeader("ErrorCategory", constant("SECURITY"))
                .setHeader("RetryCount", simple("${property.CamelRedeliveryCounter}"))
                .setHeader("FailureTime", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .log(LoggingLevel.ERROR, "Security error sent to DLQ: ${body}")
                .handled(true);

        // Handle IllegalArgumentException (validation errors) - no retries
        onException(IllegalArgumentException.class)
                .log(LoggingLevel.ERROR, "Validation error - no retries: ${exception.message}")
                .maximumRedeliveries(0)
                .deadLetterUri("jms:queue:DLQ.ValidationErrors")
                .setHeader("ErrorCategory", constant("VALIDATION"))
                .setHeader("RetryCount", simple("${property.CamelRedeliveryCounter}"))
                .setHeader("FailureTime", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .log(LoggingLevel.ERROR, "Validation error sent to DLQ: ${body}")
                .handled(true);

        // Handle RuntimeException (transient errors) - with retries
        onException(RuntimeException.class)
                .log(LoggingLevel.WARN,
                        "Runtime error (attempt ${property.CamelRedeliveryCounter}): ${exception.message}")
                .maximumRedeliveries(4)
                .redeliveryDelay(1000)
                .backOffMultiplier(1.5)
                .useExponentialBackOff()
                .maximumRedeliveryDelay(10000)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .retriesExhaustedLogLevel(LoggingLevel.ERROR)
                .deadLetterUri("jms:queue:DLQ.RuntimeErrors")
                .setHeader("ErrorCategory", constant("RUNTIME"))
                .setHeader("RetryCount", simple("${property.CamelRedeliveryCounter}"))
                .setHeader("FailureTime", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .log(LoggingLevel.ERROR,
                        "Runtime error sent to DLQ after ${property.CamelRedeliveryCounter} retries: ${body}")
                .handled(true);

        // Main processing route
        from("jms:queue:test.input")
                .routeId("comprehensive-error-test")
                .log("Processing message: ${body}")
                .setHeader("ProcessingStartTime", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .bean(AdvancedErrorSimulation.class, "processComplexMessage")
                .log("Successfully processed: ${body}")
                .setHeader("ProcessingEndTime", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                .to("jms:queue:test.success");

        // Success message handler
        from("jms:queue:test.success")
                .routeId("success-handler")
                .log("Success: ${body}")
                .to("file:output/success?fileName=success-${date:now:yyyyMMdd-HHmmss-SSS}.txt");

        // DLQ Monitoring and Analysis Routes
        from("jms:queue:DLQ.SecurityErrors")
                .routeId("dlq-security-monitor")
                .log(LoggingLevel.ERROR, "DLQ Security Error: ${body}")
                .setBody(simple("Security Error at ${header.FailureTime}\nMessage: ${body}\nRetries: ${header.RetryCount}\n"))
                .to("file:output/dlq/security-errors?fileName=security-error-${date:now:yyyyMMdd-HHmmss-SSS}.txt");

        from("jms:queue:DLQ.ValidationErrors")
                .routeId("dlq-validation-monitor")
                .log(LoggingLevel.ERROR, "DLQ Validation Error: ${body}")
                .setBody(simple("Validation Error at ${header.FailureTime}\nMessage: ${body}\nRetries: ${header.RetryCount}\n"))
                .to("file:output/dlq/validation-errors?fileName=validation-error-${date:now:yyyyMMdd-HHmmss-SSS}.txt");

        from("jms:queue:DLQ.RuntimeErrors")
                .routeId("dlq-runtime-monitor")
                .log(LoggingLevel.ERROR, "DLQ Runtime Error: ${body}")
                .setBody(simple("Runtime Error at ${header.FailureTime}\nMessage: ${body}\nRetries: ${header.RetryCount}\n"))
                .to("file:output/dlq/runtime-errors?fileName=runtime-error-${date:now:yyyyMMdd-HHmmss-SSS}.txt");

        // --- Completed missing lab section: DLQ Statistics (every 30s) ---
        from("timer://dlq-stats?period=30000")
                .routeId("dlq-statistics")
                .setBody(constant("Generating DLQ statistics..."))
                .to("direct:generate-dlq-stats");

        from("direct:generate-dlq-stats")
                .routeId("dlq-stats-generator")
                .process(exchange -> {
                    long runtime = countFilesSafe("output/dlq/runtime-errors");
                    long validation = countFilesSafe("output/dlq/validation-errors");
                    long security = countFilesSafe("output/dlq/security-errors");
                    long unexpected = countFilesSafe("output/dlq/unexpected-errors");

                    String report = ""
                            + "=== DLQ Statistics Report ===\n"
                            + "RuntimeErrors   : " + runtime + "\n"
                            + "ValidationErrors: " + validation + "\n"
                            + "SecurityErrors  : " + security + "\n"
                            + "UnexpectedErrors: " + unexpected + "\n"
                            + "GeneratedAt     : " + java.time.LocalDateTime.now() + "\n";

                    exchange.getIn().setBody(report);
                })
                .log(LoggingLevel.INFO, "${body}")
                .to("file:output/dlq?fileName=dlq-stats-${date:now:yyyyMMdd-HHmmss}.txt");

    }

    private static long countFilesSafe(String dir) {
        try {
            if (!Files.exists(Paths.get(dir))) {
                return 0;
            }
            return Files.list(Paths.get(dir)).count();
        } catch (Exception e) {
            return 0;
        }
    }
}
