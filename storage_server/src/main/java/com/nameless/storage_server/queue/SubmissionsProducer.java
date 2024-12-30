package com.nameless.storage_server.queue;
import com.nameless.storage_server.service.FileUploadService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SubmissionsProducer {
    private final RabbitTemplate rabbitTemplate;
     static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    public SubmissionsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToQueue(String projectName) {
        try {
            rabbitTemplate.convertAndSend("SubmissionsExchange", "SubmissionsRoutingKey", projectName);
            logger.info("Sent " + projectName + " to queue");
        } catch (Exception e) {
            logger.severe("Error sending message: " + e.getMessage());
            throw new RuntimeException("Error sending message", e);
        }
    }
}
