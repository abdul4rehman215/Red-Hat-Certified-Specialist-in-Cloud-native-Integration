package com.alnafi.camel.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedErrorSimulation {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedErrorSimulation.class);
    private final AtomicInteger callCount = new AtomicInteger(0);
    private final Random random = new Random();

    public String processComplexMessage(String message) throws Exception {
        int attempt = callCount.incrementAndGet();
        logger.info("Processing complex message attempt #{}: {}", attempt, message);

        // Simulate database connection errors (transient)
        if (message.contains("DB_ERROR")) {
            if (attempt < 3) {
                throw new RuntimeException("Database connection failed - attempt " + attempt);
            }
            logger.info("Database connection recovered on attempt {}", attempt);
        }

        // Simulate network timeouts (transient with random recovery)
        else if (message.contains("NETWORK_TIMEOUT")) {
            if (random.nextDouble() < 0.7) { // 70% chance of failure
                throw new RuntimeException("Network timeout occurred");
            }
            logger.info("Network recovered successfully");
        }

        // Simulate service unavailable (transient)
        else if (message.contains("SERVICE_UNAVAILABLE")) {
            if (attempt <= 2) {
                throw new RuntimeException("External service unavailable");
            }
            logger.info("External service recovered");
        }

        // Simulate permanent failures
        else if (message.contains("PERMANENT_FAILURE")) {
            throw new RuntimeException("Permanent system failure - cannot recover");
        }

        // Simulate data format errors (permanent)
        else if (message.contains("INVALID_FORMAT")) {
            throw new IllegalArgumentException("Message format is invalid and cannot be processed");
        }

        // Simulate authentication errors (permanent)
        else if (message.contains("AUTH_ERROR")) {
            throw new SecurityException("Authentication failed - invalid credentials");
        }

        // Random failures for stress testing
        else if (message.contains("RANDOM_ERROR")) {
            if (random.nextDouble() < 0.3) { // 30% chance of failure
                throw new RuntimeException("Random failure occurred");
            }
        }

        logger.info("Message processed successfully: {}", message);
        return "Processed successfully: " + message + " (attempt " + attempt + ")";
    }

    public void resetCounters() {
        callCount.set(0);
    }
}
