package com.nameless.storage_server.service.normalize;

import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;

@Service
public class Stemming implements NormalizationStep {

    @Override
    public String normalize(String token) {
        // Step 1: Split the token into words using the TokenSplitter class
        List<String> words = TokenSplitter.splitWords(token);

        // Step 2: Stem each word
        List<String> stemmedWords = new ArrayList<>();
        EnglishStemmer stemmer = new EnglishStemmer();
        for (String word : words) {
            stemmer.setCurrent(word);
            if (stemmer.stem()) {
                stemmedWords.add(stemmer.getCurrent());
            } else {
                stemmedWords.add(word); // If stemming fails, use the original word
            }
        }

        // Step 3: Join stemmed words without underscores and convert to lowercase
        return String.join("", stemmedWords).toLowerCase(); // Ensure everything is in lowercase
    }
}
