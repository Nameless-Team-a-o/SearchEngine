package com.nameless.storage_server.service.file;

import com.nameless.storage_server.exception.FileOperationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileReader {
    private final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public String readFile(String filePath) {
        return readFile(Path.of(filePath));
    }

    public String readFile(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                logger.error("File does not exist: {}", filePath);
                throw new FileOperationException("File does not exist: " + filePath);
            }

            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) {
                logger.warn("File is empty: {}", filePath);
                throw new FileOperationException("File is empty: " + filePath);
            }

            return String.join("\n", lines);
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
            throw new FileOperationException("Error reading file: " + filePath, e);
        }
    }

    public Optional<String> readFileOptional(String filePath) {
        return readFileOptional(Path.of(filePath));
    }

    public Optional<String> readFileOptional(Path filePath) {
        try {
            return Optional.of(readFile(filePath));
        } catch (FileOperationException e) {
            return Optional.empty();
        }
    }
}