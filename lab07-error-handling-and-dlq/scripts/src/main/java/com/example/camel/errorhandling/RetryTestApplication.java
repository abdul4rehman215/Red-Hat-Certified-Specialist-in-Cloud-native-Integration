package com.alnafi.camel.errorhandling;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

public class RetryTestApplication {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Add route builder
        context.addRoutes(new ErrorHandlingRouteBuilder());

        // Register the service bean
        context.getRegistry().bind("errorSimulationService", new ErrorSimulationService());

        // Create output directories
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get("output/success"));
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get("output/validation-errors"));

        context.start();

        ProducerTemplate template = context.createProducerTemplate();

        System.out.println("=== Testing Retry Logic ===");

        // Test 1: Message that will succeed after retries
        System.out.println("\n1. Testing message that succeeds after retries...");
        try {
            template.sendBody("direct:process-message", "RETRY_SUCCESS - This will fail twice then succeed");
            Thread.sleep(15000); // Wait for retries to complete
        } catch (Exception e) {
            System.out.println("Final exception: " + e.getMessage());
        }

        // Test 2: Validation error (no retries)
        System.out.println("\n2. Testing validation error (no retries)...");
        try {
            template.sendBody("direct:process-message", "VALIDATION_ERROR - Invalid format");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Validation exception: " + e.getMessage());
        }

        // Test 3: Successful message
        System.out.println("\n3. Testing successful message...");
        try {
            template.sendBody("direct:process-message", "SUCCESS - This message will process normally");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
        }

        Thread.sleep(5000);
        context.stop();
        System.out.println("Test completed. Check output directories for results.");
    }
}
