package com.nameless.storage_server.service.validator;

import com.nameless.storage_server.exception.ZipValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ZipValidator {
    public void validateZipFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ZipValidationException("File is empty");
        }
        if (!file.getOriginalFilename().endsWith(".zip")) {
            throw new ZipValidationException("File must be a ZIP archive");
        }
    }
}