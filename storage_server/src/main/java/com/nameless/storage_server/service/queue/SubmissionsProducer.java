package com.nameless.storage_server.service.queue;
import com.nameless.storage_server.service.file.FileUploadService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SubmissionsProducer {
    private final RabbitTemplate rabbitTemplate;
     static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

     @Autowired
    public SubmissionsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToQueue(Long projectId) {
        try {
            rabbitTemplate.convertAndSend("SubmissionsExchange", "SubmissionsRoutingKey", projectId);
            logger.info("Sent " + projectId + " to queue");
        } catch (Exception e) {
            logger.severe("Error sending message: " + e.getMessage());
            throw new RuntimeException("Error sending message", e);
        }
    }
}
