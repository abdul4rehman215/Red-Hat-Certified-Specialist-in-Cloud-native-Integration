package com.alnafi.camel.lab4;

import com.alnafi.camel.lab4.routes.CustomProcessorRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
* Advanced test application for comprehensive custom processor testing
*/
public class AdvancedTestApplication {

    private static final Logger logger = LoggerFactory.getLogger(AdvancedTestApplication.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting Advanced Custom Processor Testing");

        CamelContext camelContext = new DefaultCamelContext();

        try {
            camelContext.addRoutes(new CustomProcessorRouteBuilder());
            camelContext.start();

            ProducerTemplate producer = camelContext.createProducerTemplate();

            // Run comprehensive tests
            runComprehensiveTests(producer);

            logger.info("Advanced tests finished. Keeping context alive briefly to observe timer route...");
            Thread.sleep(15000);

        } finally {
            camelContext.stop();
            logger.info("Camel context stopped");
        }
    }

    private static void runComprehensiveTests(ProducerTemplate producer) throws Exception {

        // Test Case 1: Various message lengths
        logger.info("\n=== Test Case 1: Message Length Variations ===");
        testMessageLengths(producer);

        // Test Case 2: Special characters and encoding
        logger.info("\n=== Test Case 2: Special Characters ===");
        testSpecialCharacters(producer);

        // Test Case 3: Different user types
        logger.info("\n=== Test Case 3: User Type Variations ===");
        testUserTypes(producer);

        // Test Case 4: Error scenarios
        logger.info("\n=== Test Case 4: Error Scenarios ===");
        testErrorScenarios(producer);

        // Test Case 5: Performance testing
        logger.info("\n=== Test Case 5: Performance Testing ===");
        testPerformance(producer);
    }

    private static void testMessageLengths(ProducerTemplate producer) throws Exception {
        producer.sendBody("direct:transform", "short");
        Thread.sleep(500);

        String medium = "This is a medium length message for testing Camel processors.";
        producer.sendBody("direct:transform", medium);
        Thread.sleep(500);

        // Create a long message
        StringBuilder longMsg = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            longMsg.append("LONG_MESSAGE_PART_").append(i).append(" ");
        }
        producer.sendBody("direct:transform", longMsg.toString());
        Thread.sleep(500);
    }

    private static void testSpecialCharacters(ProducerTemplate producer) throws Exception {
        producer.sendBodyAndHeader(
                "direct:enrich",
                "USER001 request with special chars: !@#$%^&*()",
                "MessageType",
                "USER_REQUEST"
        );
        Thread.sleep(500);

        producer.sendBodyAndHeader(
                "direct:transform",
                "Unicode test: café, naïve, résumé, مرحبا, こんにちは",
                "MessageType",
                "SYSTEM_MESSAGE"
        );
        Thread.sleep(500);

        producer.sendBody("direct:transform", "Newlines\nLine2\nLine3\tTabbed");
        Thread.sleep(500);
    }

    private static void testUserTypes(ProducerTemplate producer) throws Exception {
        // Known user
        producer.sendBodyAndHeader(
                "direct:enrich",
                "USER002 requesting premium service",
                "MessageType",
                "USER_REQUEST"
        );
        Thread.sleep(500);

        // VIP known user
        producer.sendBodyAndHeader(
                "direct:enrich",
                "USER003 VIP escalation required",
                "MessageType",
                "USER_REQUEST"
        );
        Thread.sleep(500);

        // Unknown user
        producer.sendBodyAndHeader(
                "direct:enrich",
                "USER999 password reset request",
                "MessageType",
                "USER_REQUEST"
        );
        Thread.sleep(500);

        // Non-USER message
        producer.sendBodyAndHeader(
                "direct:enrich",
                "General announcement broadcast",
                "MessageType",
                "SYSTEM_MESSAGE"
        );
        Thread.sleep(500);
    }

    private static void testErrorScenarios(ProducerTemplate producer) throws Exception {
        // Null body (will exercise null-handling in MessageTransformProcessor)
        producer.sendBody("direct:transform", null);
        Thread.sleep(500);

        // Missing MessageType header (should default to UNKNOWN in enrichment)
        producer.sendBody("direct:enrich", "USER001 header missing test");
        Thread.sleep(500);

        // Weird format: USER with no extra tokens
        producer.sendBodyAndHeader("direct:enrich", "USER001", "MessageType", "USER_REQUEST");
        Thread.sleep(500);
    }

    private static void testPerformance(ProducerTemplate producer) throws Exception {
        long start = System.currentTimeMillis();

        int count = 50;
        for (int i = 1; i <= count; i++) {
            Map<String, Object> headers = new HashMap<>();
            headers.put("MessageType", (i % 2 == 0) ? "SYSTEM_MESSAGE" : "USER_REQUEST");

            String body = (i % 3 == 0)
                    ? "USER001 performance test message " + i
                    : "regular performance message " + i;

            producer.sendBodyAndHeaders("direct:enrich", body, headers);
        }

        long end = System.currentTimeMillis();
        logger.info("Performance test complete: Sent {} messages in {} ms", count, (end - start));
    }
}
