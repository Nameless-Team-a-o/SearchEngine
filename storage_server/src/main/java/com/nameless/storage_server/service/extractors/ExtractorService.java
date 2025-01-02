package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.nameless.storage_server.entity.Token;

import java.util.List;

public interface ExtractorService {
    List<Token> extract(CompilationUnit compilationUnit  , Long classId);
}
