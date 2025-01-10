package com.nameless.storage_server.service.normalize.steps;

import com.nameless.storage_server.service.normalize.splitter.TokenSplitter;
import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
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
    private final StemmingStep stemmingStep;

    public LemmatizationStep(@Value("${nlp.model.path}") String modelPath,
                             StemmingStep stemmingStep) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        props.setProperty("pos.model", Paths.get(modelPath).toAbsolutePath().toString());

        lemmatizer = new StanfordCoreNLP(props);
        this.stemmingStep = stemmingStep;
    }

    @Override
    public List<String> normalize(List <String> words, boolean both) {
        // Step 1: Lemmatize each word
        List<String> lemmatizedWords = new ArrayList<>();
        for (String word : words) {
            lemmatizedWords.add(lemmatizeWord(word));
        }

        if (both) {
            return  stemmingStep.stemWords(lemmatizedWords) ;
        } else {
            return  lemmatizedWords;
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
