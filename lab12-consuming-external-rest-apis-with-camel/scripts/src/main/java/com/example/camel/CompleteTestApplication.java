// src/main/java/com/example/camel/CompleteTestApplication.java
package com.example.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class CompleteTestApplication {

 public static void main(String[] args) throws Exception {
 System.out.println("=== Complete Camel REST API Consumer Test ===");
 System.out.println("This application demonstrates:");
 System.out.println("1. REST API consumption");
 System.out.println("2. Data transformation");
 System.out.println("3. Error handling");
 System.out.println("4. Monitoring and logging");
 System.out.println("===============================================");

 // Create Camel context
 CamelContext camelContext = new DefaultCamelContext();

 // Add all route builders
 camelContext.addRoutes(new EnhancedRestConsumerRoute());
 camelContext.addRoutes(new MonitoringRoute());

 // Start the context
 camelContext.start();

 System.out.println("All routes started successfully!");
 System.out.println("Application will run for 3 minutes...");

 // Run for 3 minutes to see all functionality
 Thread.sleep(180000);

 System.out.println("=== Stopping Application ===");

 // Print final statistics
 System.out.println("Final Statistics:");
 System.out.println("- Total routes: " + camelContext.getRoutes().size());
 System.out.println("- Context uptime: " + camelContext.getUptime());

 // Stop the context
 camelContext.stop();

 System.out.println("Application stopped successfully!");
 }
}
