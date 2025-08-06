package com.nameless.auth;

import com.nameless.queue.UserProducer;
import com.nameless.service.TokenHashingService;
import com.nameless.entity.verificationToken.model.VerificationToken;
import com.nameless.entity.verificationToken.repository.VerificationTokenRepository;
import com.nameless.service.VerificationTokenService;
import com.nameless.jwt.JwtService;
import com.nameless.dto.AuthRequestDTO;
import com.nameless.dto.AuthResponseDTO;
import com.nameless.dto.RegisterRequestDTO;
import com.nameless.entity.refreshToken.model.RefreshToken;
import com.nameless.entity.refreshToken.repository.RefreshTokenRepository;
import com.nameless.entity.user.model.User;
import com.nameless.entity.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final VerificationTokenService verificationTokenService;
  private final VerificationTokenRepository verificationTokenRepository;
  private final UserProducer userProducer;

  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshTokenExpirationDays;

  // Registers a new user by saving them in the database
  public boolean register(RegisterRequestDTO request) {
    // Check if username already exists
    Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
    if (userOptional.isPresent()) {
      return false;  // Username already exists
    }

    // Create and save new user
    User user = createUserFromRequest(request);
    userRepository.save(user);

    // Generate and send verification email to the user
    String verificationToken = UUID.randomUUID().toString();
    String verificationLink = generateVerificationLink(verificationToken);
    saveVerificationToken(verificationToken, request.getEmail());
    verificationTokenService.sendVerificationEmail(request.getEmail(), verificationLink);

    return true;
  }

  // Helper method to create a user from the registration request
  private User createUserFromRequest(RegisterRequestDTO request) {
    return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();
  }

  // Generates the verification link for the user
  private String generateVerificationLink(String verificationToken) {
    return "http://localhost:8080/api/v1/auth/verify/" + verificationToken;
  }

  // Saves the verification token for the user in the database
  private void saveVerificationToken(String token, String email) {
    verificationTokenService.saveToken(token, email, LocalDateTime.now().plusHours(1));
  }

  // Authenticates the user with their credentials and generates JWT tokens
  public AuthResponseDTO authenticate(AuthRequestDTO request) {
    // Authenticate user using the provided credentials
    authenticateUser(request);

    // Retrieve the user by username
    User user = findUserByUsername(request.getUsername());
    validateUserVerification(user);

    // Generate JWT access and refresh tokens
    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    // Revoke old tokens and save the new refresh token
    revokeAllUserTokens(user);
    saveUserToken(user, refreshToken);

    return buildAuthResponse(jwtToken, refreshToken);
  }

  // Helper method to authenticate the user with the authentication manager
  private void authenticateUser(AuthRequestDTO request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
  }

  // Helper method to find user by username
  private User findUserByUsername(String username) {
    return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
  }

  // Helper method to validate if the user account is verified
  private void validateUserVerification(User user) {
    Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUserEmail(user.getEmail());
    if (verificationToken.isEmpty() || !verificationToken.get().isUsed()) {
      throw new RuntimeException("Account is not verified");
    }
  }

  // Helper method to build the authentication response object
  private AuthResponseDTO buildAuthResponse(String jwtToken, String refreshToken) {
    return AuthResponseDTO.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  // Refreshes the user's access token using the provided refresh token
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    // Check if the authorization header contains a valid Bearer token
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    // Extract the refresh token from the header
    String refreshToken = authHeader.substring(7);
    String username = jwtService.extractUsernameFromRefresh(refreshToken);

    // Validate the refresh token and generate new tokens
    if (username == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    User user = findUserByUsername(username);
    if (jwtService.isRefreshTokenValid(refreshToken, user)) {
      String accessToken = jwtService.generateToken(user);
      sendAuthResponse(response, accessToken, refreshToken);
    } else {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  // Helper method to send the authentication response with new tokens
  private void sendAuthResponse(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
    AuthResponseDTO authResponse = buildAuthResponse(accessToken, refreshToken);
    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
  }

  // Retrieves the user's info from the authorization token in the request
  public Optional<User> getUserInfo(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return Optional.empty();
    }

    String token = authHeader.substring(7);
    if (!isTokenValid(token)) {
      return Optional.empty();
    }

    String username = jwtService.extractUsernameFromAccess(token);
    return username == null ? Optional.empty() : userRepository.findByUsername(username);
  }

  // Validates if the given token is still valid and not expired
  public boolean isTokenValid(String token) {
    if (token == null || jwtService.isAccessTokenExpired(token)) {
      return false;
    }

    String username = jwtService.extractUsernameFromAccess(token);
    if (username == null) {
      return false;
    }

    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isEmpty()) {
      return false;
    }

    User user = userOptional.get();
    return refreshTokenRepository.findAllValidTokenByUser(user.getId()).stream()
            .anyMatch(t -> !t.isRevoked());
  }

  // Saves the refresh token to the database for the user
  public void saveUserToken(User user, String refreshToken) {
    RefreshToken token = RefreshToken.builder()
            .user(user)
            .token(TokenHashingService.hashToken(refreshToken))
            .expiration(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
            .revoked(false)
            .build();
    refreshTokenRepository.save(token);
  }

  // Revokes all the refresh tokens for the user
  private void revokeAllUserTokens(User user) {
    refreshTokenRepository.findAllValidTokenByUser(user.getId()).forEach(token -> token.setRevoked(true));
    refreshTokenRepository.saveAll(refreshTokenRepository.findAllValidTokenByUser(user.getId()));
  }

  // Verifies the token and sends an email to notify the user if the verification is successful
  public boolean verify(String verToken) {
    if (verificationTokenService.verifyToken(verToken)) {
      VerificationToken verificationToken = verificationTokenRepository.findByToken(verToken);
      userProducer.sendUserToQueue(verificationToken.getUserEmail());
      return true;
    }
    return false;
  }

  // Generates a new verification token for the user and sends a verification email
  public void newVerifyToken(String userUsername) throws Exception {
    verificationTokenService.newVerifyToken(userUsername);
  }

  // Validates if the authorization header contains a valid token
  public boolean validateToken(String authorization) {
    return authorization != null && authorization.startsWith("Bearer ") && isTokenValid(authorization.substring(7));
  }
}
