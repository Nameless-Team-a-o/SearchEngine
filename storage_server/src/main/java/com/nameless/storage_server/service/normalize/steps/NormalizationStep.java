package com.nameless.storage_server.service.normalize.steps;


import org.springframework.stereotype.Component;

@Component
public interface NormalizationStep {
    String normalize(String token , boolean both );
}
