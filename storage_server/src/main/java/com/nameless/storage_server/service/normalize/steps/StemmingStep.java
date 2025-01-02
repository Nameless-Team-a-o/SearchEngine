package com.nameless.storage_server.service.normalize.steps;

import com.nameless.storage_server.service.normalize.TokenSplitter;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;

@Service
public class StemmingStep implements NormalizationStep {

    @Override
    public String normalize(String token, boolean both) {
        // Step 1: Split the token into words using the TokenSplitter class
        List<String> words = TokenSplitter.splitWords(token);


        return String.join("", stemmWords(words));
    }

    public static List<String> stemmWords(List<String> words) {
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
