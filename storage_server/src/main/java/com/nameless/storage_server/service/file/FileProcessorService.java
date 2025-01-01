package com.nameless.storage_server.service.file;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Token;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileProcessorService {
    List<Token> processFile(String fileCode, Clazz clazz);
}
