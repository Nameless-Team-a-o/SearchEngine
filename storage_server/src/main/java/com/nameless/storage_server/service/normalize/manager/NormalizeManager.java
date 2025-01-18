package com.nameless.storage_server.service.normalize.manager;

import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.service.normalize.splitter.TokenSplitter;
import com.nameless.storage_server.service.normalize.steps.LemmatizationStep;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
import com.nameless.storage_server.service.normalize.steps.StemmingStep;

import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NormalizeManager {

    private final TokenSplitter tokenSplitter;
    private final NormalizeTokenService normalizeTokenService;

    @Autowired
    public NormalizeManager(TokenSplitter tokenSplitter,
                            NormalizeTokenService normalizeTokenService,
                            StemmingStep stemmingStep,
                            LemmatizationStep lemmatizationStep) {
        this.tokenSplitter = tokenSplitter;
        this.normalizeTokenService = normalizeTokenService;
    }

    public void normalizeTokens(List<OriginalToken> tokens, List<NormalizationStep> notmalizationStratigies) {

        for (OriginalToken token : tokens) {
            // Split the token
            List<String> words = tokenSplitter.splitToken(token.getToken());

            List<String> normalizedWords = words.stream()
                    .flatMap(word -> notmalizationStratigies.stream()
                            .map(normalizationStrategy -> normalizationStrategy.normalize(word)))
                    .collect(Collectors.toList());

            // Store the results
            normalizeTokenService.processAndStoreToken(token, normalizedWords);
        }
    }

}
