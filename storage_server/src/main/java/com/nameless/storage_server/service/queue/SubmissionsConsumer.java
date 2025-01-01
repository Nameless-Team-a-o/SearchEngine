package com.nameless.storage_server.service.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SubmissionsConsumer {

    private static final Logger logger = Logger.getLogger(SubmissionsConsumer.class.getName());
    private final SubmissionProcessingService submissionProcessingService;

    public SubmissionsConsumer(SubmissionProcessingService submissionProcessingService) {
        this.submissionProcessingService = submissionProcessingService;
    }

    @RabbitListener(queues = "SubmissionsQueue")
    public void receive(Long projectId) {
        try {
            // Log the received message
            logger.info("Received message: " + projectId);

            logger.info("Received projectId: " + projectId);
            submissionProcessingService.processSubmissionsByProject(projectId);

        } catch (Exception e) {
            logger.severe("Error processing message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}