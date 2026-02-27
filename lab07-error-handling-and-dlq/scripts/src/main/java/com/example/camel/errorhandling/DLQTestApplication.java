package com.alnafi.camel.errorhandling;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DLQTestApplication {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Configure JMS
        JmsConfig.configureJms(context);

        // Add route builder
        context.addRoutes(new DLQRouteBuilder());

        // Register the service bean
        context.getRegistry().bind("errorSimulationService", new ErrorSimulationService());

        // Create output directories
        Files.createDirectories(Paths.get("output/success"));
        Files.createDirectories(Paths.get("output/dlq/runtime-errors"));
        Files.createDirectories(Paths.get("output/dlq/validation-errors"));
        Files.createDirectories(Paths.get("output/dlq/unexpected-errors"));

        context.start();

        ProducerTemplate template = context.createProducerTemplate();

        System.out.println("=== Testing Dead Letter Queue Functionality ===");

        // Test 1: Message that will eventually go to DLQ after retries
        System.out.println("\n1. Testing message that goes to DLQ after retries...");
        template.sendBody("jms:queue:input.messages", "NETWORK_ERROR - This will fail and go to DLQ");
        Thread.sleep(10000); // Wait for retries and DLQ processing

        // Test 2: Validation error (immediate DLQ)
        System.out.println("\n2. Testing validation error (immediate DLQ)...");
        template.sendBody("jms:queue:input.messages", "VALIDATION_ERROR - Invalid format");
        Thread.sleep(3000);

        // Test 3: Successful message
        System.out.println("\n3. Testing successful message...");
        template.sendBody("jms:queue:input.messages", "SUCCESS - This will process normally");
        Thread.sleep(3000);

        // Test 4: Timeout error
        System.out.println("\n4. Testing timeout error...");
        template.sendBody("jms:queue:input.messages", "TIMEOUT_ERROR - This will timeout and go to DLQ");
        Thread.sleep(10000);

        // Test 5: Multiple messages for load testing
        System.out.println("\n5. Sending multiple test messages...");
        for (int i = 1; i <= 5; i++) {
            if (i % 2 == 0) {
                template.sendBody("jms:queue:input.messages", "SUCCESS - Message " + i);
            } else {
                template.sendBody("jms:queue:input.messages", "NETWORK_ERROR - Failed message " + i);
            }
            Thread.sleep(1000);
        }

        // Wait for all processing to complete
        Thread.sleep(15000);

        // Display results
        displayResults();

        context.stop();
        System.out.println("\nDLQ test completed. Check output directories for detailed results.");
    }

    private static void displayResults() {
        System.out.println("\n=== Processing Results ===");

        try {
            System.out.println("Success files: " +
                    Files.list(Paths.get("output/success")).count());
            System.out.println("Runtime error DLQ files: " +
                    Files.list(Paths.get("output/dlq/runtime-errors")).count());
            System.out.println("Validation error DLQ files: " +
                    Files.list(Paths.get("output/dlq/validation-errors")).count());
            System.out.println("Unexpected error DLQ files: " +
                    Files.list(Paths.get("output/dlq/unexpected-errors")).count());
        } catch (Exception e) {
            System.err.println("Error reading results: " + e.getMessage());
        }
    }
}
