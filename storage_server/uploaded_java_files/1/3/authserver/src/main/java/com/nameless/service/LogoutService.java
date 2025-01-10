package com.nameless.service;

import com.nameless.entity.refreshToken.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    // Get the Authorization header containing the refresh token
    final String authHeader = request.getHeader("Authorization");

    // If the Authorization header is missing or does not contain a Bearer token, return early
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    // Extract the raw refresh token from the Authorization header
    String rawRefreshToken = authHeader.substring(7);
    String hashedRefreshToken = TokenHashingService.hashToken(rawRefreshToken);

    // Find the stored refresh token by its hashed value
    var storedToken = refreshTokenRepository.findByTokenHash(hashedRefreshToken).orElse(null);

    // If the token exists, revoke it and set its expiration to a past date
    if (storedToken != null) {
      storedToken.setRevoked(true);  // Mark the token as revoked
      storedToken.setExpiration(LocalDateTime.now().minusDays(1));  // Set expiration to a past date
      refreshTokenRepository.save(storedToken);  // Persist the changes to the repository

      // Clear the SecurityContext to log the user out
      SecurityContextHolder.clearContext();
    }
  }
}
