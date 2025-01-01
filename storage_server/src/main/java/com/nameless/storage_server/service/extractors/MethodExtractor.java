package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component

public class MethodExtractor implements ExtractorService {

    @Override
    public List<Token> extract(CompilationUnit compilationUnit) {
        List<Token> tokens = new ArrayList<>();

        // Find all method declarations
        compilationUnit.findAll(MethodDeclaration.class).forEach(methodDecl -> {
            Token token = new Token();
            token.setToken(methodDecl.getNameAsString()); // Name of the method
            token.setType(TokenType.METHOD); // Token type for method
            token.setLineNumber((long) methodDecl.getRange().get().begin.line); // Line number
            tokens.add(token);
        });

        return tokens;
    }
}
