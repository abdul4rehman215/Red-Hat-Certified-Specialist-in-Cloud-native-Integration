package com.alnafi.camel.eip;

import com.alnafi.camel.eip.aggregator.AggregatorRoute;
import com.alnafi.camel.eip.recipientlist.RecipientListRoute;
import com.alnafi.camel.eip.rest.EIPRestRoute;
import com.alnafi.camel.eip.splitter.SplitterRoute;
import org.apache.camel.main.Main;

public class CompleteEIPRestApplication {

 public static void main(String[] args) throws Exception {
 Main main = new Main();

 // Add all routes (EIPs + REST)
 main.addRouteBuilder(new SplitterRoute());
 main.addRouteBuilder(new AggregatorRoute());
 main.addRouteBuilder(new RecipientListRoute());
 main.addRouteBuilder(new EIPRestRoute());

 main.configure().setName("CompleteEIPRestDemo");

 System.out.println("Complete EIP + REST Demo started.");
 System.out.println("Try:");
 System.out.println("GET  http://localhost:8080/api/eip/sample");
 System.out.println("POST http://localhost:8080/api/eip/order");
 System.out.println("Press Ctrl+C to stop.");

 main.run(args);
 }
}
