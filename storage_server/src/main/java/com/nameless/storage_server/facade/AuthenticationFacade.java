package com.nameless.storage_server.facade;

import com.nameless.storage_server.entity.User;
import com.nameless.storage_server.service.jwt.JwtService;
import com.nameless.storage_server.service.user.UserService;

public class AuthenticationFacade {
    private final JwtService jwtService;
    private final UserService userService;

    public AuthenticationFacade(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public User getUserFromToken(String token) {
        String username = jwtService.extractUsernameFromAccess(token);
        return userService.getUserByUsername(username);
    }
}
