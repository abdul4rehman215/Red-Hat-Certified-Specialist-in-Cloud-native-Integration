package com.example.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for Camel-ActiveMQ integration
 */
public class CamelActiveMQApplication {

    private static final Logger logger = LoggerFactory.getLogger(CamelActiveMQApplication.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting Camel-ActiveMQ Integration Application");

        // Create Camel Main instance
        Main main = new Main();

        // Add routes
        main.configure().addRoutesBuilder(new CamelRouteBuilder());

        // Add JMS component to Camel context
        CamelContext context = main.getCamelContext();
        context.addComponent("jms", ActiveMQConfig.createJmsComponent());

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Camel-ActiveMQ Integration Application");
            try {
                main.stop();
            } catch (Exception e) {
                logger.error("Error during shutdown", e);
            }
        }));

        logger.info("Camel-ActiveMQ Integration Application started successfully");
        logger.info("Press Ctrl+C to stop the application");

        // Start the application
        main.run(args);
    }
}
