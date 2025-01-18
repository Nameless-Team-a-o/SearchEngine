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

    public LemmatizationStep() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        props.setProperty("pos.model", Paths.get("${nlp.model.path}").toAbsolutePath().toString());

        lemmatizer = new StanfordCoreNLP(props);
    }

    @Override
    public String normalize(String word) {
        CoreDocument doc = new CoreDocument(word);
        lemmatizer.annotate(doc);

        // Return the lemma of the first token (or the word itself if no lemma is found)
        return doc.tokens().isEmpty() ? word : doc.tokens().getFirst().lemma();
    }
}
