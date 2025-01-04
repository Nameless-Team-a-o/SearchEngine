package com.nameless.auth;

import com.nameless.dto.UserInfoDTO;
import com.nameless.entity.user.model.User;
import com.nameless.dto.AuthRequestDTO;
import com.nameless.dto.AuthResponseDTO;
import com.nameless.dto.RegisterRequestDTO;
import com.nameless.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  // Registration endpoint remains unchanged
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
    if(service.register(request)) {
      return ResponseEntity.status(HttpStatus.CREATED).body("Register successful, please check your email to verify your account.");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Register failed");
  }

  // Login endpoint - authenticate with username (instead of email) and return JWT token
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO request) {
    return ResponseEntity.ok(service.authenticate(request)); // Authenticate using username, not email
  }

  // Refresh token endpoint remains unchanged
  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }

  // Get user info - now returns username instead of email
  @GetMapping("/user_info")
  public ResponseEntity<UserInfoDTO> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
    try {
      Optional<User> optionalUser = service.getUserInfo(authorizationHeader);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .username(user.getUsername()) // Changed to use getUsername() for username
                .email(user.getEmail()) // Optional, still returning email here
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(userInfo);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Email verification - This endpoint can still use email for verification
  @GetMapping("/verify/{verToken}")
  public ResponseEntity<String> verify(@PathVariable("verToken") String verToken) {
    if(service.verify(verToken))
      return ResponseEntity.ok("Your account has been verified.");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
  }

  // Renew token - Now using username instead of email in the payload
  @PostMapping("/renew-token")
  public ResponseEntity<String> renewToken(@RequestBody Map<String, String> payload) {
    String username = payload.get("username"); // Changed to use username
    if (username == null) {
      return ResponseEntity.badRequest().body("Missing username in request");
    }
    try {
      service.newVerifyToken(username); // Using username for token renewal
      return ResponseEntity.ok("Verification email sent. Please check your email to verify your account.");
    } catch (Exception e) {
      if (e.getMessage().equals("No account found for the provided username.")) { // Check for username-based error
        return ResponseEntity.status(400).body("No account found for the provided username. Please register first.");
      } else if (e.getMessage().equals("Account is already verified.")) {
        return ResponseEntity.status(400).body("Your account is already verified. No need to request another verification.");
      } else {
        return ResponseEntity.status(500).body("Error occurred while renewing token.");
      }
    }
  }

  // Validate token remains unchanged
  @GetMapping("/validate_token")
  public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorization) {
    System.out.println("Validating token: " + authorization);  // For debugging
    if (service.validateToken(authorization)) {
      System.out.println("Token is valid");
      return ResponseEntity.ok("Token is valid");
    } else {
      System.out.println("Invalid token");

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
  }
}
