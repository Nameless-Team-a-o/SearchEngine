package com.nameless.storage_server.exception;

public class JavaFileSaveException extends FileProcessingException {
    public JavaFileSaveException(String message) {
        super(message, 500);
    }
}