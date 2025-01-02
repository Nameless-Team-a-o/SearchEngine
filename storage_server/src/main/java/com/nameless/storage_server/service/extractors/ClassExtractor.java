package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClassExtractor implements ExtractorService {



    @Override
    public List<Token> extract(CompilationUnit compilationUnit, Long classId) {
        List<Token> tokens = new ArrayList<>();

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            Token token = new Token();
            token.setToken(classDecl.getNameAsString());
            token.setType(TokenType.CLASS);
            token.setLineNumber((long) classDecl.getRange().get().begin.line);
            token.setClassID(classId);
            tokens.add(token);
        });

        return tokens;
    }
}