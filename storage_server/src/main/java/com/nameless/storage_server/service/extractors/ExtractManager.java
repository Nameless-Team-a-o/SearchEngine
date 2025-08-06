package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
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
    private final List<NormalizationStep> normalizationSteps;
    private static final Logger logger = Logger.getLogger(ExtractManager.class.getName());

    @Autowired
    public ExtractManager(List<ExtractorService> extractors,
                          NormalizeManager normalizeManager,
                          FileProcessorService javaFileProcessorService,
                          List<NormalizationStep> normalizationSteps) {
        this.extractors = extractors;
        this.normalizeManager = normalizeManager;
        this.javaFileProcessorService = javaFileProcessorService;
        this.normalizationSteps = normalizationSteps;
    }

    public void extractTokens(String fileContent, Clazz clazz) {
        CompilationUnit compilationUnit = javaFileProcessorService.processFile(fileContent);

        logger.info("Extracting tokens...");

        List<OriginalToken> tokens = extractors.stream()
                .flatMap(extractor -> extractor.extract(compilationUnit, clazz).stream())
                .toList();

        normalizeManager.normalizeTokens(tokens, normalizationSteps);
    }
}
