package com.nameless.storage_server.service.normalize.manager;

import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.repository.NormalizeTokenRepository;
import com.nameless.storage_server.service.normalize.splitter.TokenSplitter;
import com.nameless.storage_server.service.normalize.steps.LemmatizationStep;
import com.nameless.storage_server.service.normalize.steps.StemmingStep;
import com.nameless.storage_server.service.normalize.strategy.LemmatizationAndStemmingStrategy;
import com.nameless.storage_server.service.normalize.strategy.LemmatizationOnlyStrategy;
import com.nameless.storage_server.service.normalize.strategy.NormalizationStrategy;
import com.nameless.storage_server.service.normalize.strategy.StemmingOnlyStrategy;
import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Service
public class NormalizeManager {

    private final TokenSplitter tokenSplitter;
    private final NormalizeTokenService normalizeTokenService;
    private final NormalizationStrategy lemmatizationOnlyStrategy;
    private final NormalizationStrategy lemmatizationAndStemmingStrategy;
    private final NormalizationStrategy stemmingOnlyStrategy;

    @Autowired
    public NormalizeManager(TokenSplitter tokenSplitter,
                            NormalizeTokenService normalizeTokenService,
                            StemmingStep stemmingStep,
                            LemmatizationStep lemmatizationStep) {
        this.tokenSplitter = tokenSplitter;
        this.normalizeTokenService = normalizeTokenService;
        this.stemmingOnlyStrategy = new StemmingOnlyStrategy(stemmingStep);
        this.lemmatizationOnlyStrategy = new LemmatizationOnlyStrategy(lemmatizationStep);
        this.lemmatizationAndStemmingStrategy = new LemmatizationAndStemmingStrategy(lemmatizationStep);
    }

    public void normalizeTokens(List<Token> tokens, boolean useStemming, boolean useLemmatization) {
        NormalizationStrategy strategy = chooseStrategy(useStemming, useLemmatization);

        for (Token token : tokens) {
            // Split the token
            List<String> words = tokenSplitter.splitToken(token.getToken());

            // Apply normalization strategy
            List<String> normalizedWords = strategy.normalize(words);

            // Store the results
            normalizeTokenService.processAndStoreToken(token, normalizedWords);
        }
    }

    public NormalizationStrategy chooseStrategy(boolean useStemming, boolean useLemmatization) {
        if (useLemmatization && useStemming) {
            return lemmatizationAndStemmingStrategy;
        }
        if (useLemmatization) {
            return lemmatizationOnlyStrategy;
        }
        if (useStemming) {
            return stemmingOnlyStrategy;
        }
        return tokenWords -> tokenWords; // No normalization applied
    }

}
