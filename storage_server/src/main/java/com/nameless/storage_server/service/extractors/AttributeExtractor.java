package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttributeExtractor implements ExtractorService {

    @Override
    public List<Token> extract(CompilationUnit compilationUnit) {
        List<Token> tokens = new ArrayList<>();

        // Find all field declarations (attributes)
        compilationUnit.findAll(FieldDeclaration.class).forEach(fieldDecl -> {
            fieldDecl.getVariables().forEach(variable -> {
                Token token = new Token();
                token.setToken(variable.getNameAsString()); // Name of the field
                token.setType(TokenType.ATTRIBUTE); // Token type for attribute
                token.setLineNumber((long) fieldDecl.getRange().get().begin.line); // Line number
                tokens.add(token);
            });
        });

        return tokens;
    }
}
