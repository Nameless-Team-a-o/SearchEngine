package com.nameless.storage_server.exception;

public class ZipValidationException extends FileProcessingException {
    public ZipValidationException(String message) {
        super(message, 400);
    }
}