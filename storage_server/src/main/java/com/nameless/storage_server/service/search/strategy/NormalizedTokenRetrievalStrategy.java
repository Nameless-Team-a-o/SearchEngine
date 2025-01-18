package com.nameless.storage_server.service.search.strategy;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.entity.token.Token;
import com.nameless.storage_server.service.normlizeToken.NormalizeTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NormalizedTokenRetrievalStrategy implements TokenRetrievalStrategy {

    private final NormalizeTokenService normalizeTokenService;

    @Autowired
    public NormalizedTokenRetrievalStrategy(NormalizeTokenService normalizeTokenService) {
        this.normalizeTokenService = normalizeTokenService;
    }

    @Override
    public List<Token> retrieveTokens(SearchRequestDto searchDto, List<String> normalizedQuery, String tokenType) {
        if (normalizedQuery == null || normalizedQuery.isEmpty()) {
            throw new IllegalArgumentException("Normalized query list cannot be null or empty");
        }

        if ("ALL".equalsIgnoreCase(tokenType)) {
            return normalizedQuery.stream()
                    .map(normalizeTokenService::findTop10Token)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } else {
            // Filter by token type
            return normalizedQuery.stream()
                    .map(query -> normalizeTokenService.findTop10ByTokenAndType(query, TokenType.valueOf(tokenType)))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }
}
