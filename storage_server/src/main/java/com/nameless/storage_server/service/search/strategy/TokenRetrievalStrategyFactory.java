package com.nameless.storage_server.service.search.strategy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenRetrievalStrategyFactory {

    private final ExactMatchTokenRetrievalStrategy exactMatchStrategy;
    private final NormalizedTokenRetrievalStrategy normalizedMatchStrategy;

    @Autowired
    public TokenRetrievalStrategyFactory(ExactMatchTokenRetrievalStrategy exactMatchStrategy,
                                         NormalizedTokenRetrievalStrategy normalizedMatchStrategy) {
        this.exactMatchStrategy = exactMatchStrategy;
        this.normalizedMatchStrategy = normalizedMatchStrategy;
    }

    public TokenRetrievalStrategy getStrategy(boolean exactMatch) {
        if (exactMatch) {
            return exactMatchStrategy;
        } else {
            return normalizedMatchStrategy;
        }
    }
}
