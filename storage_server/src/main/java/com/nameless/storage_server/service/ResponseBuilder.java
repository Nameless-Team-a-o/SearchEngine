package com.nameless.storage_server.service;

import com.nameless.storage_server.dto.ClassContentResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseBuilder {
    public ResponseEntity<?> buildClassContentResponse(String filePath, String content , Long projectID) {
        return ResponseEntity.ok(new ClassContentResponseDTO(filePath, content , projectID));
    }
    public <T> ResponseEntity<T> buildSuccessResponse(T data) {
        return ResponseEntity.ok(data);
    }

    public <T> ResponseEntity<T> buildErrorResponse(String errorMessage, HttpStatus status) {
        return ResponseEntity.status(status).build();
    }
}