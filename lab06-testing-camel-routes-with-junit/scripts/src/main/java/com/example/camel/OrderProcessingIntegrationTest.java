package com.example.camel;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProcessingIntegrationTest extends CamelTestSupport {

    @EndpointInject("mock:result")
    private MockEndpoint resultMock;

    @EndpointInject("mock:error")
    private MockEndpoint errorMock;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Error handling route
                onException(IllegalArgumentException.class)
                        .handled(true)
                        .log("Error processing order: ${exception.message}")
                        .setBody(simple("Error: ${exception.message}"))
                        .to("mock:error");

                // Main integration route
                from("direct:orderIntegration")
                        .routeId("order-integration-test")
                        .log("Starting order integration test")
                        .to("direct:validateOrder")
                        .log("Order validated successfully")
                        .to("direct:transformOrder")
                        .log("Order transformed successfully")
                        .to("mock:result");

                // Include the original routes
                from("direct:processOrder")
                        .routeId("order-processing-route")
                        .log("Processing order: ${body}")
                        .choice()
                            .when(xpath("/order/priority[text()='HIGH']"))
                                .log("High priority order detected")
                                .to("mock:highPriorityQueue")
                            .when(xpath("/order/priority[text()='MEDIUM']"))
                                .log("Medium priority order detected")
                                .to("mock:mediumPriorityQueue")
                            .otherwise()
                                .log("Low priority order detected")
                                .to("mock:lowPriorityQueue")
                        .end();

                from("direct:validateOrder")
                        .routeId("order-validation-route")
                        .log("Validating order: ${body}")
                        .choice()
                            .when(xpath("/order/customerId[text()='']"))
                                .throwException(new IllegalArgumentException("Customer ID is required"))
                            .when(xpath("/order/amount[number(.) <= 0]"))
                                .throwException(new IllegalArgumentException("Order amount must be positive"))
                            .otherwise()
                                .log("Order validation successful")
                                .setHeader("ValidationStatus", constant("VALID"))
                        .end();

                from("direct:transformOrder")
                        .routeId("order-transformation-route")
                        .log("Transforming order format")
                        .setHeader("ProcessedTimestamp", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
                        .transform().xpath("/order/customerId", String.class)
                        .setHeader("CustomerId", body());
            }
        };
    }

    @BeforeEach
    void setUp() {
        resultMock.reset();
        errorMock.reset();
    }

    @Test
    @DisplayName("Test complete order processing integration flow")
    void testCompleteOrderProcessingFlow() throws Exception {
        // Arrange
        String validOrder = """
        <order>
          <orderId>INT001</orderId>
          <customerId>CUST_INTEGRATION</customerId>
          <priority>HIGH</priority>
          <amount>2000.00</amount>
          <product>Integration Test Product</product>
        </order>
        """;

        // Set expectations
        resultMock.expectedMessageCount(1);
        resultMock.expectedBodiesReceived("CUST_INTEGRATION");
        resultMock.expectedHeaderReceived("CustomerId", "CUST_INTEGRATION");
        resultMock.allMessages().header("ProcessedTimestamp").isNotNull();
        errorMock.expectedMessageCount(0);

        // Act
        template.sendBody("direct:orderIntegration", validOrder);

        // Assert
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test error handling in integration flow")
    void testErrorHandlingIntegrationFlow() throws Exception {
        // Arrange
        String invalidOrder = """
        <order>
          <orderId>INT002</orderId>
          <customerId></customerId>
          <priority>HIGH</priority>
          <amount>1000.00</amount>
          <product>Error Test Product</product>
        </order>
        """;

        // Set expectations
        resultMock.expectedMessageCount(0);
        errorMock.expectedMessageCount(1);
        errorMock.expectedBodiesReceived("Error: Customer ID is required");

        // Act
        template.sendBody("direct:orderIntegration", invalidOrder);

        // Assert
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test concurrent order processing")
    void testConcurrentOrderProcessing() throws Exception {
        // Arrange
        int numberOfOrders = 5;
        resultMock.expectedMessageCount(numberOfOrders);
        errorMock.expectedMessageCount(0);

        // Act - Send multiple orders concurrently
        for (int i = 1; i <= numberOfOrders; i++) {
            String order = String.format("""
            <order>
              <orderId>CONCURRENT_%d</orderId>
              <customerId>CUST_%d</customerId>
              <priority>MEDIUM</priority>
              <amount>%d00.00</amount>
              <product>Concurrent Test Product %d</product>
            </order>
            """, i, i, i, i);

            template.asyncSendBody("direct:orderIntegration", order);
        }

        // Assert
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);

        // Verify all orders were processed
        assertEquals(numberOfOrders, resultMock.getReceivedCounter());
    }
}
