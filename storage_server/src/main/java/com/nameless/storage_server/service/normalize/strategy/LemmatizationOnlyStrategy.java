package com.nameless.storage_server.service.normalize.strategy;

import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LemmatizationOnlyStrategy implements NormalizationStrategy {
    private final NormalizationStep lemmatization;

    @Autowired
    public LemmatizationOnlyStrategy(@Qualifier("lemmatizationStep") NormalizationStep lemmatization) {
        this.lemmatization = lemmatization;
    }

    @Override
    public List<String> normalize(List <String> tokenWords) {
        return lemmatization.normalize(tokenWords, false);
    }


}