package com.nameless.storage_server.service.normalize;

import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.repository.NormalizeTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenNormalizer {

    private final NormalizationStep lemmatization;
    private final NormalizationStep stemming;
    private final NormalizeTokenRepository normalizeTokenRepository;

    // Constructor injection of the NormalizeTokenRepository and NormalizationStep implementations
    @Autowired
    public TokenNormalizer(NormalizeTokenRepository normalizeTokenRepository,
                           Lemmatization lemmatization,
                           Stemming stemming) {
        this.lemmatization = lemmatization;
        this.stemming = stemming;
        this.normalizeTokenRepository = normalizeTokenRepository;
    }

    public void normalizeTokens(List<Token> tokens, boolean useStemming, boolean useLemmatization) {
        // Process each token through the normalization steps based on conditions
        List<NormalizeToken> normalizedTokens = tokens.stream()
                .map(token -> normalizeToken(token, useStemming, useLemmatization))
                .collect(Collectors.toList());

        // Save normalized tokens to the repository
        normalizeTokenRepository.saveAll(normalizedTokens);
    }

    private NormalizeToken normalizeToken(Token token, boolean useStemming, boolean useLemmatization) {
        String normalizedToken = token.getToken();

        // Case 1: Only Lemmatization
        if (useLemmatization && !useStemming) {
            normalizedToken = lemmatization.normalize(normalizedToken);
        }
        // Case 2: Only Stemming
        else if (useStemming && !useLemmatization) {
            normalizedToken = stemming.normalize(normalizedToken);
        }
        // Case 3: Lemmatization first, then Stemming (order changed)
        else if (useLemmatization && useStemming) {
            String lemmatizedToken = lemmatization.normalize(normalizedToken); // Apply lemmatization first
            normalizedToken = stemming.normalize(lemmatizedToken); // Apply stemming after lemmatization
        }

        // Create a new NormalizeToken entity
        NormalizeToken normalized = new NormalizeToken();
        normalized.setToken(normalizedToken);
        normalized.setType(token.getType());
        normalized.setLineNumber(token.getLineNumber());
        normalized.setClassID(token.getClassID());

        return normalized;
    }
}
