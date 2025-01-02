package com.nameless.storage_server.service.normalize;

import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.repository.NormalizeTokenRepository;
import com.nameless.storage_server.service.normalize.steps.LemmatizationStep;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
import com.nameless.storage_server.service.normalize.steps.StemmingStep;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenNormalizer {

    private final NormalizationStep lemmatization;
    private final NormalizationStep stemming;
    private final NormalizeTokenRepository normalizeTokenRepository;


    @Autowired
    public TokenNormalizer(NormalizeTokenRepository normalizeTokenRepository,
                           LemmatizationStep lemmatizationStep,
                           StemmingStep stemmingStep) {
        this.lemmatization = lemmatizationStep;
        this.stemming = stemmingStep;
        this.normalizeTokenRepository = normalizeTokenRepository;
    }

    public void normalizeTokens(List<Token> tokens, boolean useStemming, boolean useLemmatization) {
        List<NormalizeToken> normalizedTokens = tokens.stream()
                .map(token -> normalizeToken(token, useStemming, useLemmatization))
                .collect(Collectors.toList());

        normalizeTokenRepository.saveAll(normalizedTokens);
    }

    private NormalizeToken normalizeToken(Token token, boolean useStemming, boolean useLemmatization) {
        String normalizedToken = token.getToken();

        // Case 1: Only Lemmatization
        if (useLemmatization && !useStemming) {
            normalizedToken = lemmatization.normalize(normalizedToken, false);
        }
        // Case 2: Only Stemming
        else if (useStemming && !useLemmatization) {
            normalizedToken = stemming.normalize(normalizedToken, false);
        }
        // Case 3: Lemmatization first, then Stemming (order changed)
        else if (useLemmatization && useStemming) {
            normalizedToken = lemmatization.normalize(normalizedToken,true );
        }

        // Create a new NormalizeToken entity
        NormalizeToken normalized = new NormalizeToken();
        normalized.setToken(normalizedToken.toLowerCase());
        normalized.setType(token.getType());
        normalized.setLineNumber(token.getLineNumber());
        normalized.setClassID(token.getClassID());

        return normalized;
    }
}
