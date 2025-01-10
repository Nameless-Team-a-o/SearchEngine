package com.nameless.storage_server.exception;

public class ProjectCreationException extends FileProcessingException {
    public ProjectCreationException(String message) {
        super(message, 500);
    }
}