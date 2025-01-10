package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenStorageStrategy {
    private final NormalizeTokenService normalizeTokenService;

    public TokenStorageStrategy(NormalizeTokenService normalizeTokenService) {
        this.normalizeTokenService = normalizeTokenService;
    }

    public void storeProcessedToken(String originalToken, List<String> words, Token sourceToken) {
        // Store individual words and their prefixes
        for (String word : words) {
            storeTokenWithPrefixes(word.toLowerCase(), sourceToken);
        }

        // Store the normalized combined form
        String normalizedCombined = String.join("", words);
        storeTokenWithPrefixes(normalizedCombined.toLowerCase(), sourceToken);
    }

    private void storeTokenWithPrefixes(String token, Token sourceToken) {
        // Store the full token first
        normalizeTokenService.saveNormalizeToken(new NormalizeToken(
                token,
                sourceToken.getType(),
                sourceToken.getLineNumber(),
                sourceToken.getClazz()
        ));

        // Store all meaningful prefixes (length > 1)
        for (int i = 1; i < token.length() -1 ; i++) {
            String prefix = token.substring(0, i + 1);
            if (prefix.length() > 1) {  // Only store meaningful prefixes
                normalizeTokenService.saveNormalizeToken(new NormalizeToken(
                        prefix,
                        sourceToken.getType(),
                        sourceToken.getLineNumber(),
                        sourceToken.getClazz()
                ));
            }
        }
    }
}
