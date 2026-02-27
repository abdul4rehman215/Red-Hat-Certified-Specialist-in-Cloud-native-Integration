package com.alnafi.integration.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class InventoryServiceSimulator {
 private static final Logger logger = LoggerFactory.getLogger(InventoryServiceSimulator.class);
 private HttpServer server;

 // Simulated inventory data
 private final Map<String, Integer> inventory = new HashMap<>();

 public InventoryServiceSimulator() {
 // Initialize inventory
 inventory.put("PROD001", 100);
 inventory.put("PROD002", 50);
 inventory.put("PROD003", 25);
 inventory.put("PROD004", 75);
 inventory.put("PROD005", 200);
 }

 public void start() throws IOException {
 server = HttpServer.create(new InetSocketAddress(8080), 0);
 server.createContext("/inventory/check", new InventoryHandler());
 server.setExecutor(null);
 server.start();
 logger.info("Inventory service simulator started on port 8080");
 }

 public void stop() {
 if (server != null) {
 server.stop(0);
 logger.info("Inventory service simulator stopped");
 }
 }

 class InventoryHandler implements HttpHandler {
 @Override
 public void handle(HttpExchange exchange) throws IOException {
 if ("GET".equals(exchange.getRequestMethod())) {
 String query = exchange.getRequestURI().getQuery();
 String productCode = extractProductCode(query);

 if (productCode != null && inventory.containsKey(productCode)) {
 int availableQty = inventory.get(productCode);
 String response = String.format(
 "{\"productCode\":\"%s\",\"availableQuantity\":%d,\"status\":\"AVAILABLE\"}",
 productCode, availableQty);

 exchange.getResponseHeaders().set("Content-Type", "application/json");
 exchange.sendResponseHeaders(200, response.length());

 try (OutputStream os = exchange.getResponseBody()) {
 os.write(response.getBytes());
 }

 logger.info("Inventory check for {}: {} available", productCode, availableQty);
 } else {
 String response = "{\"error\":\"Product not found\"}";
 exchange.sendResponseHeaders(404, response.length());

 try (OutputStream os = exchange.getResponseBody()) {
 os.write(response.getBytes());
 }
 }
 } else {
 exchange.sendResponseHeaders(405, 0);
 }
 }

 private String extractProductCode(String query) {
 if (query != null && query.startsWith("productCode=")) {
 return query.substring("productCode=".length());
 }
 return null;
 }
 }
}
