package com.example.routing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class PerformanceTest {

 private static final String BASE_URL = "http://localhost:8080";
 private static final HttpClient client = HttpClient.newHttpClient();

 public static void main(String[] args) throws Exception {
 System.out.println("Starting performance test for dynamic routing...");

 ExecutorService executor = Executors.newFixedThreadPool(10);

 // Test different routing scenarios concurrently
 for (int i = 0; i < 100; i++) {
 final int requestId = i;
 executor.submit(() -> {
 try {
 testOrderRouting(requestId);
 testProductRouting(requestId);
 testComplexRouting(requestId);
 } catch (Exception e) {
 System.err.println("Error in request " + requestId + ": " + e.getMessage());
 }
 });
 }

 executor.shutdown();
 executor.awaitTermination(60, TimeUnit.SECONDS);

 System.out.println("Performance test completed.");
 }

 private static void testOrderRouting(int id) throws Exception {
 String[] priorities = {"HIGH", "MEDIUM", "LOW"};
 String priority = priorities[id % 3];

 String json = String.format(
 "{\"orderId\": \"ORD%03d\", \"priority\": \"%s\", \"amount\": %d}",
 id, priority, (id * 100) % 2000
 );

 HttpRequest request = HttpRequest.newBuilder()
 .uri(URI.create(BASE_URL + "/orders"))
 .header("Content-Type", "application/json")
 .POST(HttpRequest.BodyPublishers.ofString(json))
 .build();

 HttpResponse<String> response = client.send(request,
 HttpResponse.BodyHandlers.ofString());

 if (response.statusCode() == 200) {
 System.out.println("Order " + id + " processed successfully");
 }
 }

 private static void testProductRouting(int id) throws Exception {
 String[] categories = {"ELECTRONICS", "CLOTHING", "BOOKS", "FOOD"};
 String category = categories[id % 4];

 String json = String.format(
 "{\"productId\": \"PROD%03d\", \"category\": \"%s\", \"name\": \"Product %d\"}",
 id, category, id
 );

 HttpRequest request = HttpRequest.newBuilder()
 .uri(URI.create(BASE_URL + "/products"))
 .header("Content-Type", "application/json")
 .POST(HttpRequest.BodyPublishers.ofString(json))
 .build();

 HttpResponse<String> response = client.send(request,
 HttpResponse.BodyHandlers.ofString());

 if (response.statusCode() == 200) {
 System.out.println("Product " + id + " processed successfully");
 }
 }

 private static void testComplexRouting(int id) throws Exception {
 String[] priorities = {"URGENT", "HIGH", "MEDIUM", "LOW"};
 String[] regions = {"NORTH", "SOUTH", "EAST", "WEST"};
 String[] categories = {"ELECTRONICS", "CLOTHING", "BOOKS", "FOOD"};

 String json = String.format(
 "{\"orderId\": \"COMPLEX%03d\", \"priority\": \"%s\", \"amount\": %d, \"region\": \"%s\", \"category\": \"%s\"}",
 id, priorities[id % 4], (id * 150) % 3000, regions[id % 4], categories[id % 4]
 );

 HttpRequest request = HttpRequest.newBuilder()
 .uri(URI.create(BASE_URL + "/complex-orders"))
 .header("Content-Type", "application/json")
 .POST(HttpRequest.BodyPublishers.ofString(json))
 .build();

 HttpResponse<String> response = client.send(request,
 HttpResponse.BodyHandlers.ofString());

 if (response.statusCode() == 200) {
 System.out.println("Complex order " + id + " processed successfully");
 }
 }
}
