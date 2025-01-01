package com.nameless.storage_server.controller;

import com.nameless.storage_server.service.file.FileUploadService;
import com.nameless.storage_server.service.jwt.JwtService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload/zip")
    public ResponseEntity<String> uploadZipFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            boolean isTokenValid =  jwtService.validateToken(token);
            if (!isTokenValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
            }

            fileUploadService.processZipFileAndStoreJavaFiles(file, token);

            // إرجاع رسالة نجاح
            return ResponseEntity.ok("ZIP file processed successfully. Java files have been stored.");
        } catch (IllegalArgumentException e) {
            // التعامل مع الأخطاء المتعلقة بالتحقق
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            // التعامل مع أخطاء معالجة الملف
            return ResponseEntity.status(500).body("Error processing the .zip file");
        } catch (Exception e) {
            // التعامل مع الأخطاء غير المتوقعة
            return ResponseEntity.status(500).body("An unexpected error occurred");
        }
    }

}
