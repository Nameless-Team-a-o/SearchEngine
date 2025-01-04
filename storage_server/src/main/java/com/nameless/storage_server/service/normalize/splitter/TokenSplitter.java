package com.nameless.storage_server.service.normalize.splitter;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenSplitter {
    private final SplittingHandler firstHandler;

    public TokenSplitter(SnakeCaseHandler snakeCaseHandler, CamelCaseHandler camelCaseHandler) {
        // Configure the chain of responsibility
        snakeCaseHandler.setNext(camelCaseHandler);
        this.firstHandler = snakeCaseHandler;
    }

    public List<String> splitWords(String token) {
        if (firstHandler == null) {
            throw new IllegalStateException("No handlers defined");
        }
        return firstHandler.handle(token);
    }
}
