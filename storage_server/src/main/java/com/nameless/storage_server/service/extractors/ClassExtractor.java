package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ClassExtractor implements ExtractorService {
    private final TokenService tokenService;
    private static final Logger logger = Logger.getLogger(ClassExtractor.class.getName());


    @Autowired
    public ClassExtractor(final TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @Override
    public List<OriginalToken> extract(CompilationUnit compilationUnit, Clazz clazz) {
        List<OriginalToken> tokens = new ArrayList<>();

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            OriginalToken token = tokenService.createToken(
                    classDecl.getNameAsString(),
                    TokenType.CLASS,
                    (long) classDecl.getRange().get().begin.line,
                    clazz
            );

            logger.info(String.format("Saved Token: %s, Type: %s, Line Number: %d, Class ID: %d",
                    token.getToken(), token.getType(), token.getLineNumber(), token.getClazz().getId()));

            tokens.add(token);
        });

        return tokens;
    }
}