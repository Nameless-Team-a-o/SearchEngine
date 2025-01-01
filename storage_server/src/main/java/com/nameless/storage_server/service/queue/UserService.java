package com.nameless.storage_server.service.queue;

import com.nameless.storage_server.entity.User;
import com.nameless.storage_server.exception.UserCreationException;
import com.nameless.storage_server.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new IllegalArgumentException("User with username '" + username + "' already exists.");
        });

        User newUser = new User();
        newUser.setUsername(username);
        userRepository.save(newUser);

        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            throw new UserCreationException("Failed to create user: " + username, e);
        }
    }
}
