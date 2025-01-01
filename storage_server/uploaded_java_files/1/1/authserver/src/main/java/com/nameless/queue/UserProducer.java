package com.nameless.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.entity.user.model.User;
import com.nameless.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserProducer {
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendUserToQueue(String Email) {
        Optional<User> user = userRepository.findByEmail(Email);
        if (!user.isPresent()) {
            log.error("User not found for email: {}", Email);
            return;  // Early return if user is not found
        }
        String username = user.get().getUsername();

        try {
            // Convert username to JSON
            String usernameJson = objectMapper.writeValueAsString(username);

            // Send the message to the exchange with the routing key
            rabbitTemplate.convertAndSend("UserExchange", "UserRoutingKey", usernameJson);
            log.info("Sent user '{}' to queue", username);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for user: {}", Email, e);
            throw new RuntimeException("Error processing JSON", e);  // Optional: rethrow the exception
        }
    }
}
