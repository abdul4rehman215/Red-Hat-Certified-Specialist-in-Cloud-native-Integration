// src/main/java/com/example/camel/EnhancedRestConsumerRoute.java
package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;

public class EnhancedRestConsumerRoute extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 // Global error handler
 onException(Exception.class)
 .handled(true)
 .log("Error occurred: ${exception.message}")
 .to("direct:handleError");

 // Route 1: Enhanced posts fetching with transformation
 from("timer://enhancedPosts?period=20000&repeatCount=2")
 .routeId("enhanced-posts-route")
 .log("=== Starting Enhanced Posts Fetch ===")
 .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
 .setHeader("User-Agent", constant("Camel-REST-Consumer/1.0"))
 .setHeader("Accept", constant("application/json"))
 .to("https://jsonplaceholder.typicode.com/posts?_limit=5&bridgeEndpoint=true")
 .log("Posts API Response Code: ${header.CamelHttpResponseCode}")
 .choice()
 .when(header("CamelHttpResponseCode").isEqualTo(200))
 .log("Successfully received posts data")
 .process(new DataTransformProcessor())
 .log("Data transformation completed")
 .to("direct:validateAndStore")
 .otherwise()
 .log("Failed to fetch posts: ${header.CamelHttpResponseCode}")
 .to("direct:handleApiError")
 .end();

 // Route 2: Enhanced user fetching with multiple users
 from("timer://enhancedUsers?period=25000&repeatCount=2")
 .routeId("enhanced-users-route")
 .log("=== Starting Enhanced Users Fetch ===")
 .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
 .setHeader("User-Agent", constant("Camel-REST-Consumer/1.0"))
 .setHeader("Accept", constant("application/json"))
 .to("https://jsonplaceholder.typicode.com/users?_limit=3&bridgeEndpoint=true")
 .log("Users API Response Code: ${header.CamelHttpResponseCode}")
 .choice()
 .when(header("CamelHttpResponseCode").isEqualTo(200))
 .log("Successfully received users data")
 .process(new DataTransformProcessor())
 .log("User data transformation completed")
 .to("direct:processUsersArray")
 .otherwise()
 .log("Failed to fetch users: ${header.CamelHttpResponseCode}")
 .to("direct:handleApiError")
 .end();

 // Route 3: Validate and store transformed data
 from("direct:validateAndStore")
 .routeId("validate-store-route")
 .log("Validating transformed data...")
 .unmarshal().json()
 .choice()
 .when().jsonpath("$[?(@.category)]")
 .log("Data validation passed - category field present")
 .split().jsonpath("$[*]")
 .log("Storing post: ${body[title]} (Category: ${body[category]})")
 .to("direct:storeInDatabase")
 .end()
 .otherwise()
 .log("Data validation failed - missing required fields")
 .end();

 // Route 4: Process users array
 from("direct:processUsersArray")
 .routeId("process-users-array-route")
 .log("Processing users array...")
 .unmarshal().json()
 .split().jsonpath("$[*]")
 .log("Processing user: ${body[name]} (${body[email]}) - Domain: ${body[emailDomain]}")
 .choice()
 .when().jsonpath("$[?(@.emailDomain == 'biz')]")
 .log("Business user detected: ${body[name]}")
 .setHeader("UserType", constant("BUSINESS"))
 .otherwise()
 .setHeader("UserType", constant("PERSONAL"))
 .end()
 .to("direct:storeUserInDatabase")
 .end();

 // Route 5: Store in database (simulation)
 from("direct:storeInDatabase")
 .routeId("store-database-route")
 .log("=== STORING POST IN DATABASE ===")
 .process(exchange -> {
 Object body = exchange.getIn().getBody();
 System.out.println("DATABASE INSERT: " + body);
 // Simulate database operation
 Thread.sleep(100);
 })
 .log("Post successfully stored in database");

 // Route 6: Store user in database (simulation)
 from("direct:storeUserInDatabase")
 .routeId("store-user-database-route")
 .log("=== STORING USER IN DATABASE ===")
 .log("User Type: ${header.UserType}")
 .process(exchange -> {
 Object body = exchange.getIn().getBody();
 String userType = exchange.getIn().getHeader("UserType", String.class);
 System.out.println("DATABASE INSERT USER (" + userType + "): " + body);
 // Simulate database operation
 Thread.sleep(100);
 })
 .log("User successfully stored in database");

 // Route 7: Handle API errors
 from("direct:handleApiError")
 .routeId("handle-api-error-route")
 .log("=== API ERROR HANDLER ===")
 .log("Response Code: ${header.CamelHttpResponseCode}")
 .log("Response Text: ${body}")
 .choice()
 .when(header("CamelHttpResponseCode").isEqualTo(404))
 .log("Resource not found - skipping")
 .when(header("CamelHttpResponseCode").isEqualTo(429))
 .log("Rate limit exceeded - implementing backoff")
 .delay(5000)
 .otherwise()
 .log("Unexpected error occurred")
 .end();

 // Route 8: General error handler
 from("direct:handleError")
 .routeId("general-error-handler-route")
 .log("=== GENERAL ERROR HANDLER ===")
 .log("Error: ${exception.message}")
 .log("Stack trace: ${exception.stacktrace}");
 }
}
