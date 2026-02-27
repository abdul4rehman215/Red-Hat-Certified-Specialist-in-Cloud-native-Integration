package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.util.Random;

public class FailureSimulatorProcessor implements Processor {

 private final double failureRate;
 private final Random random = new Random();
 private int processCount = 0;

 public FailureSimulatorProcessor(double failureRate) {
 this.failureRate = failureRate;
 }

 @Override
 public void process(Exchange exchange) throws Exception {
 processCount++;
 String body = exchange.getIn().getBody(String.class);

 // Simulate different types of failures
 if (random.nextDouble() < failureRate) {
 if (processCount % 3 == 0) {
 throw new RuntimeException("Simulated runtime exception for message: " + body);
 } else if (processCount % 5 == 0) {
 throw new IllegalArgumentException("Simulated validation error for message: " + body);
 } else {
 throw new Exception("Simulated general exception for message: " + body);
 }
 }

 // Success case
 exchange.getIn().setBody("Processed successfully: " + body + " (attempt #" + processCount + ")");
 System.out.println("Successfully processed: " + body);
 }
}
