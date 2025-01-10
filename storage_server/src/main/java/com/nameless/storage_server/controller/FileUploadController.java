package com.nameless.storage_server.controller;

import com.nameless.storage_server.exception.AuthenticationException;
import com.nameless.storage_server.exception.FileProcessingException;
import com.nameless.storage_server.service.file.FileUploadService;
import com.nameless.storage_server.service.jwt.JwtService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {
    private final JwtService jwtService;
    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(JwtService jwtService,
                                FileUploadService fileUploadService) {
        this.jwtService = jwtService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload/zip")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestHeader("Authorization") String token) {
        if (!jwtService.validateToken(token)) {
            throw new AuthenticationException("Invalid token.");
        }

        fileUploadService.processZipFile(file, token);
        return ResponseEntity.ok("ZIP file processed successfully. Java files have been stored.");
    }
}