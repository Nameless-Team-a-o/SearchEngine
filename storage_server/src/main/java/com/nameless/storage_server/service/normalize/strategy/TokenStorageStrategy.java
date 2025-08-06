package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.entity.token.NormalizedToken;
import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TokenStorageStrategy {
    private final NormalizeTokenService normalizeTokenService;

    public TokenStorageStrategy(NormalizeTokenService normalizeTokenService) {
        this.normalizeTokenService = normalizeTokenService;
    }

    public void storeProcessedToken(String originalToken, List<String> words, OriginalToken sourceToken) {
        Set<String> seen = new HashSet<>();  // Move it here

        for (String word : words) {
            storeTokenWithPrefixes(word.toLowerCase(), sourceToken, seen);
        }

        String normalizedCombined = String.join("", words);
        storeTokenWithPrefixes(normalizedCombined.toLowerCase(), sourceToken, seen);
    }

    private void storeTokenWithPrefixes(String token, OriginalToken sourceToken, Set<String> seen) {
        // Store the full token first
        if (token.length() > 1 && seen.add(token)) {
            normalizeTokenService.saveNormalizeToken(new NormalizedToken(
                    token,
                    sourceToken.getType(),
                    sourceToken.getLineNumber(),
                    sourceToken.getClazz()
            ));
        }

        // Store all meaningful prefixes (length >= 3)
        for (int i = 2; i < token.length(); i++) {
            String prefix = token.substring(0, i);
            if (prefix.length() >= 3 && seen.add(prefix)) {
                normalizeTokenService.saveNormalizeToken(new NormalizedToken(
                        prefix,
                        sourceToken.getType(),
                        sourceToken.getLineNumber(),
                        sourceToken.getClazz()
                ));
            }
        }
    }
}
