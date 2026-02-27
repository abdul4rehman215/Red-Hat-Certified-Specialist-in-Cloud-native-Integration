package com.alnafi.integration;

import com.alnafi.integration.route.OrderProcessingRoute;
import com.alnafi.integration.service.InventoryServiceSimulator;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.main.Main;

public class IntegrationApplication {

 public static void main(String[] args) throws Exception {

 // Start embedded ActiveMQ broker (for message queue notifications)
 BrokerService broker = new BrokerService();
 broker.setPersistent(false);
 broker.setUseJmx(false);
 broker.addConnector("tcp://localhost:61616");
 broker.start();

 // Start inventory simulator (REST API)
 InventoryServiceSimulator simulator = new InventoryServiceSimulator();
 simulator.start();

 // Start Camel routes
 Main main = new Main();
 main.configure().setName("CamelEndToEndIntegrationLab");
 main.addRouteBuilder(new OrderProcessingRoute());

 System.out.println("Starting End-to-End Integration Application...");
 System.out.println("- Watching: data/input for CSV orders");
 System.out.println("- Writing reports: data/output");
 System.out.println("- Processed files: data/processed");
 System.out.println("- Errors: data/error");
 System.out.println("- Inventory API: http://localhost:8080/inventory/check?productCode=PROD001");
 System.out.println("- MQ Broker: tcp://localhost:61616");
 System.out.println("Press Ctrl+C to stop");

 // Add MQ consumer routes for notifications
 main.configure().addRoutesBuilder(new com.alnafi.integration.route.NotificationConsumerRoute());

 try {
 main.run(args);
 } finally {
 simulator.stop();
 broker.stop();
 }
 }
}
