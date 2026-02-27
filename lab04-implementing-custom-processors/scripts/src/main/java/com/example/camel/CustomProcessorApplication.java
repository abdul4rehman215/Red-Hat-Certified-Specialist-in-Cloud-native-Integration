package com.alnafi.camel.lab4;

import com.alnafi.camel.lab4.routes.CustomProcessorRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Main application class for custom processor demonstration
*/
public class CustomProcessorApplication {

    private static final Logger logger = LoggerFactory.getLogger(CustomProcessorApplication.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting Custom Processor Lab Application");

        // Create Camel context
        CamelContext camelContext = new DefaultCamelContext();

        try {
            // Add our route builder
            camelContext.addRoutes(new CustomProcessorRouteBuilder());

            // Start the context
            camelContext.start();
            logger.info("Camel context started successfully");

            // Get producer template for sending messages
            ProducerTemplate producer = camelContext.createProducerTemplate();

            // Test the routes with different messages
            testRoutes(producer);

            // Keep the application running for timer-based route
            logger.info("Application running... Timer route will execute automatically");
            logger.info("Press Ctrl+C to stop the application");

            // Keep running for 60 seconds to see timer route execution
            Thread.sleep(60000);

        } finally {
            // Stop the context
            camelContext.stop();
            logger.info("Camel context stopped");
        }
    }

    /**
     * Test all routes with different message types
     */
    private static void testRoutes(ProducerTemplate producer) throws Exception {
        logger.info("=== Starting Route Testing ===");

        // Test 1: Basic transformation route
        logger.info("\n--- Test 1: Basic Transformation ---");
        producer.sendBody("direct:transform", "hello world from camel");

        Thread.sleep(1000); // Small delay for log readability

        // Test 2: Enrichment route with user message
        logger.info("\n--- Test 2: User Message Enrichment ---");
        producer.sendBodyAndHeader("direct:enrich", "USER001 requesting account balance",
                "MessageType", "USER_REQUEST");

        Thread.sleep(1000);

        // Test 3: Enrichment route with unknown user
        logger.info("\n--- Test 3: Unknown User Message ---");
        producer.sendBodyAndHeader("direct:enrich", "USER999 unknown user request",
                "MessageType", "USER_REQUEST");

        Thread.sleep(1000);

        // Test 4: Combined processing
        logger.info("\n--- Test 4: Combined Processing ---");
        producer.sendBodyAndHeader("direct:combined", "USER002 premium service request",
                "MessageType", "USER_REQUEST");

        Thread.sleep(1000);

        // Test 5: System message
        logger.info("\n--- Test 5: System Message ---");
        producer.sendBodyAndHeader("direct:enrich", "System maintenance scheduled",
                "MessageType", "SYSTEM_MESSAGE");

        Thread.sleep(1000);

        logger.info("=== Route Testing Completed ===\n");
    }
}
