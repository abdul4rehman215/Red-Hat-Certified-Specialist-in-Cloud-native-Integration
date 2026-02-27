package com.example.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Scanner;

/**
 * Utility class for sending test messages to ActiveMQ queues
 */
public class MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
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
                System.out.println("\n=== Message Sender Menu ===");
                System.out.println("1. Send to input.queue (simple transformation)");
                System.out.println("2. Send to order.queue (order processing)");
                System.out.println("3. Send to notification.input (pub-sub pattern)");
                System.out.println("4. Send to batch.input (aggregation pattern)");
                System.out.println("5. Send to request.queue (request-reply)");
                System.out.println("6. Send to risky.queue (error handling)");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice == 7) {
                    break;
                }

                System.out.print("Enter message content: ");
                String messageContent = scanner.nextLine();

                String queueName = getQueueName(choice);
                if (queueName != null) {
                    sendMessage(session, queueName, messageContent);
                }
            }

            // Cleanup
            session.close();
            connection.close();
            scanner.close();

        } catch (Exception e) {
            logger.error("Error in message sender", e);
        }
    }

    private static String getQueueName(int choice) {
        switch (choice) {
            case 1: return "input.queue";
            case 2: return "order.queue";
            case 3: return "notification.input";
            case 4: return "batch.input";
            case 5: return "request.queue";
            case 6: return "risky.queue";
            default:
                System.out.println("Invalid choice!");
                return null;
        }
    }

    private static void sendMessage(Session session, String queueName, String messageContent) {
        try {
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage(messageContent);
            producer.send(message);

            logger.info("Message sent to {}: {}", queueName, messageContent);
            System.out.println("Message sent successfully to " + queueName);

            producer.close();

        } catch (Exception e) {
            logger.error("Error sending message to " + queueName, e);
        }
    }
}
