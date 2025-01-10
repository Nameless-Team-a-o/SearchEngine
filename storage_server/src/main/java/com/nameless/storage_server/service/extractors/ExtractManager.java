package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.service.processor.FileProcessorService;
import com.nameless.storage_server.service.normalize.manager.NormalizeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ExtractManager {

    private final List<ExtractorService> extractors;
    private final NormalizeManager normalizeManager;
    private final FileProcessorService javaFileProcessorService;
    private static final Logger logger = Logger.getLogger(ExtractManager.class.getName());

    @Autowired
    public ExtractManager(List<ExtractorService> extractors,
                          NormalizeManager normalizeManager,
                          FileProcessorService javaFileProcessorService) {
        this.extractors = extractors;
        this.normalizeManager = normalizeManager;
        this.javaFileProcessorService = javaFileProcessorService;
    }

    /**
     * Extracts tokens from a CompilationUnit using various extractors and stores them in the database.
     *
     * @param fileContent the content of the Java file to be processed.
     * @param clazz       the associated class metadata.
     */
    public void extractTokens(String fileContent, Clazz clazz) {
        CompilationUnit compilationUnit = javaFileProcessorService.processFile(fileContent);

        logger.info("Extracting tokens...");

        List<Token> tokens = extractors.stream()
                .flatMap(extractor -> extractor.extract(compilationUnit, clazz).stream())
                .toList();

        normalizeManager.normalizeTokens(tokens, true, true);
    }
}
