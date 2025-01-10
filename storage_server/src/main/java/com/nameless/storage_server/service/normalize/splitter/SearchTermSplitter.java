package com.nameless.storage_server.service.normalize.splitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SearchTermSplitter {

    private static final Logger logger = LoggerFactory.getLogger(SearchTermSplitter.class);
    private final CamelCaseHandler camelCaseHandler;

    public SearchTermSplitter(CamelCaseHandler camelCaseHandler) {
        this.camelCaseHandler = camelCaseHandler;
    }

    public List<String> handle(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        // Step 1: Clean the input (remove invalid characters and trim)
        String cleanedInput = token.replaceAll("[^a-zA-Z0-9_]", " ").trim();
        logger.info("Step 1 - Cleaned Input: {}", cleanedInput);

        // Step 2: Remove numbers from the start of the string
        cleanedInput = cleanedInput.replaceFirst("^\\d+", "");
        logger.info("Step 2 - Removed Leading Numbers: {}", cleanedInput);

        // Step 3: Split into parts based on spaces and underscores
        String[] parts = cleanedInput.split("[\\s_]+");
        logger.info("Step 3 - Split Parts: {}", (Object) parts);

        // Step 4: Construct a camelCase string
        StringBuilder finalWord = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                finalWord.append(parts[i].toLowerCase());
            } else {
                finalWord.append(capitalize(parts[i]));
            }
        }
        String camelCased = finalWord.toString();
        logger.info("Step 4 - CamelCased String: {}", camelCased);

        // Step 5: Pass the camelCase string to the CamelCaseHandler
        List<String> result = camelCaseHandler.handle(camelCased);
        logger.info("Step 5 - Result after CamelCaseHandler: {}", result);

        return result;
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
