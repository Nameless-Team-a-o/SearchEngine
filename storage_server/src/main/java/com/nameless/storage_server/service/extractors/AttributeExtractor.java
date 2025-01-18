package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
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
public class AttributeExtractor implements ExtractorService {
    private final TokenService tokenService;
    private static final Logger logger = Logger.getLogger(AttributeExtractor.class.getName());

    @Autowired
    public AttributeExtractor(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public List<OriginalToken> extract(CompilationUnit compilationUnit, Clazz clazz) {
        List<OriginalToken> tokens = new ArrayList<>();

        compilationUnit.findAll(FieldDeclaration.class).forEach(fieldDecl -> {
            fieldDecl.getVariables().forEach(variable -> {
                OriginalToken token = tokenService.createToken(
                        variable.getNameAsString(),
                        TokenType.ATTRIBUTE ,
                        (long) fieldDecl.getRange().get().begin.line,
                        clazz
                );

                logger.info(String.format("Saved Token: %s, Type: %s, Line Number: %d, Class ID: %d",
                        token.getToken(), token.getType(), token.getLineNumber(), token.getClazz().getId()));

                tokens.add(token);
            });
        });

        return tokens;
    }
}
