package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Camel Route Builder for ActiveMQ integration
 */
public class CamelRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CamelRouteBuilder.class);

    @Override
    public void configure() throws Exception {

        logger.info("Configuring Camel routes for ActiveMQ integration");

        // Error handling configuration
        onException(Exception.class)
                .handled(true)
                .log("Error processing message: ${exception.message}")
                .to("jms:queue:error.queue");

        // Route 1: Simple message transformation route
        from("jms:queue:input.queue")
                .routeId("simple-transformation-route")
                .log("Received message from input.queue: ${body}")
                .process(new MessageProcessor())
                .log("Sending processed message to output.queue: ${body}")
                .to("jms:queue:output.queue");

        // Route 2: Order processing route with content-based routing
        from("jms:queue:order.queue")
                .routeId("order-processing-route")
                .log("Received order from order.queue: ${body}")
                .process(new OrderProcessor())
                .choice()
                    .when(header("OrderStatus").isEqualTo("APPROVED"))
                        .log("Order approved, sending to fulfillment: ${body}")
                        .to("jms:queue:fulfillment.queue")
                    .when(header("OrderStatus").isEqualTo("PENDING"))
                        .log("Order pending, sending to review: ${body}")
                        .to("jms:queue:review.queue")
                    .otherwise()
                        .log("Unknown order status, sending to error: ${body}")
                        .to("jms:queue:error.queue")
                .end();

        // Route 3: Topic-based publish-subscribe pattern
        from("jms:queue:notification.input")
                .routeId("notification-publisher-route")
                .log("Publishing notification to topic: ${body}")
                .multicast()
                .to("jms:topic:email.notifications")
                .to("jms:topic:sms.notifications")
                .to("jms:topic:push.notifications");

        // Route 4: Topic subscribers
        from("jms:topic:email.notifications")
                .routeId("email-notification-subscriber")
                .log("Email notification received: ${body}")
                .transform(simple("EMAIL: ${body}"))
                .to("jms:queue:processed.notifications");

        from("jms:topic:sms.notifications")
                .routeId("sms-notification-subscriber")
                .log("SMS notification received: ${body}")
                .transform(simple("SMS: ${body}"))
                .to("jms:queue:processed.notifications");

        from("jms:topic:push.notifications")
                .routeId("push-notification-subscriber")
                .log("Push notification received: ${body}")
                .transform(simple("PUSH: ${body}"))
                .to("jms:queue:processed.notifications");

        // Route 5: Aggregation pattern - collect messages and process in batches
        from("jms:queue:batch.input")
                .routeId("batch-aggregation-route")
                .log("Received message for batching: ${body}")
                .aggregate(constant(true))
                    .completionSize(3)
                    .completionTimeout(10000)
                    .log("Processing batch of ${body.size} messages")
                    .split(body())
                        .log("Processing individual message from batch: ${body}")
                        .to("jms:queue:batch.output")
                .end();

        // Route 6: Request-Reply pattern
        from("jms:queue:request.queue")
                .routeId("request-reply-route")
                .log("Processing request: ${body}")
                .transform(simple("Response to: ${body}"))
                .log("Sending response: ${body}")
                .to("jms:queue:response.queue");

        // Route 7: Dead Letter Queue pattern
        from("jms:queue:risky.queue")
                .routeId("risky-processing-route")
                .errorHandler(deadLetterChannel("jms:queue:dead.letter.queue")
                        .maximumRedeliveries(3)
                        .redeliveryDelay(2000))
                .log("Processing risky message: ${body}")
                .process(exchange -> {
                    // Simulate random failures for demonstration
                    if (Math.random() < 0.3) {
                        throw new RuntimeException("Simulated processing error");
                    }
                    exchange.getIn().setBody("Successfully processed: " + exchange.getIn().getBody());
                })
                .log("Risky message processed successfully: ${body}")
                .to("jms:queue:success.queue");

        logger.info("Camel routes configured successfully");
    }
}
