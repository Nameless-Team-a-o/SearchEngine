package com.nameless.storage_server.service.normalize.steps;

import com.nameless.storage_server.service.normalize.splitter.CamelCaseHandler;
import com.nameless.storage_server.service.normalize.splitter.SnakeCaseHandler;
import edu.stanford.nlp.pipeline.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class LemmatizationStep implements NormalizationStep {

    private final StanfordCoreNLP lemmatizer;
    private final SnakeCaseHandler snakeCaseHandler;
    private final CamelCaseHandler camelCaseHandler;

    public LemmatizationStep(@Value("${nlp.model.path}") String modelPath,
                             SnakeCaseHandler snakeCaseHandler,
                             CamelCaseHandler camelCaseHandler) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        props.setProperty("pos.model", Paths.get(modelPath).toAbsolutePath().toString());

        lemmatizer = new StanfordCoreNLP(props);
        this.snakeCaseHandler = snakeCaseHandler;
        this.camelCaseHandler = camelCaseHandler;

        // Chaining handlers to process snake case -> camel case
        snakeCaseHandler.setNext(camelCaseHandler);
    }

    @Override
    public String normalize(String token, boolean both) {
        // Step 1: Split the token into words using the handlers chain
        List<String> words = snakeCaseHandler.handle(token);

        // Step 2: Lemmatize each word
        List<String> lemmatizedWords = new ArrayList<>();
        for (String word : words) {
            lemmatizedWords.add(lemmatizeWord(word));
        }

        if (both) {
            return String.join("", StemmingStep.stemmWords(lemmatizedWords));
        } else {
            return String.join("", lemmatizedWords);
        }
    }

    // Helper method to lemmatize a single word using Stanford CoreNLP
    private String lemmatizeWord(String word) {
        CoreDocument doc = new CoreDocument(word);
        lemmatizer.annotate(doc);

        // Return the lemma of the first token (or the word itself if no lemma is found)
        return doc.tokens().isEmpty() ? word : doc.tokens().get(0).lemma();
    }
}
