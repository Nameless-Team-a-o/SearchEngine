package com.nameless.storage_server.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SubmissionsConsumer {

    private static final Logger logger = Logger.getLogger(SubmissionsConsumer.class.getName());

    @RabbitListener(queues = "SubmissionsQueue")
    public void receive(String message) {
        try {
            // Log the received message
            logger.info("Received message: " + message);

            String projectName = message.trim();
            logger.info("Received projectName: " + projectName);

        } catch (Exception e) {
            logger.severe("Error processing message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}