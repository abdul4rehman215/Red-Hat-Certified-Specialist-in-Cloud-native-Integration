package com.alnafi.camel.eip;

import com.alnafi.camel.eip.aggregator.AggregatorRoute;
import com.alnafi.camel.eip.recipientlist.RecipientListRoute;
import com.alnafi.camel.eip.splitter.SplitterRoute;
import org.apache.camel.main.Main;

public class CompleteEIPApplication {

 public static void main(String[] args) throws Exception {
 Main main = new Main();

 // Add all our routes
 main.addRouteBuilder(new SplitterRoute());
 main.addRouteBuilder(new AggregatorRoute());
 main.addRouteBuilder(new RecipientListRoute());

 // Configure Camel context
 main.configure().setName("CompleteEIPDemo");

 System.out.println("=".repeat(80));
 System.out.println("Starting Complete EIP Demo Application");
 System.out.println("=".repeat(80));
 System.out.println("This application demonstrates three key Enterprise Integration Patterns:");
 System.out.println("1. SPLITTER: Divides large orders into individual items");
 System.out.println("2. AGGREGATOR: Combines processed items back into complete orders");
 System.out.println("3. RECIPIENT LIST: Routes orders to multiple systems dynamically");
 System.out.println();
 System.out.println("Watch the logs to see the complete message flow:");
 System.out.println("Order Generation → Splitting → Processing → Aggregation → Distribution");
 System.out.println();
 System.out.println("Press Ctrl+C to stop the application.");
 System.out.println("=".repeat(80));

 // Run the application
 main.run(args);
 }
}
