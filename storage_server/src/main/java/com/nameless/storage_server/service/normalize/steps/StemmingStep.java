package com.nameless.storage_server.service.normalize.steps;

import com.nameless.storage_server.service.normalize.splitter.CamelCaseHandler;
import com.nameless.storage_server.service.normalize.splitter.SnakeCaseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;

@Service
public class StemmingStep implements NormalizationStep {
    private final SnakeCaseHandler snakeCaseHandler;
    private final CamelCaseHandler camelCaseHandler;

    @Autowired
    public StemmingStep( SnakeCaseHandler snakeCaseHandler,
                         CamelCaseHandler camelCaseHandler) {
        this.snakeCaseHandler = snakeCaseHandler;
        this.camelCaseHandler = camelCaseHandler;
        snakeCaseHandler.setNext(camelCaseHandler);

    }
    @Override
    public String normalize(String token, boolean both) {
        // Step 1: Split the token into words using the TokenSplitter class
        List<String> words = snakeCaseHandler.handle(token);


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
