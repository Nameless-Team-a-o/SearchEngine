package com.nameless.storage_server.exception;

public class QueueException extends FileProcessingException {
  public QueueException(String message) {
    super(message, 500);
  }
}
