package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class DataTypeExtractor implements ExtractorService {

    @Override
    public List<Token> extract(CompilationUnit compilationUnit) {
        List<Token> tokens = new ArrayList<>();

        // Find all variable declarations
        compilationUnit.findAll(VariableDeclarator.class).forEach(varDecl -> {
            Type type = varDecl.getType(); // Get the type of the variable
            Token token = new Token();
            token.setToken(type.asString()); // Set the data type as the token
            token.setType(TokenType.DATATYPE); // Token type for data type
            token.setLineNumber((long) varDecl.getRange().get().begin.line); // Line number
            tokens.add(token);
        });

        return tokens;
    }
}
