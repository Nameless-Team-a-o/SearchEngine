package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class StemmingOnlyStrategy implements NormalizationStrategy {
    private final NormalizationStep stemming;


    public StemmingOnlyStrategy(@Qualifier("stemmingStep")NormalizationStep stemming) {
        this.stemming = stemming;
    }

    @Override
    public String normalize(String token) {
        return stemming.normalize(token, false);
    }
}