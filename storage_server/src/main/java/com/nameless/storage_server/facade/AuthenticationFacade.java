package com.nameless.storage_server.facade;

import com.nameless.storage_server.entity.User;
import com.nameless.storage_server.facade.interfaces.OperationFacade;
import com.nameless.storage_server.service.jwt.JwtService;
import com.nameless.storage_server.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements OperationFacade<Object, Object> {
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

    @Override
    public ResponseEntity<Object> execute(Object o) {
        return null;
    }
}
