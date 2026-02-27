// routes/RestApiRoute.java
import org.apache.camel.builder.RouteBuilder;

public class RestApiRoute extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    restConfiguration()
      .component("platform-http")
      .port(8080);

    rest("/api")
      .get("/hello")
      .to("direct:hello")
      .get("/status")
      .to("direct:status");

    from("direct:hello")
      .setBody(constant("{\"message\": \"Hello from Camel K REST API\", \"timestamp\": \"" +
        System.currentTimeMillis() + "\"}"))
      .setHeader("Content-Type", constant("application/json"));

    from("direct:status")
      .setBody(constant("{\"status\": \"healthy\", \"service\": \"camel-k-rest-api\"}"))
      .setHeader("Content-Type", constant("application/json"));
  }
}
