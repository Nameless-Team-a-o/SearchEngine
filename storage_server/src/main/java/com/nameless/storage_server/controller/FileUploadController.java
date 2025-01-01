package com.nameless.storage_server.controller;

import com.nameless.storage_server.service.file.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * Controller class for handling file upload requests.
 */
@RestController
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Constructs a FileUploadController with the specified service.
     *
     * @param fileUploadService the service for processing uploaded files.
     */
    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * Handles the upload of a .zip file containing .java files.
     *
     * @param file the uploaded .zip file.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/upload/zip")
    public ResponseEntity<String> uploadZipFile(@RequestParam("file") MultipartFile file) {
        try {
            // Call the service method to process and store .java files
            fileUploadService.processZipFileAndStoreJavaFiles(file);

            // Return a success message
            return ResponseEntity.ok("ZIP file processed successfully. Java files have been stored.");
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            // Handle file processing errors
            return ResponseEntity.status(500).body("Error processing the .zip file");
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(500).body("An unexpected error occurred");
        }
    }
}
