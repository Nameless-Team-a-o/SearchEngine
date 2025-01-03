package com.nameless.storage_server.service.validator;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ZipValidator {
    public void validateZipFile(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".zip")) {
            throw new IllegalArgumentException("Uploaded file must be a .zip file.");
        }
    }
}