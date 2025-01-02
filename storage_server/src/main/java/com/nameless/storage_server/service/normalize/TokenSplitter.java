package com.nameless.storage_server.service.normalize;

import java.util.ArrayList;
import java.util.List;

public class TokenSplitter {

    public static List<String> splitWords(String token) {
        List<String> words = new ArrayList<>();

        // Handle snake_case
        String[] parts = token.split("_");

        if(parts.length == 1) {
            words.addAll(splitCamelCase(parts[0]));

        }
        // Process each part and convert them to lowercase, no need to capitalize
        else{
            String finalWord = parts[0].toLowerCase();
            for (int i = 1 ; i < parts.length ; i++) {
                  parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase();
                  finalWord = finalWord + parts[i];
            }
                    words.addAll(splitCamelCase(finalWord));
        }


        return words;
    }

    private static List<String> splitCamelCase(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!word.isEmpty()) {
                    result.add(word.toString().toLowerCase());
                }
                word = new StringBuilder().append(c);
            } else {
                word.append(c);
            }
        }
        if (!word.isEmpty()) {
            result.add(word.toString().toLowerCase());
        }

        return result;
    }
}
