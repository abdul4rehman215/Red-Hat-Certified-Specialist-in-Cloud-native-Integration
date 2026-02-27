package com.alnafi.camel.errorhandling;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;

public class JmsConfig {

    public static void configureJms(CamelContext context) {
        // Create ActiveMQ connection factory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("vm://localhost?broker.persistent=false");

        // Create JMS component
        JmsComponent jmsComponent = JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);

        // Add JMS component to Camel context
        context.addComponent("jms", jmsComponent);
    }
}
