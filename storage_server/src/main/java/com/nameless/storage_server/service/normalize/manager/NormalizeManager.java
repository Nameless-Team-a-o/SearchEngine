package com.nameless.storage_server.service.normalize.manager;

import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.repository.NormalizeTokenRepository;
import com.nameless.storage_server.service.normalize.steps.LemmatizationStep;
import com.nameless.storage_server.service.normalize.steps.StemmingStep;
import com.nameless.storage_server.service.normalize.strategy.LemmatizationAndStemmingStrategy;
import com.nameless.storage_server.service.normalize.strategy.LemmatizationOnlyStrategy;
import com.nameless.storage_server.service.normalize.strategy.NormalizationStrategy;
import com.nameless.storage_server.service.normalize.strategy.StemmingOnlyStrategy;
import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class NormalizeManager {

    private final NormalizeTokenRepository normalizeTokenRepository;
    private final NormalizationStrategy lemmatizationOnlyStrategy;
    private final NormalizationStrategy stemmingOnlyStrategy;
    private final NormalizationStrategy lemmatizationAndStemmingStrategy;
    private final NormalizeTokenService normalizeTokenService;

    @Autowired
    public NormalizeManager(NormalizeTokenRepository normalizeTokenRepository,
                            LemmatizationStep lemmatizationStep,
                            StemmingStep stemmingStep,
                            NormalizeTokenService normalizeTokenService) {
        this.normalizeTokenRepository = normalizeTokenRepository;
        this.lemmatizationOnlyStrategy = new LemmatizationOnlyStrategy(lemmatizationStep);
        this.stemmingOnlyStrategy = new StemmingOnlyStrategy(stemmingStep);
        this.lemmatizationAndStemmingStrategy = new LemmatizationAndStemmingStrategy(lemmatizationStep);
        this.normalizeTokenService = normalizeTokenService;
    }

    public void normalizeTokens(List<Token> tokens, boolean useStemming, boolean useLemmatization) {
        NormalizationStrategy strategy = chooseStrategy(useStemming, useLemmatization);

        List<NormalizeToken> normalizedTokens = tokens.stream()
                .map(token -> normalizeToken(token, strategy))
                .toList();

        normalizeTokenRepository.saveAll(normalizedTokens);
    }

    private NormalizationStrategy chooseStrategy(boolean useStemming, boolean useLemmatization) {
        if (useLemmatization && useStemming) {
            return lemmatizationAndStemmingStrategy;
        }
        if (useLemmatization) {
            return lemmatizationOnlyStrategy;
        }
        if (useStemming) {
            return stemmingOnlyStrategy;
        }
        return token -> token; // No normalization applied
    }

    private NormalizeToken normalizeToken(Token token, NormalizationStrategy strategy) {
        String normalizedToken = strategy.normalize(token.getToken());
        return normalizeTokenService.createNormalizeToken(
                normalizedToken.toLowerCase(),
                token.getType(),
                token.getLineNumber(),
                token.getClazz());
    }

}
