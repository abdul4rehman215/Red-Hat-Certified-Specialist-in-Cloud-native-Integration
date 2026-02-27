package com.alnafi.camel.eip;

import com.alnafi.camel.eip.aggregator.AggregatorRoute;
import com.alnafi.camel.eip.splitter.SplitterRoute;
import org.apache.camel.main.Main;

public class SplitterAggregatorApplication {

 public static void main(String[] args) throws Exception {
 Main main = new Main();

 // Add our routes
 main.addRouteBuilder(new SplitterRoute());
 main.addRouteBuilder(new AggregatorRoute());

 // Configure Camel context
 main.configure().setName("SplitterAggregatorEIPDemo");

 System.out.println("Starting Splitter-Aggregator EIP Demo...");
 System.out.println("The application will:");
 System.out.println("1. Generate sample orders");
 System.out.println("2. Split orders into individual items");
 System.out.println("3. Process items individually");
 System.out.println("4. Aggregate items back into complete orders");
 System.out.println("Press Ctrl+C to stop the application.");

 // Run the application
 main.run(args);
 }
}
