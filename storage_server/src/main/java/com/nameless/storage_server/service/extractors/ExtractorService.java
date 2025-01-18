package com.nameless.storage_server.service.extractors;

import com.github.javaparser.ast.CompilationUnit;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.token.OriginalToken;

import java.util.List;

public interface ExtractorService {
    List<OriginalToken> extract(CompilationUnit compilationUnit  , Clazz clazz);
}
