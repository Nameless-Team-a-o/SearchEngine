package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;

import java.util.List;

public class LemmatizationAndStemmingStrategy implements NormalizationStrategy {
    private final NormalizationStep lemmatization;

    public LemmatizationAndStemmingStrategy(NormalizationStep lemmatization) {
        this.lemmatization = lemmatization;
    }

    @Override
    public List<String> normalize(List <String> tokenWords) {
        return lemmatization.normalize(tokenWords, true);
    }


}