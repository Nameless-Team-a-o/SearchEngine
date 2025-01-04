package com.nameless.storage_server.service.normalize.splitter;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenSplitter {
    private SplittingHandler firstHandler;

    public void setFirstHandler(SplittingHandler firstHandler) {
        this.firstHandler = firstHandler;
    }

    public List<String> splitWords(String token) {
        if (firstHandler == null) {
            throw new IllegalStateException("No handlers defined");
        }
        return firstHandler.handle(token);
    }
}
