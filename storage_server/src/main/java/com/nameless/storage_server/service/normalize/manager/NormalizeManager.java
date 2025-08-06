package com.nameless.storage_server.service.normalize.manager;

import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.service.normalize.splitter.TokenSplitter;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
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
                            NormalizeTokenService normalizeTokenService) {
        this.tokenSplitter = tokenSplitter;
        this.normalizeTokenService = normalizeTokenService;
    }

    public void normalizeTokens(List<OriginalToken> tokens, List<NormalizationStep> normalizationStrategies) {
        for (OriginalToken token : tokens) {
            // Step 1: Split the token into sub-words (e.g., camelCase → [camel, Case])
            List<String> words = tokenSplitter.splitToken(token.getToken());

            List<String> normalizedWords = words.stream()
                    .map(word -> {
                        String result = word;
                        // Apply each normalization step sequentially
                        for (NormalizationStep step : normalizationStrategies) {
                            result = step.normalize(result);
                        }
                        return result;
                    })
                    .collect(Collectors.toList());

            // Step 2: Store the normalized token result
            normalizeTokenService.processAndStoreToken(token, normalizedWords);
        }
    }
}
