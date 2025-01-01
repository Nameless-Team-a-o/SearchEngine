package com.nameless.storage_server.service.normalize;


import org.tartarus.snowball.SnowballStemmer;

import java.util.ArrayList;
import java.util.List;

public class StemmingStep implements NormalizationStep {

    @Override
    public String normalize(String token) {
        // Step 1: Split the camelCase string into words
        List<String> words = splitCamelCase(token);

        // Step 2: Stem each word using the Snowball Stemmer
        List<String> stemmedWords = new ArrayList<>();
        SnowballStemmer stemmer = new SnowballStemmer() {
            @Override
            public boolean stem() {
                return false;
            }
        };
        for (String word : words) {
            stemmer.setCurrent(word);
            if (stemmer.stem()) {
                stemmedWords.add(stemmer.getCurrent());
            } else {
                stemmedWords.add(word); // If stemming fails, keep the original word
            }
        }

        // Step 3: Join the stemmed words back into a single string
        return String.join("", stemmedWords);
    }

    // Helper method to split a camelCase string into words
    private List<String> splitCamelCase(String str) {
        List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c) && word.length() > 0) {
                words.add(word.toString());
                word.setLength(0);
            }
            word.append(c);
        }

        if (word.length() > 0) {
            words.add(word.toString()); // Add the last word
        }

        return words;
    }
}
