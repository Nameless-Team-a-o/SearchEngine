package com.nameless.storage_server.service.processor;

import com.github.javaparser.ast.CompilationUnit;
import org.springframework.stereotype.Service;

@Service
public interface FileProcessorService {
    CompilationUnit processFile(String fileCode);
}
