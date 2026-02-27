// src/main/java/com/example/gateway/RateLimitingRoutes.java
package com.example.gateway;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RateLimitingRoutes extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    // Enhanced API Gateway Routes with Rate Limiting
    from("direct:get-users")
      .routeId("gateway-get-users-with-throttle")
      .throttle(10).timePeriodMillis(60000) // 10 requests per minute
      .log("Gateway: Routing GET /users request (Rate Limited)")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .doTry()
        .to("http://localhost:8081/users?bridgeEndpoint=true&connectTimeout=5000&socketTimeout=10000")
        .log("Gateway: Response from User Service: ${body}")
      .doCatch(Exception.class)
        .log("Gateway: Error calling User Service: ${exception.message}")
        .setBody(constant("{\"error\": \"Service temporarily unavailable\"}"))
        .setHeader("Content-Type", constant("application/json"))
        .setHeader("CamelHttpResponseCode", constant(503))
      .end();

    from("direct:get-products")
      .routeId("gateway-get-products-with-throttle")
      .throttle(15).timePeriodMillis(60000) // 15 requests per minute
      .log("Gateway: Routing GET /products request (Rate Limited)")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .doTry()
        .to("http://localhost:8082/products?bridgeEndpoint=true&connectTimeout=5000&socketTimeout=10000")
        .log("Gateway: Response from Product Service: ${body}")
      .doCatch(Exception.class)
        .log("Gateway: Error calling Product Service: ${exception.message}")
        .setBody(constant("{\"error\": \"Service temporarily unavailable\"}"))
        .setHeader("Content-Type", constant("application/json"))
        .setHeader("CamelHttpResponseCode", constant(503))
      .end();

    from("direct:get-orders")
      .routeId("gateway-get-orders-with-throttle")
      .throttle(5).timePeriodMillis(60000) // 5 requests per minute (more restrictive)
      .log("Gateway: Routing GET /orders request (Rate Limited)")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .doTry()
        .to("http://localhost:8083/orders?bridgeEndpoint=true&connectTimeout=5000&socketTimeout=10000")
        .log("Gateway: Response from Order Service: ${body}")
      .doCatch(Exception.class)
        .log("Gateway: Error calling Order Service: ${exception.message}")
        .setBody(constant("{\"error\": \"Service temporarily unavailable\"}"))
        .setHeader("Content-Type", constant("application/json"))
        .setHeader("CamelHttpResponseCode", constant(503))
      .end();
  }
}
