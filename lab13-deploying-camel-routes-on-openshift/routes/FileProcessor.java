// routes/FileProcessor.java
import org.apache.camel.builder.RouteBuilder;

public class FileProcessor extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    from("timer:tick?period=10000")
      .setBody(constant("Hello from Camel K on OpenShift - " + System.currentTimeMillis()))
      .log("Processing message: ${body}")
      .to("log:info");
  }
}
