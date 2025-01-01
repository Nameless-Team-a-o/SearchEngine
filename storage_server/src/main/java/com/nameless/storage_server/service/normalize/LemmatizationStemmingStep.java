package com.nameless.storage_server.service.normalize;

import com.nameless.storage_server.service.normalize.NormalizationStep;
import edu.stanford.nlp.pipeline.*;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class LemmatizationStemmingStep implements NormalizationStep {

    private final StanfordCoreNLP lemmatizer;

    public LemmatizationStemmingStep() {
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
        // Step 1: Split the token into words (snake_case and camelCase)
        List<String> words = splitWords(token);

        // Step 2: Lemmatize each word
        List<String> lemmatizedWords = new ArrayList<>();
        for (String word : words) {
            lemmatizedWords.add(lemmatizeWord(word));
        }

        // Step 3: Stem each lemmatized word
        List<String> stemmedWords = new ArrayList<>();
        EnglishStemmer stemmer = new EnglishStemmer();
        for (String word : lemmatizedWords) {
            stemmer.setCurrent(word);
            if (stemmer.stem()) {
                stemmedWords.add(stemmer.getCurrent());
            } else {
                stemmedWords.add(word); // If stemming fails, use lemmatized word
            }
        }

        // Step 4: Join stemmed words without underscores and convert to lowercase
        return String.join("", stemmedWords).toLowerCase(); // Ensure everything is in lowercase
    }

    private List<String> splitWords(String token) {
        List<String> words = new ArrayList<>();

        // Handle snake_case
        String[] parts = token.split("_");

        // Process each part and convert them to lowercase, no need to capitalize
        for (String part : parts) {
            if(parts.length>1)
                words.addAll(splitCamelCase(part.toLowerCase()));
            else
                words.addAll(splitCamelCase(part));
        }

        return words;
    }

    private List<String> splitCamelCase(String str) {
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

    // Helper method to lemmatize a single word using Stanford CoreNLP
    private String lemmatizeWord(String word) {
        CoreDocument doc = new CoreDocument(word);
        lemmatizer.annotate(doc);

        // Return the lemma of the first token (or the word itself if no lemma)
        return doc.tokens().isEmpty() ? word : doc.tokens().get(0).lemma();
    }
}
