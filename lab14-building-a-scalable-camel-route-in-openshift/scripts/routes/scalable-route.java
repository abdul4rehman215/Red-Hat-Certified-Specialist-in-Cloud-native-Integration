// routes/scalable-route.java
// camel-k: language=java
// camel-k: dependency=camel-jackson
// camel-k: dependency=camel-undertow
// camel-k: property=server.port=8080

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ScalableRoute extends RouteBuilder {

    private static final AtomicLong requestCounter = new AtomicLong(0);

    @Override
    public void configure() throws Exception {

        // Configure REST endpoint
        restConfiguration()
                .component("undertow")
                .host("0.0.0.0")
                .port(8080)
                .bindingMode(RestBindingMode.json);

        // Health check endpoint for Kubernetes probes
        rest("/health")
                .get()
                .to("direct:health");

        // Main API endpoint
        rest("/api/v1")
                .get("/process/{id}")
                .to("direct:processRequest")
                .post("/data")
                .to("direct:processData");

        // Metrics endpoint for monitoring
        rest("/metrics")
                .get()
                .to("direct:metrics");

        // Health check route
        from("direct:health")
                .setBody(constant("{\"status\":\"UP\",\"service\":\"camel-scaling-service\"}"))
                .setHeader("Content-Type", constant("application/json"));

        // Process GET request route
        from("direct:processRequest")
                .log("Processing request for ID: ${header.id} on pod: ${env:HOSTNAME}")
                .process(exchange -> {
                    String id = exchange.getIn().getHeader("id", String.class);
                    long count = requestCounter.incrementAndGet();

                    // Simulate some processing time
                    Thread.sleep(100 + (long) (Math.random() * 200));

                    Map<String, Object> response = new HashMap<>();
                    response.put("id", id);
                    response.put("processed_by", System.getenv("HOSTNAME"));
                    response.put("request_count", count);
                    response.put("timestamp", System.currentTimeMillis());
                    response.put("status", "processed");

                    exchange.getIn().setBody(response);
                })
                .setHeader("Content-Type", constant("application/json"));

        // Process POST data route
        from("direct:processData")
                .log("Processing POST data on pod: ${env:HOSTNAME}")
                .process(exchange -> {
                    long count = requestCounter.incrementAndGet();

                    // Simulate heavier processing for POST requests
                    Thread.sleep(200 + (long) (Math.random() * 300));

                    Map<String, Object> response = new HashMap<>();
                    response.put("processed_by", System.getenv("HOSTNAME"));
                    response.put("request_count", count);
                    response.put("timestamp", System.currentTimeMillis());
                    response.put("status", "data_processed");
                    response.put("message", "Data processing completed successfully");

                    exchange.getIn().setBody(response);
                })
                .setHeader("Content-Type", constant("application/json"));

        // Metrics route for monitoring
        from("direct:metrics")
                .process(exchange -> {
                    Map<String, Object> metrics = new HashMap<>();
                    metrics.put("total_requests", requestCounter.get());
                    metrics.put("pod_name", System.getenv("HOSTNAME"));
                    metrics.put("uptime", System.currentTimeMillis());

                    exchange.getIn().setBody(metrics);
                })
                .setHeader("Content-Type", constant("application/json"));
    }
}
