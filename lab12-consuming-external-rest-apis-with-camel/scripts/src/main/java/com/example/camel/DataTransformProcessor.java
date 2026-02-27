// src/main/java/com/example/camel/DataTransformProcessor.java
package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;

public class DataTransformProcessor implements Processor {

 private final ObjectMapper objectMapper = new ObjectMapper();

 @Override
 public void process(Exchange exchange) throws Exception {
 String jsonBody = exchange.getIn().getBody(String.class);

 // Parse JSON
 JsonNode rootNode = objectMapper.readTree(jsonBody);

 if (rootNode.isArray()) {
 // Process array of posts
 for (JsonNode postNode : rootNode) {
 transformPost((ObjectNode) postNode);
 }
 } else {
 // Process single object (user)
 transformUser((ObjectNode) rootNode);
 }

 // Set transformed data back to exchange
 exchange.getIn().setBody(objectMapper.writeValueAsString(rootNode));
 }

 private void transformPost(ObjectNode postNode) {
 // Add metadata
 postNode.put("processedAt", new Date().toString());
 postNode.put("source", "jsonplaceholder-api");

 // Transform title to uppercase
 if (postNode.has("title")) {
 String title = postNode.get("title").asText();
 postNode.put("title", title.toUpperCase());
 }

 // Add word count for body
 if (postNode.has("body")) {
 String body = postNode.get("body").asText();
 int wordCount = body.split("\\s+").length;
 postNode.put("wordCount", wordCount);
 }

 // Add category based on userId
 if (postNode.has("userId")) {
 int userId = postNode.get("userId").asInt();
 String category = userId <= 5 ? "PRIORITY" : "STANDARD";
 postNode.put("category", category);
 }
 }

 private void transformUser(ObjectNode userNode) {
 // Add metadata
 userNode.put("processedAt", new Date().toString());
 userNode.put("source", "jsonplaceholder-api");

 // Create full address string
 if (userNode.has("address")) {
 JsonNode address = userNode.get("address");
 String fullAddress = String.format("%s, %s, %s %s",
 address.get("street").asText(),
 address.get("city").asText(),
 address.get("suite").asText(),
 address.get("zipcode").asText()
 );
 userNode.put("fullAddress", fullAddress);
 }

 // Extract domain from email
 if (userNode.has("email")) {
 String email = userNode.get("email").asText();
 String domain = email.substring(email.indexOf("@") + 1);
 userNode.put("emailDomain", domain);
 }
 }
}
