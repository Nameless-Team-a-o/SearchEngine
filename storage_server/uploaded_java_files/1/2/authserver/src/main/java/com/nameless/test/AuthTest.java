package com.nameless.test;

import com.nameless.auth.AuthenticationController;
import com.nameless.auth.AuthenticationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void validateToken_valid() throws Exception {
        when(authenticationService.validateToken("Bearer jwt_token")).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/validate_token")
                        .header("Authorization", "Bearer jwt_token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }

    @Test
    void validateToken_invalid() throws Exception {
        when(authenticationService.validateToken("Bearer invalid_token")).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/validate_token")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token"));
    }
}

