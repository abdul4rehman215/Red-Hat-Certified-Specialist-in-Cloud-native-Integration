package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom processor for handling messages
 */
public class MessageProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void process(Exchange exchange) throws Exception {
        String originalMessage = exchange.getIn().getBody(String.class);
        String timestamp = LocalDateTime.now().format(formatter);

        logger.info("Processing message: {}", originalMessage);

        // Transform the message
        String processedMessage = String.format(
                "Processed at %s: %s",
                timestamp,
                originalMessage.toUpperCase()
        );

        // Set the processed message back to the exchange
        exchange.getIn().setBody(processedMessage);

        // Add custom headers
        exchange.getIn().setHeader("ProcessedBy", "MessageProcessor");
        exchange.getIn().setHeader("ProcessedAt", timestamp);

        logger.info("Message processed successfully: {}", processedMessage);
    }
}
