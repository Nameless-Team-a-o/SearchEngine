package com.nameless.storage_server.controller;

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
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            // TODO: Replace with spring security
            boolean isTokenValid =  jwtService.validateToken(token);
            if (!isTokenValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
            }

            fileUploadService.processZipFile(file, token);

            return ResponseEntity.ok("ZIP file processed successfully. Java files have been stored.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing the .zip file");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred");
        }
    }

}
