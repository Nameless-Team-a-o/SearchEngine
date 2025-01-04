package com.nameless.storage_server.service.normalize.steps;

import com.nameless.storage_server.service.normalize.splitter.TokenSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;

@Service
public class StemmingStep implements NormalizationStep {
    private final TokenSplitter tokenSplitter;

    @Autowired
    public StemmingStep(TokenSplitter tokenSplitter) {
        this.tokenSplitter = tokenSplitter;
    }

    @Override
    public String normalize(String token, boolean both) {
        // Step 1: Split the token into words using the TokenSplitter class
        List<String> words = tokenSplitter.splitWords(token);

        // Step 2: Apply stemming to the words
        return String.join("", stemWords(words));
    }

    public static List<String> stemWords(List<String> words) {
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
        return stemmedWords;
    }
}
