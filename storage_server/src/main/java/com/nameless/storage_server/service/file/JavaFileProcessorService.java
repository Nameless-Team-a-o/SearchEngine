package com.nameless.storage_server.service.file;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.repository.TokenRepository;
import com.nameless.storage_server.service.extractors.AttributeExtractor;
import com.nameless.storage_server.service.extractors.ClassExtractor;
import com.nameless.storage_server.service.extractors.DataTypeExtractor;
import com.nameless.storage_server.service.extractors.MethodExtractor;
import com.nameless.storage_server.service.normalize.TokenNormalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for processing Java files to extract tokens and normalize them.
 */
@Service
public class JavaFileProcessorService implements FileProcessorService {

    private static final Logger logger = Logger.getLogger(JavaFileProcessorService.class.getName());

    private final AttributeExtractor attributeExtractor;
    private final ClassExtractor classExtractor;
    private final DataTypeExtractor dataTypeExtractor;
    private final MethodExtractor methodExtractor;
    private final TokenNormalizer tokenNormalizer;
    private final TokenRepository tokenRepository;

    @Autowired
    public JavaFileProcessorService(AttributeExtractor attributeExtractor,
                                    ClassExtractor classExtractor,
                                    DataTypeExtractor dataTypeExtractor,
                                    MethodExtractor methodExtractor,
                                    TokenNormalizer tokenNormalizer,
                                    TokenRepository tokenRepository) {
        this.attributeExtractor = attributeExtractor;
        this.classExtractor = classExtractor;
        this.dataTypeExtractor = dataTypeExtractor;
        this.methodExtractor = methodExtractor;
        this.tokenNormalizer = tokenNormalizer;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Processes a Java file, extracting tokens and storing them in the database.
     *
     * @param fileCode the Java file's code as a string.
     * @param clazz    the class metadata associated with the file.
     * @return a list of extracted and normalized tokens.
     */
    @Override
    public List<Token> processFile(String fileCode, Clazz clazz) {
        List<Token> tokens = new ArrayList<>();

        CompilationUnit compilationUnit = parseJavaCode(fileCode);
        extractTokens(compilationUnit, tokens, clazz);

        logger.info("Normalizing tokens...");
        tokenNormalizer.normalizeTokens(tokens, true, true);

        return tokens;
    }

    /**
     * Parses Java code into a CompilationUnit.
     *
     * @param fileCode the Java code to parse.
     * @return the parsed CompilationUnit.
     * @throws RuntimeException if parsing fails.
     */
    private CompilationUnit parseJavaCode(String fileCode) {
        logger.info("Parsing Java code...");
        JavaParser parser = new JavaParser(new ParserConfiguration());
        return parser.parse(fileCode)
                .getResult()
                .orElseThrow(() -> new RuntimeException("Failed to parse the Java code."));
    }

    /**
     * Extracts tokens from a CompilationUnit using various extractors and stores them in the database.
     *
     * @param compilationUnit the parsed CompilationUnit.
     * @param tokens          the list to which extracted tokens will be added.
     * @param clazz           the associated class metadata.
     */
    private void extractTokens(CompilationUnit compilationUnit, List<Token> tokens, Clazz clazz) {
        logger.info("Extracting tokens...");

        tokens.addAll(attributeExtractor.extract(compilationUnit));
        tokens.addAll(classExtractor.extract(compilationUnit));
        tokens.addAll(dataTypeExtractor.extract(compilationUnit));
        tokens.addAll(methodExtractor.extract(compilationUnit));

        tokens.forEach(token -> {
            token.setClassID(clazz.getId());
            tokenRepository.save(token);

            logger.info(String.format("Saved Token: %s, Type: %s, Line Number: %d, Class ID: %d",
                    token.getToken(), token.getType(), token.getLineNumber(), token.getClassID()));
        });
    }
}
