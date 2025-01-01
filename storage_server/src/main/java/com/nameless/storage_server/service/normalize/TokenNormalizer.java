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

    private final List<NormalizationStep> normalizationSteps;
    private final NormalizeTokenRepository normalizeTokenRepository;

    // Constructor injection of the NormalizeTokenRepository and NormalizationStep implementations
    public TokenNormalizer(NormalizeTokenRepository normalizeTokenRepository,
                           LemmatizationStemmingStep lemmatizationStemmingStep) {
        this.normalizationSteps = List.of(lemmatizationStemmingStep);
        this.normalizeTokenRepository = normalizeTokenRepository;
    }

    public void normalizeTokens(List<Token> tokens) {
        // Process each token through the normalization steps
        List<NormalizeToken> normalizedTokens = tokens.stream()
                .map(this::normalizeToken)
                .collect(Collectors.toList());

        // Save normalized tokens to the repository
        normalizeTokenRepository.saveAll(normalizedTokens);
    }

    private NormalizeToken normalizeToken(Token token) {
        // Apply each normalization step to the token
        String normalizedToken = normalizationSteps.stream()
                .map(step -> step.normalize(token.getToken()))
                .collect(Collectors.joining("")); // You can use another delimiter if necessary

        // Create a new NormalizeToken entity
        NormalizeToken normalized = new NormalizeToken();
        normalized.setToken(normalizedToken.toLowerCase());
        normalized.setType(token.getType());
        normalized.setLineNumber(token.getLineNumber());
        normalized.setClassID(token.getClassID());
        return normalized;
    }
}
