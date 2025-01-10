package com.nameless.storage_server.exception;

public class SubmissionException extends FileProcessingException {
    public SubmissionException(String message) {
        super(message, 500);
    }
}