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
    public List<Token> extract(CompilationUnit compilationUnit, Long classId) {
        List<Token> tokens = new ArrayList<>();

        compilationUnit.findAll(MethodDeclaration.class).forEach(methodDecl -> {
            Token token = new Token();
            token.setToken(methodDecl.getNameAsString());
            token.setType(TokenType.METHOD);
            token.setLineNumber((long) methodDecl.getRange().get().begin.line);
            token.setClassID(classId);
            tokens.add(token);
        });

        return tokens;
    }
}
