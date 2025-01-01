package com.nameless.storage_server.service.normalize;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public interface NormalizationStep {
    String normalize(String token);
}
