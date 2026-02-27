package com.example.camel;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProcessingRouteTest extends CamelTestSupport {

    @EndpointInject("mock:highPriorityQueue")
    private MockEndpoint highPriorityMock;

    @EndpointInject("mock:mediumPriorityQueue")
    private MockEndpoint mediumPriorityMock;

    @EndpointInject("mock:lowPriorityQueue")
    private MockEndpoint lowPriorityMock;

    @EndpointInject("mock:orderTransformed")
    private MockEndpoint orderTransformedMock;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new OrderProcessingRoute();
    }

    @Override
    protected String isMockEndpoints() {
        return "direct:highPriorityQueue|direct:mediumPriorityQueue|direct:lowPriorityQueue|direct:orderTransformed";
    }

    @BeforeEach
    void setUp() {
        highPriorityMock.reset();
        mediumPriorityMock.reset();
        lowPriorityMock.reset();
        orderTransformedMock.reset();
    }

    // -----------------------------
    // Test Data Builders
    // -----------------------------

    private String createHighPriorityOrder() {
        return """
        <order>
          <orderId>12345</orderId>
          <customerId>CUST001</customerId>
          <priority>HIGH</priority>
          <amount>1500.00</amount>
          <product>Laptop</product>
        </order>
        """;
    }

    private String createMediumPriorityOrder() {
        return """
        <order>
          <orderId>12346</orderId>
          <customerId>CUST002</customerId>
          <priority>MEDIUM</priority>
          <amount>750.00</amount>
          <product>Tablet</product>
        </order>
        """;
    }

    private String createLowPriorityOrder() {
        return """
        <order>
          <orderId>12347</orderId>
          <customerId>CUST003</customerId>
          <priority>LOW</priority>
          <amount>250.00</amount>
          <product>Mouse</product>
        </order>
        """;
    }

    private String createInvalidOrderNoCustomer() {
        return """
        <order>
          <orderId>12348</orderId>
          <customerId></customerId>
          <priority>HIGH</priority>
          <amount>1000.00</amount>
          <product>Keyboard</product>
        </order>
        """;
    }

    private String createInvalidOrderNegativeAmount() {
        return """
        <order>
          <orderId>12349</orderId>
          <customerId>CUST004</customerId>
          <priority>MEDIUM</priority>
          <amount>-100.00</amount>
          <product>Monitor</product>
        </order>
        """;
    }

    // -----------------------------
    // Tests
    // -----------------------------

    @Test
    @DisplayName("Test basic route creation and context startup")
    void testRouteCreation() throws Exception {
        assertEquals(3, context.getRoutes().size());
        assertTrue(context.getRouteController()
                .getRouteStatus("order-processing-route")
                .isStarted());
    }

    @Test
    @DisplayName("Test high priority order routing")
    void testHighPriorityOrderRouting() throws Exception {
        String highPriorityOrder = createHighPriorityOrder();

        highPriorityMock.expectedMessageCount(1);
        highPriorityMock.expectedBodiesReceived(highPriorityOrder);
        mediumPriorityMock.expectedMessageCount(0);
        lowPriorityMock.expectedMessageCount(0);

        template.sendBody("direct:processOrder", highPriorityOrder);

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test medium priority order routing")
    void testMediumPriorityOrderRouting() throws Exception {
        String mediumPriorityOrder = createMediumPriorityOrder();

        highPriorityMock.expectedMessageCount(0);
        mediumPriorityMock.expectedMessageCount(1);
        mediumPriorityMock.expectedBodiesReceived(mediumPriorityOrder);
        lowPriorityMock.expectedMessageCount(0);

        template.sendBody("direct:processOrder", mediumPriorityOrder);

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test low priority order routing (default case)")
    void testLowPriorityOrderRouting() throws Exception {
        String lowPriorityOrder = createLowPriorityOrder();

        highPriorityMock.expectedMessageCount(0);
        mediumPriorityMock.expectedMessageCount(0);
        lowPriorityMock.expectedMessageCount(1);
        lowPriorityMock.expectedBodiesReceived(lowPriorityOrder);

        template.sendBody("direct:processOrder", lowPriorityOrder);

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test valid order processing through validation")
    void testValidOrderValidation() throws Exception {
        String validOrder = createHighPriorityOrder();

        highPriorityMock.expectedMessageCount(1);
        highPriorityMock.expectedBodiesReceived(validOrder);
        highPriorityMock.expectedHeaderReceived("ValidationStatus", "VALID");

        template.sendBody("direct:validateOrder", validOrder);

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test invalid order with empty customer ID")
    void testInvalidOrderEmptyCustomerId() throws Exception {
        String invalidOrder = createInvalidOrderNoCustomer();

        highPriorityMock.expectedMessageCount(0);
        mediumPriorityMock.expectedMessageCount(0);
        lowPriorityMock.expectedMessageCount(0);

        assertThrows(Exception.class, () -> template.sendBody("direct:validateOrder", invalidOrder));

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test invalid order with negative amount")
    void testInvalidOrderNegativeAmount() throws Exception {
        String invalidOrder = createInvalidOrderNegativeAmount();

        highPriorityMock.expectedMessageCount(0);
        mediumPriorityMock.expectedMessageCount(0);
        lowPriorityMock.expectedMessageCount(0);

        assertThrows(Exception.class, () -> template.sendBody("direct:validateOrder", invalidOrder));

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test order transformation route")
    void testOrderTransformation() throws Exception {
        String originalOrder = createHighPriorityOrder();

        orderTransformedMock.expectedMessageCount(1);
        orderTransformedMock.expectedBodiesReceived("CUST001");
        orderTransformedMock.expectedHeaderReceived("CustomerId", "CUST001");
        orderTransformedMock.allMessages().header("ProcessedTimestamp").isNotNull();

        template.sendBody("direct:transformOrder", originalOrder);

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test multiple orders processing in sequence")
    void testMultipleOrdersProcessing() throws Exception {
        String highOrder = createHighPriorityOrder();
        String mediumOrder = createMediumPriorityOrder();
        String lowOrder = createLowPriorityOrder();

        highPriorityMock.expectedMessageCount(1);
        mediumPriorityMock.expectedMessageCount(1);
        lowPriorityMock.expectedMessageCount(1);

        template.sendBody("direct:processOrder", highOrder);
        template.sendBody("direct:processOrder", mediumOrder);
        template.sendBody("direct:processOrder", lowOrder);

        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("Test message content and headers with custom assertions")
    void testMessageContentAndHeaders() throws Exception {
        String order = createHighPriorityOrder();

        highPriorityMock.expectedMessageCount(1);

        highPriorityMock.allMessages().body().contains("CUST001");
        highPriorityMock.allMessages().body().contains("HIGH");
        highPriorityMock.allMessages().body().contains("1500.00");

        template.sendBody("direct:processOrder", order);

        assertMockEndpointsSatisfied();

        String receivedBody = highPriorityMock.getReceivedExchanges()
                .get(0).getIn().getBody(String.class);

        assertTrue(receivedBody.contains("<priority>HIGH</priority>"),
                "Message should contain HIGH priority");
        assertTrue(receivedBody.contains("<customerId>CUST001</customerId>"),
                "Message should contain correct customer ID");
    }
}
