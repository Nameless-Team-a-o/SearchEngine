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
    public List<Token> extract(CompilationUnit compilationUnit, Long classId) {
        List<Token> tokens = new ArrayList<>();

        compilationUnit.findAll(FieldDeclaration.class).forEach(fieldDecl -> {
            fieldDecl.getVariables().forEach(variable -> {
                Token token = new Token();
                token.setToken(variable.getNameAsString());
                token.setType(TokenType.ATTRIBUTE);
                token.setLineNumber((long) fieldDecl.getRange().get().begin.line);
                token.setClassID(classId);
                tokens.add(token);
            });
        });

        return tokens;
    }
}
