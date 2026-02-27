// src/main/java/com/example/camel/RestApiConsumerRoute.java
package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;

public class RestApiConsumerRoute extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Route 1: Consume JSONPlaceholder API for posts
 from("timer://fetchPosts?period=30000&repeatCount=3")
 .routeId("fetch-posts-route")
 .log("Starting to fetch posts from JSONPlaceholder API...")
 .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
 .setHeader("Content-Type", constant("application/json"))
 .to("https://jsonplaceholder.typicode.com/posts?bridgeEndpoint=true")
 .log("Received response with ${header.CamelHttpResponseCode} status code")
 .choice()
 .when(header("CamelHttpResponseCode").isEqualTo(200))
 .log("Successfully fetched posts data")
 .to("direct:processPostsData")
 .otherwise()
 .log("Failed to fetch posts. Status: ${header.CamelHttpResponseCode}")
 .end();

 // Route 2: Consume specific user information
 from("timer://fetchUser?period=45000&repeatCount=2")
 .routeId("fetch-user-route")
 .log("Fetching user information...")
 .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
 .setHeader("Content-Type", constant("application/json"))
 .to("https://jsonplaceholder.typicode.com/users/1?bridgeEndpoint=true")
 .log("User API response status: ${header.CamelHttpResponseCode}")
 .choice()
 .when(header("CamelHttpResponseCode").isEqualTo(200))
 .to("direct:processUserData")
 .otherwise()
 .log("Error fetching user data: ${header.CamelHttpResponseCode}")
 .end();

 // Route 3: Process posts data
 from("direct:processPostsData")
 .routeId("process-posts-route")
 .log("Processing posts data...")
 .unmarshal().json()
 .log("Converted JSON to object. Processing ${body.size} posts")
 .split().jsonpath("$[*]")
 .log("Processing post: ID=${body[id]}, Title=${body[title]}")
 .to("direct:savePostData")
 .end();

 // Route 4: Process user data
 from("direct:processUserData")
 .routeId("process-user-route")
 .log("Processing user data...")
 .unmarshal().json()
 .log("User Info - Name: ${body[name]}, Email: ${body[email]}, Company: ${body[company][name]}")
 .to("direct:saveUserData");

 // Route 5: Save post data (simulation)
 from("direct:savePostData")
 .routeId("save-post-route")
 .log("Saving post data: ${body}")
 .process(exchange -> {
 // Simulate data processing/saving
 Object body = exchange.getIn().getBody();
 System.out.println("Post saved to database: " + body);
 });

 // Route 6: Save user data (simulation)
 from("direct:saveUserData")
 .routeId("save-user-route")
 .log("Saving user data: ${body}")
 .process(exchange -> {
 // Simulate data processing/saving
 Object body = exchange.getIn().getBody();
 System.out.println("User saved to database: " + body);
 });
 }
}
