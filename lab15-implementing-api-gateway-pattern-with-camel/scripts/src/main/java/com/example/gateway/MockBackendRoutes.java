// src/main/java/com/example/gateway/MockBackendRoutes.java
package com.example.gateway;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MockBackendRoutes extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    // Mock User Service
    from("jetty:http://0.0.0.0:8081/users?httpMethodRestrict=GET,POST")
      .routeId("user-service")
      .log("Received request for User Service: ${body}")
      .choice()
        .when(header("CamelHttpMethod").isEqualTo("GET"))
          .setBody(constant("{\"users\": [{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john@example.com\"},"
            + "{\"id\": 2, \"name\": \"Jane Smith\", \"email\": \"jane@example.com\"}]}"))
        .when(header("CamelHttpMethod").isEqualTo("POST"))
          .setBody(constant("{\"message\": \"User created successfully\", \"id\": 3}"))
      .end()
      .setHeader("Content-Type", constant("application/json"));

    // Mock Product Service
    from("jetty:http://0.0.0.0:8082/products?httpMethodRestrict=GET,POST")
      .routeId("product-service")
      .log("Received request for Product Service: ${body}")
      .choice()
        .when(header("CamelHttpMethod").isEqualTo("GET"))
          .setBody(constant("{\"products\": [{\"id\": 1, \"name\": \"Laptop\", \"price\": 999.99}, {\"id\": 2,"
            + " \"name\": \"Mouse\", \"price\": 29.99}]}"))
        .when(header("CamelHttpMethod").isEqualTo("POST"))
          .setBody(constant("{\"message\": \"Product created successfully\", \"id\": 3}"))
      .end()
      .setHeader("Content-Type", constant("application/json"));

    // Mock Order Service
    from("jetty:http://0.0.0.0:8083/orders?httpMethodRestrict=GET,POST")
      .routeId("order-service")
      .log("Received request for Order Service: ${body}")
      .choice()
        .when(header("CamelHttpMethod").isEqualTo("GET"))
          .setBody(constant("{\"orders\": [{\"id\": 1, \"userId\": 1, \"productId\": 1, \"quantity\": 2, \"total\": 1999.98}]}"))
        .when(header("CamelHttpMethod").isEqualTo("POST"))
          .setBody(constant("{\"message\": \"Order created successfully\", \"id\": 2}"))
      .end()
      .setHeader("Content-Type", constant("application/json"));
  }
}
