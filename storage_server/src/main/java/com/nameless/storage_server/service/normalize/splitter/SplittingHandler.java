package com.nameless.storage_server.service.normalize.splitter;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface SplittingHandler {
    void setNext(SplittingHandler next);
    List<String> handle(String token);
}
