package com.example.routing;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationService {

 private static final Map<String, String> routingRules = new HashMap<>();
 private static final Map<String, Integer> priorityWeights = new HashMap<>();
 private static final Map<String, String> regionQueues = new HashMap<>();

 private static double highValueThreshold = 1000.0;
 private static int businessHoursStart = 9;
 private static int businessHoursEnd = 17;
 private static int eveningHoursEnd = 21;

 static {
 // Initialize routing rules (defaults)
 routingRules.put("ELECTRONICS", "direct:electronicsProcessor");
 routingRules.put("CLOTHING", "direct:clothingProcessor");
 routingRules.put("BOOKS", "direct:booksProcessor");
 routingRules.put("FOOD", "direct:foodProcessor");

 // Initialize priority weights (defaults)
 priorityWeights.put("URGENT", 1);
 priorityWeights.put("HIGH", 2);
 priorityWeights.put("MEDIUM", 3);
 priorityWeights.put("LOW", 4);

 // Default region queues
 regionQueues.put("NORTH", "direct:northRegionQueue");
 regionQueues.put("SOUTH", "direct:southRegionQueue");
 regionQueues.put("EAST", "direct:eastRegionQueue");
 regionQueues.put("WEST", "direct:westRegionQueue");
 regionQueues.put("DEFAULT", "direct:defaultRegionQueue");

 // Try loading overrides from routing-config.properties (external config)
 loadExternalProperties();
 }

 private static void loadExternalProperties() {
 try (InputStream in = ConfigurationService.class.getClassLoader()
         .getResourceAsStream("routing-config.properties")) {

 if (in == null) {
 return; // no external file, just keep defaults
 }

 Properties p = new Properties();
 p.load(in);

 // Category routes
 putIfPresent(p, "routing.electronics.endpoint", "ELECTRONICS");
 putIfPresent(p, "routing.clothing.endpoint", "CLOTHING");
 putIfPresent(p, "routing.books.endpoint", "BOOKS");
 putIfPresent(p, "routing.food.endpoint", "FOOD");

 String defaultRoute = p.getProperty("routing.default.endpoint");
 if (defaultRoute != null && !defaultRoute.isBlank()) {
 routingRules.put("DEFAULT", defaultRoute.trim());
 }

 // Priority weights
 setWeightIfPresent(p, "priority.urgent.weight", "URGENT");
 setWeightIfPresent(p, "priority.high.weight", "HIGH");
 setWeightIfPresent(p, "priority.medium.weight", "MEDIUM");
 setWeightIfPresent(p, "priority.low.weight", "LOW");

 // High value threshold
 String threshold = p.getProperty("order.highvalue.threshold");
 if (threshold != null) {
 highValueThreshold = Double.parseDouble(threshold.trim());
 }

 // Region queues
 setQueueIfPresent(p, "region.north.queue", "NORTH");
 setQueueIfPresent(p, "region.south.queue", "SOUTH");
 setQueueIfPresent(p, "region.east.queue", "EAST");
 setQueueIfPresent(p, "region.west.queue", "WEST");
 setQueueIfPresent(p, "region.default.queue", "DEFAULT");

 // Business hours
 String bhStart = p.getProperty("business.hours.start");
 String bhEnd = p.getProperty("business.hours.end");
 String eveEnd = p.getProperty("evening.hours.end");

 if (bhStart != null) businessHoursStart = Integer.parseInt(bhStart.trim());
 if (bhEnd != null) businessHoursEnd = Integer.parseInt(bhEnd.trim());
 if (eveEnd != null) eveningHoursEnd = Integer.parseInt(eveEnd.trim());

 } catch (Exception e) {
 // keep defaults if external config has issues
 System.err.println("Warning: failed to load routing-config.properties: " + e.getMessage());
 }
 }

 private static void putIfPresent(Properties p, String key, String category) {
 String v = p.getProperty(key);
 if (v != null && !v.isBlank()) {
 routingRules.put(category, v.trim());
 }
 }

 private static void setWeightIfPresent(Properties p, String key, String priority) {
 String v = p.getProperty(key);
 if (v != null && !v.isBlank()) {
 priorityWeights.put(priority, Integer.parseInt(v.trim()));
 }
 }

 private static void setQueueIfPresent(Properties p, String key, String region) {
 String v = p.getProperty(key);
 if (v != null && !v.isBlank()) {
 regionQueues.put(region, v.trim());
 }
 }

 public static String getRouteForCategory(String category) {
 if (category == null) return routingRules.getOrDefault("DEFAULT", "direct:defaultCategoryProcessor");
 return routingRules.getOrDefault(category.toUpperCase(),
         routingRules.getOrDefault("DEFAULT", "direct:defaultCategoryProcessor"));
 }

 public static int getPriorityWeight(String priority) {
 if (priority == null) return 5;
 return priorityWeights.getOrDefault(priority.toUpperCase(), 5);
 }

 public static boolean isHighValueOrder(double amount) {
 return amount > highValueThreshold;
 }

 public static String getProcessingQueue(String region) {
 if (region == null) return regionQueues.getOrDefault("DEFAULT", "direct:defaultRegionQueue");
 return regionQueues.getOrDefault(region.toUpperCase(), regionQueues.getOrDefault("DEFAULT", "direct:defaultRegionQueue"));
 }

 public static int getBusinessHoursStart() {
 return businessHoursStart;
 }

 public static int getBusinessHoursEnd() {
 return businessHoursEnd;
 }

 public static int getEveningHoursEnd() {
 return eveningHoursEnd;
 }
}
