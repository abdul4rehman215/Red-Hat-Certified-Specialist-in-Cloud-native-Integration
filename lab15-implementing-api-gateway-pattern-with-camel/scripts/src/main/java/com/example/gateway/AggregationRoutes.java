// src/main/java/com/example/gateway/AggregationRoutes.java
package com.example.gateway;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AggregationRoutes extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    // Dashboard aggregation route
    from("direct:aggregate-dashboard")
      .routeId("dashboard-aggregation")
      .log("Gateway: Aggregating dashboard data")
      .multicast(new DashboardAggregationStrategy())
        .parallelProcessing()
        .to("direct:fetch-users-for-dashboard")
        .to("direct:fetch-products-for-dashboard")
        .to("direct:fetch-orders-for-dashboard")
      .end()
      .log("Gateway: Dashboard aggregation complete: ${body}");

    // Individual service calls for dashboard
    from("direct:fetch-users-for-dashboard")
      .routeId("fetch-users-dashboard")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .doTry()
        .to("http://localhost:8081/users?bridgeEndpoint=true&connectTimeout=3000&socketTimeout=5000")
        .setProperty("serviceType", constant("users"))
      .doCatch(Exception.class)
        .log("Error fetching users for dashboard: ${exception.message}")
        .setBody(constant("{\"users\": [], \"error\": \"Service unavailable\"}"))
        .setProperty("serviceType", constant("users"))
      .end();

    from("direct:fetch-products-for-dashboard")
      .routeId("fetch-products-dashboard")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .doTry()
        .to("http://localhost:8082/products?bridgeEndpoint=true&connectTimeout=3000&socketTimeout=5000")
        .setProperty("serviceType", constant("products"))
      .doCatch(Exception.class)
        .log("Error fetching products for dashboard: ${exception.message}")
        .setBody(constant("{\"products\": [], \"error\": \"Service unavailable\"}"))
        .setProperty("serviceType", constant("products"))
      .end();

    from("direct:fetch-orders-for-dashboard")
      .routeId("fetch-orders-dashboard")
      .removeHeaders("CamelHttp*")
      .setHeader("CamelHttpMethod", constant("GET"))
      .doTry()
        .to("http://localhost:8083/orders?bridgeEndpoint=true&connectTimeout=3000&socketTimeout=5000")
        .setProperty("serviceType", constant("orders"))
      .doCatch(Exception.class)
        .log("Error fetching orders for dashboard: ${exception.message}")
        .setBody(constant("{\"orders\": [], \"error\": \"Service unavailable\"}"))
        .setProperty("serviceType", constant("orders"))
      .end();
  }

  // Custom aggregation strategy for dashboard data
  public static class DashboardAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

      if (oldExchange == null) {
        // First exchange - initialize the aggregated body
        String serviceType = newExchange.getProperty("serviceType", String.class);
        String responseBody = newExchange.getIn().getBody(String.class);

        String aggregatedBody = String.format(
          "{\"dashboard\": {\"%s\": %s}}",
          serviceType, responseBody
        );

        newExchange.getIn().setBody(aggregatedBody);
        return newExchange;

      } else {
        // Subsequent exchanges - merge with existing data
        String existingBody = oldExchange.getIn().getBody(String.class);
        String serviceType = newExchange.getProperty("serviceType", String.class);
        String responseBody = newExchange.getIn().getBody(String.class);

        // Simple JSON merging (in production, use proper JSON library)
        String mergedBody = existingBody.substring(0, existingBody.length() - 2) +
          String.format(", \"%s\": %s}}", serviceType, responseBody);

        oldExchange.getIn().setBody(mergedBody);
        return oldExchange;
      }
    }
  }
}
