package com.alnafi.camel.lab4.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Custom processor that transforms incoming messages by:
* 1. Converting text to uppercase
* 2. Adding a timestamp
* 3. Adding custom headers
*/
public class MessageTransformProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(MessageTransformProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        // Log the incoming message
        logger.info("Processing message in custom processor");

        // Get the message body as String
        String originalMessage = exchange.getIn().getBody(String.class);
        logger.info("Original message: {}", originalMessage);

        // Transform the message
        String transformedMessage = transformMessage(originalMessage);

        // Set the transformed message back to the exchange
        exchange.getIn().setBody(transformedMessage);

        // Add custom headers
        addCustomHeaders(exchange);

        logger.info("Transformed message: {}", transformedMessage);
    }

    /**
     * Transform the message content
     */
    private String transformMessage(String originalMessage) {
        if (originalMessage == null) {
            return "NULL_MESSAGE_PROCESSED_AT_" + System.currentTimeMillis();
        }

        // Convert to uppercase and add timestamp
        String transformed = originalMessage.toUpperCase() +
                " [PROCESSED_AT_" + System.currentTimeMillis() + "]";

        return transformed;
    }

    /**
     * Add custom headers to the message
     */
    private void addCustomHeaders(Exchange exchange) {
        exchange.getIn().setHeader("ProcessedBy", "MessageTransformProcessor");
        exchange.getIn().setHeader("ProcessingTimestamp", System.currentTimeMillis());
        exchange.getIn().setHeader("MessageLength",
                exchange.getIn().getBody(String.class).length());
    }
}
