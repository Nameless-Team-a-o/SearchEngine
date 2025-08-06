package com.nameless.storage_server.service.search.strategy;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.entity.token.Token;

import java.util.List;
public interface TokenRetrievalStrategy {
    List<Token> retrieveTokens(SearchRequestDto searchDto, List<String> normalizedQuery, String tokenType);
}