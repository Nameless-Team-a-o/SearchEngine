package com.nameless.storage_server.service.search.strategy;

import com.nameless.storage_server.dto.SearchRequestDto;

import java.util.List;
public interface TokenRetrievalStrategy {
    List<?> retrieveTokens(SearchRequestDto searchDto, List<String> normalizedQuery, String tokenType);
}