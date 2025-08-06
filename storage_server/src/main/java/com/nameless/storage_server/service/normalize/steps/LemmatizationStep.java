package com.nameless.storage_server.service.normalize.steps;

import edu.stanford.nlp.pipeline.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

@Component
@Order(1)
public class LemmatizationStep implements NormalizationStep {

    private static final Logger logger = LoggerFactory.getLogger(LemmatizationStep.class);
    private final StanfordCoreNLP pipeline;


    public LemmatizationStep() {
        try {
            logger.info("Initializing LemmatizationStep...");

            // Load model file from resources
            InputStream modelStream = getClass().getResourceAsStream("/models/english-left3words-distsim.tagger");
            if (modelStream == null) {
                throw new IllegalStateException("POS model file not found in resources/models/");
            }

            // Create temp file to pass absolute path
            File tempFile = File.createTempFile("pos-model-", ".tagger");
            Files.copy(modelStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempFile.deleteOnExit();

            logger.info("POS model loaded at: {}", tempFile.getAbsolutePath());

            // Configure pipeline
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
            props.setProperty("pos.model", tempFile.getAbsolutePath());

            this.pipeline = new StanfordCoreNLP(props);
            logger.info("Lemmatizer pipeline initialized successfully.");

        } catch (Exception e) {
            logger.error("Failed to initialize Lemmatizer", e);
            throw new RuntimeException("Failed to initialize Lemmatizer", e);
        }
    }

    @Override
    public String normalize(String word) {
        CoreDocument doc = new CoreDocument(word);
        pipeline.annotate(doc);
        return doc.tokens().isEmpty() ? word : doc.tokens().getFirst().lemma();
    }
}
