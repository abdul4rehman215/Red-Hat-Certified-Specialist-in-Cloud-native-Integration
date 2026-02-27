// routes/MessageTransformer.java
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class MessageTransformer extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    from("timer:transform?period=15000")
      .setBody(constant("{\"name\": \"John Doe\", \"age\": 30, \"city\": \"New York\"}"))
      .log("Original message: ${body}")
      .unmarshal().json(JsonLibrary.Jackson)
      .process(exchange -> {
        java.util.Map<String, Object> body = exchange.getIn().getBody(java.util.Map.class);
        body.put("processed", true);
        body.put("processedAt", System.currentTimeMillis());
        exchange.getIn().setBody(body);
      })
      .marshal().json(JsonLibrary.Jackson)
      .log("Transformed message: ${body}")
      .to("log:transformed");
  }
}
