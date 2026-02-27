package com.alnafi.camel.lab4.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.junit.Assert.*;

/**
* Integration tests for custom processor routes
*/
public class CustomProcessorRouteTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CustomProcessorRouteBuilder();
    }

    @Test
    public void testTransformRoute() throws Exception {
        String result = template.requestBody("direct:transform", "test message", String.class);

        assertNotNull(result);
        assertTrue("Message should be transformed to uppercase",
                result.startsWith("TEST MESSAGE"));
        assertTrue("Message should contain processing timestamp",
                result.contains("PROCESSED_AT_"));
    }

    @Test
    public void testEnrichmentRoute() throws Exception {
        String result = template.requestBodyAndHeader(
                "direct:enrich",
                "USER001 account inquiry",
                "MessageType",
                "USER_REQUEST",
                String.class
        );

        assertNotNull(result);
        assertTrue("Message should contain message type",
                result.startsWith("[USER_REQUEST]"));
        assertTrue("Message should be enriched with user data",
                result.contains("John Doe - Premium Customer"));
    }

    @Test
    public void testCombinedProcessing() throws Exception {
        Exchange exchange = template.request(
                "direct:combined",
                e -> {
                    e.getIn().setBody("USER002 service request");
                    e.getIn().setHeader("MessageType", "USER_REQUEST");
                }
        );

        assertNotNull(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertNotNull(body);

        assertTrue("Message should contain enrichment data",
                body.contains("Jane Smith - Standard Customer"));

        assertEquals("Should have ProcessedBy header",
                "MessageTransformProcessor",
                exchange.getIn().getHeader("ProcessedBy"));

        assertEquals("Should have EnrichedBy header",
                "MessageEnrichmentProcessor",
                exchange.getIn().getHeader("EnrichedBy"));

        assertEquals("Priority should be HIGH for USER_REQUEST",
                "HIGH",
                exchange.getIn().getHeader("Priority"));
    }
}
