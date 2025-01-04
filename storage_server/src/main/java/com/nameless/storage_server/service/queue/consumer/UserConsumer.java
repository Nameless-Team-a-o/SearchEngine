package com.nameless.storage_server.service.queue.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.storage_server.service.user.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    private final UserService userService;

    @Autowired
    public UserConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = "UserQueue")
    public void receive(String message) {
        try {
            // Deserialize the message to ensure it's parsed correctly
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(message);
            String userName = rootNode.asText(); // Extract the username from the JSON

            System.out.println("Received username: " + userName);

            // Log received username and call the service
            userService.createUser(userName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
