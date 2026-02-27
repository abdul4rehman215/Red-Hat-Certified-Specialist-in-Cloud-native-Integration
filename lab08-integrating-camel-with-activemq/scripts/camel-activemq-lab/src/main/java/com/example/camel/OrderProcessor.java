package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Processor for handling order messages
 */
public class OrderProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
    private final Random random = new Random();

    @Override
    public void process(Exchange exchange) throws Exception {
        String orderMessage = exchange.getIn().getBody(String.class);

        logger.info("Processing order: {}", orderMessage);

        // Simulate order processing logic
        String orderId = "ORD-" + (1000 + random.nextInt(9000));
        String status = random.nextBoolean() ? "APPROVED" : "PENDING";

        String processedOrder = String.format(
                "Order ID: %s | Status: %s | Original: %s",
                orderId,
                status,
                orderMessage
        );

        exchange.getIn().setBody(processedOrder);
        exchange.getIn().setHeader("OrderId", orderId);
        exchange.getIn().setHeader("OrderStatus", status);

        logger.info("Order processed: {} with status: {}", orderId, status);
    }
}
