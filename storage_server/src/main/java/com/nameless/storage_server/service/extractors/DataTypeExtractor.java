package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class DataTypeExtractor implements ExtractorService {
    private final TokenService tokenService;
    private static final Logger logger = Logger.getLogger(DataTypeExtractor.class.getName());

    @Autowired
    public DataTypeExtractor(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public List<Token> extract(CompilationUnit compilationUnit, Clazz clazz) {
        List<Token> tokens = new ArrayList<>();

        compilationUnit.findAll(VariableDeclarator.class).forEach(varDecl -> {
            Type type = varDecl.getType();
            Token token =tokenService.createToken(
                    type.asString(),
                    TokenType.DATATYPE,
                    (long) varDecl.getRange().get().begin.line,
                    clazz
            );

            logger.info(String.format("Saved Token: %s, Type: %s, Line Number: %d, Class ID: %d",
                    token.getToken(), token.getType(), token.getLineNumber(), token.getClazz().getId()));

            tokens.add(token);
        });

        return tokens;
    }
}
