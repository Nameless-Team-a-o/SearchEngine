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
    public List<Token> extract(CompilationUnit compilationUnit, Long classId) {
        List<Token> tokens = new ArrayList<>();

        compilationUnit.findAll(VariableDeclarator.class).forEach(varDecl -> {
            Type type = varDecl.getType();
            Token token = new Token();
            token.setToken(type.asString());
            token.setType(TokenType.DATATYPE);
            token.setLineNumber((long) varDecl.getRange().get().begin.line);
            token.setClassID(classId);
            tokens.add(token);
        });

        return tokens;
    }
}
