// src/main/java/com/example/gateway/ApiGatewayRoutes.java
package com.example.gateway;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class ApiGatewayRoutes extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    // Configure REST DSL
    restConfiguration()
      .component("jetty")
      .host("0.0.0.0")
      .port(8080)
      .bindingMode(RestBindingMode.json)
      .enableCORS(true);

    // API Gateway Routes
    rest("/api/v1")
      .get("/users").to("direct:get-users")
      .post("/users").to("direct:create-user")
      .get("/products").to("direct:get-products")
      .post("/products").to("direct:create-product")
      .get("/orders").to("direct:get-orders")
      .post("/orders").to("direct:create-order")
      .get("/dashboard").to("direct:aggregate-dashboard");

    // Route implementations with mediation logic
    from("direct:get-users")
      .routeId("gateway-get-users")
      .log("Gateway: Routing GET /users request")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .to("http://localhost:8081/users?bridgeEndpoint=true")
      .log("Gateway: Response from User Service: ${body}");

    from("direct:create-user")
      .routeId("gateway-create-user")
      .log("Gateway: Routing POST /users request")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("POST"))
      .to("http://localhost:8081/users?bridgeEndpoint=true")
      .log("Gateway: Response from User Service: ${body}");

    from("direct:get-products")
      .routeId("gateway-get-products")
      .log("Gateway: Routing GET /products request")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .to("http://localhost:8082/products?bridgeEndpoint=true")
      .log("Gateway: Response from Product Service: ${body}");

    from("direct:create-product")
      .routeId("gateway-create-product")
      .log("Gateway: Routing POST /products request")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("POST"))
      .to("http://localhost:8082/products?bridgeEndpoint=true")
      .log("Gateway: Response from Product Service: ${body}");

    from("direct:get-orders")
      .routeId("gateway-get-orders")
      .log("Gateway: Routing GET /orders request")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .to("http://localhost:8083/orders?bridgeEndpoint=true")
      .log("Gateway: Response from Order Service: ${body}");

    from("direct:create-order")
      .routeId("gateway-create-order")
      .log("Gateway: Routing POST /orders request")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("POST"))
      .to("http://localhost:8083/orders?bridgeEndpoint=true")
      .log("Gateway: Response from Order Service: ${body}");
  }
}
