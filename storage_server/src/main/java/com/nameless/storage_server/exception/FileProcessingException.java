package com.nameless.storage_server.exception;

public class FileProcessingException extends RuntimeException {
    private final int statusCode;

    public FileProcessingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}