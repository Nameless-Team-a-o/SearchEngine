package com.nameless.storage_server.service.normalize;

import java.util.ArrayList;
import java.util.List;

public class TokenSplitter {

    public static List<String> splitWords(String token) {
        List<String> words = new ArrayList<>();

        // Handle snake_case
        String[] parts = token.split("_");

        // Process each part and convert them to lowercase, no need to capitalize
        for (String part : parts) {
            if (parts.length > 1) {
                words.addAll(splitCamelCase(part.toLowerCase()));
            } else {
                words.addAll(splitCamelCase(part));
            }
        }

        return words;
    }

    private static List<String> splitCamelCase(String str) {
        List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c) && word.length() > 0) {
                words.add(word.toString());
                word.setLength(0);
            }
            word.append(Character.toLowerCase(c)); // Convert to lowercase immediately
        }

        if (word.length() > 0) {
            words.add(word.toString());
        }

        return words;
    }
}
