package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.repository.ClazzRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClassExtractor implements ExtractorService {
    private  final ClazzRepository clazzRepository;
    public ClassExtractor(final ClazzRepository clazzRepository) {
        this.clazzRepository = clazzRepository;
    }


    @Override
    public List<Token> extract(CompilationUnit compilationUnit) {
        List<Token> tokens = new ArrayList<>();

        // Find all class or interface declarations
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            Token token = new Token();
            token.setToken(classDecl.getNameAsString()); // Name of the class or interface
            token.setType(TokenType.CLASS); // Token type for class
            token.setLineNumber((long) classDecl.getRange().get().begin.line); // Line number
            tokens.add(token);
        });

        return tokens;
    }
}