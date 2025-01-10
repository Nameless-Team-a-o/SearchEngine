package com.nameless.storage_server.service.normalize.splitter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TokenSplitter {
    private final List<SplittingHandler> handlers;

    public TokenSplitter(CamelCaseHandler camelCaseHandler,
                         SnakeCaseHandler snakeCaseHandler) {
        this.handlers = new ArrayList<>();
        handlers.add(snakeCaseHandler);
        handlers.add(camelCaseHandler);


        // Chain the handlers
        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).setNext(handlers.get(i + 1));
        }
    }

    public List<String> splitToken(String token) {
        // Pre-process the token
        token = preprocessToken(token);

        // Apply handlers
        if (token.contains("_")) {
            return handlers.get(0).handle(token); // Snake case handler
        } else {
            return handlers.get(1).handle(token); // Camel case handler
        }
    }


    private String preprocessToken(String token) {
        // Remove special characters except underscore
        token = token.replaceAll("[^a-zA-Z0-9_]", "");
        // Remove numbers from start
        token = token.replaceFirst("^\\d+", "");



        return token;
    }
}
