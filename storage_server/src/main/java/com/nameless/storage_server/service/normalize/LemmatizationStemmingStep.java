package com.nameless.storage_server.service.normalize;

import edu.stanford.nlp.pipeline.*;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class LemmatizationStemmingStep implements NormalizationStep {

    private StanfordCoreNLP lemmatizer;

    public LemmatizationStemmingStep() {
        // Specify the model path for the POS tagger
        String modelPath = "C:\\Users\\user\\Desktop\\System design training\\spring\\search engine\\storage_server\\english-left3words-distsim.tagger";  // Specify the full path to the model file

        // Initialize Stanford CoreNLP with lemmatization and POS tagging
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");

        // Include the model path in your StanfordCoreNLP pipeline
        props.setProperty("pos.model", modelPath);  // Set the model path here

        lemmatizer = new StanfordCoreNLP(props);
    }

    @Override
    public String normalize(String token) {
        // Step 1: Split the camelCase string into words
        List<String> words = splitCamelCase(token);

        // Step 2: Lemmatize each word
        List<String> lemmatizedWords = new ArrayList<>();
        for (String word : words) {
            lemmatizedWords.add(lemmatizeWord(word));
        }

        // Step 3: Stem each lemmatized word using Snowball English Stemmer
        List<String> stemmedWords = new ArrayList<>();
        EnglishStemmer stemmer = new EnglishStemmer(); // Correct instantiation of EnglishStemmer

        for (String word : lemmatizedWords) {
            stemmer.setCurrent(word);
            if (stemmer.stem()) {
                stemmedWords.add(stemmer.getCurrent());
            } else {
                stemmedWords.add(word); // If stemming fails, keep the lemmatized word
            }
        }

        // Step 4: Join the stemmed words back into a single string
        return String.join("", stemmedWords);
    }

    // Helper method to lemmatize a single word using Stanford CoreNLP
    private String lemmatizeWord(String word) {
        // Create a document annotation
        CoreDocument doc = new CoreDocument(word);
        lemmatizer.annotate(doc);

        // Get the lemmatized form (the first token is the lemmatized word)
        return doc.tokens().get(0).lemma();
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
