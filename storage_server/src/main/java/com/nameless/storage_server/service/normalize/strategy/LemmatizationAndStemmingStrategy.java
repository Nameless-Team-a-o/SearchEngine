package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.service.normalize.steps.NormalizationStep;

public class LemmatizationAndStemmingStrategy implements NormalizationStrategy {
    private final NormalizationStep lemmatization;

    public LemmatizationAndStemmingStrategy(NormalizationStep lemmatization) {
        this.lemmatization = lemmatization;
    }

    @Override
    public String normalize(String token) {
        return lemmatization.normalize(token, true);
    }
}