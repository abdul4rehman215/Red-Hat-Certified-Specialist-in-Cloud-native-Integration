// src/main/java/com/example/camel/RestConsumerApplication.java
package com.example.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class RestConsumerApplication {

 public static void main(String[] args) throws Exception {
 System.out.println("=== Starting Camel REST API Consumer ===");

 // Create Camel context
 CamelContext camelContext = new DefaultCamelContext();

 // Add enhanced route builder
 camelContext.addRoutes(new EnhancedRestConsumerRoute());

 // Start the context
 camelContext.start();

 System.out.println("Camel REST Consumer started successfully!");
 System.out.println("The application will run for 2 minutes to demonstrate API consumption.");
 System.out.println("Press Ctrl+C to stop earlier if needed.");

 // Keep the application running for demonstration
 Thread.sleep(120000); // Run for 2 minutes

 System.out.println("=== Stopping Camel REST API Consumer ===");

 // Stop the context
 camelContext.stop();

 System.out.println("Application stopped successfully!");
 }
}
