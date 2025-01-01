package com.nameless.storage_server.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class JwtService {
    @Autowired
    private RestTemplate restTemplate;

    static final Logger logger = Logger.getLogger(JwtService.class.getName());
    private String accessSecretKey = "FDBAB94A50DCF54E37FBB01D23CFED8192C6C94DCC31CAFE24F901F23B859680";

    // Method to extract username from access token
    public String extractUsernameFromAccess(String token) {
        // Removed logging statements to prevent logging
        return extractClaim(token, Claims::getSubject); // Extract the username (subject) claim
    }

    // Extracting any claim from the JWT token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extract all claims from the token
        return claimsResolver.apply(claims); // Extract the specific claim
    }

    // Extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new JwtException("Token is expired", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new JwtException("Token is malformed", e);
        } catch (Exception e) {
            throw new JwtException("Token parsing error", e);
        }
    }

    // Example method to return the key for signing the JWT
    private String getSignInKey() {
        return accessSecretKey;
    }

    public boolean validateToken(String token) {
        String url = "http://localhost:8080/api/v1/auth/validate_token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

}
