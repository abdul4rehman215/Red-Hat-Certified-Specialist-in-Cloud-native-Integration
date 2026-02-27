package com.alnafi.camel.errorhandling;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ComprehensiveDLQTestApplication {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Configure JMS (in-memory broker)
        JmsConfig.configureJms(context);

        // Add routes
        context.addRoutes(new ComprehensiveErrorRoutes());

        // Register beans
        context.getRegistry().bind("advancedErrorSimulation", new AdvancedErrorSimulation());

        // Create output directories
        Files.createDirectories(Paths.get("output/success"));
        Files.createDirectories(Paths.get("output/dlq/runtime-errors"));
        Files.createDirectories(Paths.get("output/dlq/validation-errors"));
        Files.createDirectories(Paths.get("output/dlq/security-errors"));
        Files.createDirectories(Paths.get("output/dlq/unexpected-errors"));
        Files.createDirectories(Paths.get("output/dlq"));

        context.start();

        ProducerTemplate template = context.createProducerTemplate();

        System.out.println("=== Comprehensive DLQ Error Simulation ===");

        // 1) Transient DB error (should succeed on 3rd attempt)
        System.out.println("\n1) Sending DB_ERROR (should recover after retries)...");
        template.sendBody("jms:queue:test.input", "DB_ERROR - simulate transient DB issue");
        Thread.sleep(8000);

        // 2) Permanent failure (should go to DLQ.RuntimeErrors)
        System.out.println("\n2) Sending PERMANENT_FAILURE (should go to runtime DLQ)...");
        template.sendBody("jms:queue:test.input", "PERMANENT_FAILURE - cannot recover");
        Thread.sleep(8000);

        // 3) Invalid format (should go to DLQ.ValidationErrors immediately)
        System.out.println("\n3) Sending INVALID_FORMAT (validation DLQ, no retry)...");
        template.sendBody("jms:queue:test.input", "INVALID_FORMAT - broken payload");
        Thread.sleep(3000);

        // 4) Auth error (security DLQ, no retry)
        System.out.println("\n4) Sending AUTH_ERROR (security DLQ, no retry)...");
        template.sendBody("jms:queue:test.input", "AUTH_ERROR - invalid token");
        Thread.sleep(3000);

        // 5) Random error stress (mix of failures/success)
        System.out.println("\n5) Sending RANDOM_ERROR messages (stress test)...");
        for (int i = 1; i <= 6; i++) {
            template.sendBody("jms:queue:test.input", "RANDOM_ERROR - message " + i);
            Thread.sleep(800);
        }

        // Let DLQ statistics timer generate at least one report
        System.out.println("\nWaiting for DLQ stats report generation...");
        Thread.sleep(35000);

        context.stop();
        System.out.println("\nTest finished. Check output/ folders for results and DLQ stats.");
    }
}
