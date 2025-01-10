package com.nameless.storage_server.service.normalize.strategy;

import java.util.List;

public interface NormalizationStrategy {
    List<String> normalize(List <String> tokenWords);
}
