package com.nameless.jwt;

import com.nameless.entity.refreshToken.model.RefreshToken;
import com.nameless.entity.refreshToken.repository.RefreshTokenRepository;
import com.nameless.service.TokenHashingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${application.security.jwt.accessSecret-key}")
  private String accessSecretKey ;
  @Value("${application.security.jwt.refresh-token.refreshSecret-key}")
  private String refreshSecretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  // Extract username from the access token
  public String extractUsernameFromAccess(String token) {
    return extractClaim(token, Claims::getSubject, accessSecretKey);  // Uses 'username' as subject
  }

  // Extract username from the refresh token
  public String extractUsernameFromRefresh(String token) {
    return extractClaim(token, Claims::getSubject, refreshSecretKey);  // Uses 'username' as subject
  }

  // Extract any claim from the token using a provided key
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
    final Claims claims = extractAllClaims(token, key);
    return claimsResolver.apply(claims);
  }

  // Generate an access token for a user
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  // Generate a token with additional claims for a user
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration, accessSecretKey);
  }

  // Generate a refresh token for a user
  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration, refreshSecretKey);
  }

  // Build a JWT token with provided claims, expiration time, and secret key
  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration,
          String key
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())  // Username is used here
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(key), SignatureAlgorithm.HS256)
            .compact();
  }

  // Validate if the access token is valid
  public boolean isAccessTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsernameFromAccess(token);  // Extract username from token
    return (username.equals(userDetails.getUsername())) && !isAccessTokenExpired(token);
  }

  // Validate if the refresh token is valid
  public boolean isRefreshTokenValid(String refreshToken, UserDetails userDetails) {
    final String username = extractUsernameFromRefresh(refreshToken);  // Extract username from token
    if (username.equals(userDetails.getUsername())) {
      String hashedToken = TokenHashingService.hashToken(refreshToken);
      Optional<RefreshToken> storedToken = refreshTokenRepository.findByTokenHash(hashedToken);
      return storedToken.isPresent() && !storedToken.get().isExpired() && !storedToken.get().isRevoked();
    }
    return false;
  }

  // Check if the access token is expired
  public boolean isAccessTokenExpired(String token) {
    return extractExpiration(token, accessSecretKey).before(new Date());
  }

  // Check if the refresh token is expired
  public boolean isRefreshTokenExpired(String token) {
    return extractExpiration(token, refreshSecretKey).before(new Date());
  }

  // Extract expiration time from the token
  private Date extractExpiration(String token, String key) {
    return extractClaim(token, Claims::getExpiration, key);
  }

  // Extract all claims from the token
  private Claims extractAllClaims(String token, String key) {
    try {
      return Jwts
              .parserBuilder()
              .setSigningKey(getSignInKey(key))
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (Exception e) {
      throw new RuntimeException("Token parsing failed", e);
    }
  }

  // Get the signing key for the JWT from the provided secret key
  private Key getSignInKey(String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
