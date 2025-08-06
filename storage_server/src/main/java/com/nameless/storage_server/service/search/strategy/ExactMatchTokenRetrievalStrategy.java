package com.nameless.storage_server.service.search.strategy;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.entity.token.OriginalToken;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.entity.token.Token;
import com.nameless.storage_server.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExactMatchTokenRetrievalStrategy implements TokenRetrievalStrategy {

    private final TokenService tokenService;

    @Autowired
    public ExactMatchTokenRetrievalStrategy(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public List<Token> retrieveTokens(SearchRequestDto searchDto, List<String> normalizedQuery, String tokenType) {
        if (searchDto == null || searchDto.getSearchTerm() == null || searchDto.getSearchTerm().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }

        if ("ALL".equalsIgnoreCase(tokenType)) {
            return tokenService.findTop10Token(searchDto.getSearchTerm());
        } else {
            // Filter by token type
            return tokenService.findTop10ByTokenAndType(searchDto.getSearchTerm(), TokenType.valueOf(tokenType));
        }
    }
}
