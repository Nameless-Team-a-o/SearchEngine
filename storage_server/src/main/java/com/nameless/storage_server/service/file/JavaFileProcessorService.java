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

@Service
public class JavaFileProcessorService implements FileProcessorService {

    // Autowire the extractors
    @Autowired
    private AttributeExtractor attributeExtractor;
    @Autowired
    private ClassExtractor classExtractor;
    @Autowired
    private DataTypeExtractor dataTypeExtractor;
    @Autowired
    private MethodExtractor methodExtractor;

    @Autowired
    private TokenRepository tokenRepository;


    private  final TokenNormalizer  tokenNormalizer;

    public JavaFileProcessorService(TokenNormalizer tokenNormalizer) {
        this.tokenNormalizer = tokenNormalizer;
    }

    private static final Logger logger = Logger.getLogger(JavaFileProcessorService.class.getName());

    @Override
    public List<Token> processFile(String fileCode, Clazz clazz) {
        List<Token> tokens = new ArrayList<>();

        // Configure the parser with custom settings
        ParserConfiguration configuration = new ParserConfiguration();
        JavaParser parser = new JavaParser(configuration);

        // Parse the Java code into a CompilationUnit using the custom configuration
        CompilationUnit compilationUnit = parser.parse(fileCode).getResult().orElse(null);

        // Check if parsing was successful
        if (compilationUnit == null) {
            throw new RuntimeException("Failed to parse the Java code.");
        }

        // Extract tokens using the respective extractors (now autowired)
        tokens.addAll(attributeExtractor.extract(compilationUnit));
        tokens.addAll(classExtractor.extract(compilationUnit));
        tokens.addAll(dataTypeExtractor.extract(compilationUnit));
        tokens.addAll(methodExtractor.extract(compilationUnit));

        // Log the extracted tokens using a proper logger
        logger.info("Extracted Tokens:");
        for (Token token : tokens) {
            token.setClassID(clazz.getId());
            tokenRepository.save(token);

            System.out.println("Token: " + token.getToken() + ", Type: " + token.getType() + ", Line Number: " + token.getLineNumber()+ ", Class ID: " + token.getClassID()) ;
        }
        tokenNormalizer.normalizeTokens(tokens);
        return tokens;
    }
}
