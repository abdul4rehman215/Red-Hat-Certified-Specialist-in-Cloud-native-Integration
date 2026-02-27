package com.alnafi.camel.eip;

import com.alnafi.camel.eip.splitter.SplitterRoute;
import org.apache.camel.main.Main;

public class SplitterApplication {

 public static void main(String[] args) throws Exception {
 Main main = new Main();

 // Add our route
 main.addRouteBuilder(new SplitterRoute());

 // Configure Camel context
 main.configure().setName("SplitterEIPDemo");

 System.out.println("Starting Splitter EIP Demo...");
 System.out.println("The application will generate sample orders and split them into individual items.");
 System.out.println("Press Ctrl+C to stop the application.");

 // Run the application
 main.run(args);
 }
}
