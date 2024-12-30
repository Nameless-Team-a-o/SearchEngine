package com.nameless.storage_server.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.storage_server.service.FileUploadService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SubmissionsProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    public SubmissionsProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }


    public   void  sendToQueue(String projectName){
        try {
            // Convert username to JSON
            String projectNameJson = objectMapper.writeValueAsString(projectName);

            // Send the message to the exchange with the routing key
            rabbitTemplate.convertAndSend("SubmissionsExchange", "SubmissionsRoutingKey", projectNameJson);
            logger.info("Sent " +projectName + " to queue");
        } catch (JsonProcessingException e) {
            logger.severe("Error processing JSON for projectName: "+ projectName);
            throw new RuntimeException("Error processing JSON", e);  // Optional: rethrow the exception
        }
    }
}
