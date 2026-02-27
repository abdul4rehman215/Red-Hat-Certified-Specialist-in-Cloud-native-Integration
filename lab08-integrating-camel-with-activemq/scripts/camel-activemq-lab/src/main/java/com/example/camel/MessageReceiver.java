package com.example.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Scanner;

/**
 * Utility class for receiving messages from ActiveMQ queues
 */
public class MessageReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);
    private static final String BROKER_URL = "tcp://localhost:61616";

    public static void main(String[] args) {
        try {
            // Create connection factory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

            // Create connection and session
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n=== Message Receiver Menu ===");
                System.out.println("1. Receive from output.queue");
                System.out.println("2. Receive from fulfillment.queue");
                System.out.println("3. Receive from review.queue");
                System.out.println("4. Receive from processed.notifications");
                System.out.println("5. Receive from batch.output");
                System.out.println("6. Receive from success.queue");
                System.out.println("7. Receive from error.queue");
                System.out.println("8. Receive from dead.letter.queue");
                System.out.println("9. Receive from response.queue");
                System.out.println("10. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice == 10) {
                    break;
                }

                String queueName = getQueueName(choice);
                if (queueName == null) {
                    System.out.println("Invalid option!");
                    continue;
                }

                receiveMessage(session, queueName);
            }

            // Cleanup
            session.close();
            connection.close();
            scanner.close();

        } catch (Exception e) {
            logger.error("Error in message receiver", e);
        }
    }

    private static String getQueueName(int choice) {
        switch (choice) {
            case 1: return "output.queue";
            case 2: return "fulfillment.queue";
            case 3: return "review.queue";
            case 4: return "processed.notifications";
            case 5: return "batch.output";
            case 6: return "success.queue";
            case 7: return "error.queue";
            case 8: return "dead.letter.queue";
            case 9: return "response.queue";
            default: return null;
        }
    }

    private static void receiveMessage(Session session, String queueName) {
        try {
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);

            System.out.println("Waiting for message from " + queueName + " (timeout 5s)...");
            Message message = consumer.receive(5000);

            if (message == null) {
                System.out.println("No message received from " + queueName + " (timeout).");
                consumer.close();
                return;
            }

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("✅ Received: " + textMessage.getText());
            } else {
                System.out.println("✅ Received non-text message: " + message);
            }

            // Print common headers (if present)
            String processedBy = message.getStringProperty("ProcessedBy");
            String processedAt = message.getStringProperty("ProcessedAt");
            String orderId = message.getStringProperty("OrderId");
            String orderStatus = message.getStringProperty("OrderStatus");

            if (processedBy != null) System.out.println("Header ProcessedBy: " + processedBy);
            if (processedAt != null) System.out.println("Header ProcessedAt: " + processedAt);
            if (orderId != null) System.out.println("Header OrderId: " + orderId);
            if (orderStatus != null) System.out.println("Header OrderStatus: " + orderStatus);

            consumer.close();

        } catch (Exception e) {
            logger.error("Error receiving message from " + queueName, e);
        }
    }
}
