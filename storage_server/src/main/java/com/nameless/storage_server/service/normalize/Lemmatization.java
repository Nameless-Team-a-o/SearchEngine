package com.nameless.storage_server.service.normalize;

import edu.stanford.nlp.pipeline.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class Lemmatization implements NormalizationStep {

    private final StanfordCoreNLP lemmatizer;

    public Lemmatization() {
        // Specify the model path for the POS tagger
        String modelPath = "C:\\Users\\user\\Desktop\\System design training\\spring\\search engine\\storage_server\\english-left3words-distsim.tagger";

        // Configure Stanford CoreNLP with lemmatization and POS tagging
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");

        // Set the custom POS model path
        props.setProperty("pos.model", modelPath);

        lemmatizer = new StanfordCoreNLP(props);
    }

    @Override
    public String normalize(String token) {
        // Step 1: Split the token into words using the TokenSplitter class
        List<String> words = TokenSplitter.splitWords(token);

        // Step 2: Lemmatize each word
        List<String> lemmatizedWords = new ArrayList<>();
        for (String word : words) {
            lemmatizedWords.add(lemmatizeWord(word));
        }

        // Step 3: Join lemmatized words without underscores and convert to lowercase
        return String.join("", lemmatizedWords).toLowerCase(); // Ensure everything is in lowercase
    }

    // Helper method to lemmatize a single word using Stanford CoreNLP
    private String lemmatizeWord(String word) {
        CoreDocument doc = new CoreDocument(word);
        lemmatizer.annotate(doc);

        // Return the lemma of the first token (or the word itself if no lemma)
        return doc.tokens().isEmpty() ? word : doc.tokens().get(0).lemma();
    }
}
