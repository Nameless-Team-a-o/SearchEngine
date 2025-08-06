package com.nameless.storage_server.service.normalize.steps;

import org.springframework.stereotype.Component;

import java.util.List;


public interface NormalizationStep {
    String normalize(String word);
}
