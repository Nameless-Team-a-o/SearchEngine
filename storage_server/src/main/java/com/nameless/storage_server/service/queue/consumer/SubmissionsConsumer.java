package com.nameless.storage_server.service.queue.consumer;

import com.nameless.storage_server.service.processor.SubmissionProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SubmissionsConsumer {

    private static final Logger logger = Logger.getLogger(SubmissionsConsumer.class.getName());
    private final SubmissionProcessor submissionProcessor;

    @Autowired
    public SubmissionsConsumer(SubmissionProcessor submissionProcessor) {
        this.submissionProcessor = submissionProcessor;
    }

    @RabbitListener(queues = "SubmissionsQueue")
    public void receive(Long projectId) {
        try {
            // Log the received message
            logger.info("Received message: " + projectId);

            logger.info("Received projectId: " + projectId);
            submissionProcessor.processUnprocessedSubmissions(projectId);

        } catch (Exception e) {
            logger.severe("Error processing message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}