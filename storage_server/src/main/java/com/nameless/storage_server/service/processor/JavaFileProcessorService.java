package com.nameless.storage_server.service.processor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service for processing Java files to extract tokens and normalize them.
 */
@Service
public class JavaFileProcessorService implements FileProcessorService {

    private static final Logger logger = Logger.getLogger(JavaFileProcessorService.class.getName());

    /**
     * Parses Java code into a CompilationUnit.
     *
     * @param fileCode the Java code to parse.
     * @return the parsed CompilationUnit.
     * @throws RuntimeException if parsing fails.
     */
    public CompilationUnit processFile(String fileCode) {
        logger.info("Parsing Java code...");
        JavaParser parser = new JavaParser(new ParserConfiguration());
        return parser.parse(fileCode)
                .getResult()
                .orElseThrow(() -> new RuntimeException("Failed to parse the Java code."));
    }


}
