package com.example.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;

/**
 * Configuration class for ActiveMQ connection setup
 */
public class ActiveMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConfig.class);

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    /**
     * Creates and configures ActiveMQ connection factory
     * @return Configured ConnectionFactory
     */
    public static ConnectionFactory createConnectionFactory() {
        logger.info("Creating ActiveMQ connection factory for broker: {}", BROKER_URL);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setUserName(USERNAME);
        connectionFactory.setPassword(PASSWORD);

        // Configure connection factory settings
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setMaxThreadPoolSize(50);

        return connectionFactory;
    }

    /**
     * Creates a pooled connection factory for better performance
     * @return Pooled ConnectionFactory
     */
    public static PooledConnectionFactory createPooledConnectionFactory() {
        logger.info("Creating pooled connection factory");

        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(createConnectionFactory());
        pooledConnectionFactory.setMaxConnections(10);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(100);

        return pooledConnectionFactory;
    }

    /**
     * Creates JMS component for Camel
     * @return Configured JmsComponent
     */
    public static JmsComponent createJmsComponent() {
        logger.info("Creating JMS component for Camel");

        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(createPooledConnectionFactory());

        // Configure JMS component settings
        jmsComponent.setConcurrentConsumers(5);
        jmsComponent.setMaxConcurrentConsumers(10);
        jmsComponent.setReceiveTimeout(5000);

        return jmsComponent;
    }
}
