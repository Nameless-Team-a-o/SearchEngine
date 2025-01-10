package com.nameless.storage_server.service.search;

import com.nameless.storage_server.dto.SearchResponseDTO;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TokenInfoHelper {
    private static final Logger logger = LoggerFactory.getLogger(TokenInfoHelper.class);
    //TODO: make interface
    public static SearchResponseDTO generateTokenInfo(Object token) {
        Clazz clazz = token instanceof Token ? ((Token) token).getClazz() : ((NormalizeToken) token).getClazz();
        TokenType type = token instanceof Token ? ((Token) token).getType() : ((NormalizeToken) token).getType();
        Long lineNumber = token instanceof Token ? ((Token) token).getLineNumber() : ((NormalizeToken) token).getLineNumber();

        String classId = String.valueOf(clazz.getId());
        String className = clazz.getClassName();
        String tokenType = type.toString();
        String filePath = clazz.getFilePath();

        try {
            String lineContent = readSpecificLine(Path.of(filePath), lineNumber);
            String tokenDetail = String.format("Class ID: %s | Class: %s | Token Type: %s | Line %d: %s",
                    classId, className, tokenType, lineNumber, lineContent);

            return new SearchResponseDTO(classId, className, tokenType, tokenDetail, lineNumber, filePath);

        } catch (IOException e) {
            logger.error("Failed to read file: " + filePath, e);
            throw new RuntimeException("Failed to read token information", e);
        }
    }

    private static String readSpecificLine(Path path, long lineNumber) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.skip(lineNumber - 1)
                    .findFirst()
                    .orElseThrow(() -> new IOException("Line " + lineNumber + " not found in file"));
        }
    }
}