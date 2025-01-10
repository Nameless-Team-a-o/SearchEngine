package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class StemmingOnlyStrategy implements NormalizationStrategy {
    private final NormalizationStep stemming;


    public StemmingOnlyStrategy(@Qualifier("stemmingStep")NormalizationStep stemming) {
        this.stemming = stemming;
    }

    @Override
    public List<String> normalize(List <String> tokenWords) {
        return stemming.normalize(tokenWords, false);
    }

}