package com.nameless.storage_server.facade;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.dto.SearchResponseDTO;
import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.exception.InvalidRequestException;
import com.nameless.storage_server.facade.interfaces.AbstractOperationFacade;
import com.nameless.storage_server.service.ResponseBuilder;
import com.nameless.storage_server.service.normalize.splitter.SearchTermSplitter;
import com.nameless.storage_server.service.normalize.strategy.NormalizationStrategy;
import com.nameless.storage_server.service.search.TokenInfoHelper;
import com.nameless.storage_server.service.search.strategy.TokenRetrievalStrategy;
import com.nameless.storage_server.service.search.strategy.TokenRetrievalStrategyFactory;
import com.nameless.storage_server.service.normalize.manager.NormalizeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchFacade extends AbstractOperationFacade<SearchRequestDto, List<SearchResponseDTO>> {

    private final NormalizeManager normalizeManager;
    private final TokenRetrievalStrategyFactory strategyFactory;
    private final ResponseBuilder responseBuilder;
    private final SearchTermSplitter searchTermSplitter;

    @Autowired
    public SearchFacade(NormalizeManager normalizeManager,
                        TokenRetrievalStrategyFactory strategyFactory,
                        ResponseBuilder responseBuilder,
                        SearchTermSplitter searchTermSplitter) {
        this.normalizeManager = normalizeManager;
        this.strategyFactory = strategyFactory;
        this.responseBuilder = responseBuilder;
        this.searchTermSplitter = searchTermSplitter;
    }

    @Override
    protected void validateRequest(SearchRequestDto request) {
        if (request == null || request.getSearchTerm() == null) {
            throw new InvalidRequestException("Search request cannot be null");
        }
    }

    @Override
    protected List<SearchResponseDTO> processRequest(SearchRequestDto request) {
        // Get normalization strategy
        NormalizationStrategy normStrategy = normalizeManager.chooseStrategy(
                request.isUseStemming(),
                request.isUseLemmatization()
        );
        List<String> words = searchTermSplitter.handle(request.getSearchTerm());

        List<String> normalizedQuery = normStrategy.normalize(words);

        // Get token retrieval strategy
        TokenRetrievalStrategy retrievalStrategy = strategyFactory.getStrategy(
                request.isExactMatch()
        );

        // Retrieve and process tokens

        //TODO: make interface
        List<?> tokens = retrievalStrategy.retrieveTokens(
                request,
                normalizedQuery,
                request.getTokenTypeDto()
        );

        return processTokens(tokens);
    }

    @Override
    protected ResponseEntity<List<SearchResponseDTO>> buildResponse(List<SearchResponseDTO> result) {
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @Override
    protected ResponseEntity<List<SearchResponseDTO>> handleError(Exception e) {
        if (e instanceof InvalidRequestException) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    private List<SearchResponseDTO> processTokens(List<?> tokens) {
        return tokens.stream()
                .filter(token -> token instanceof Token || token instanceof NormalizeToken)
                .map(TokenInfoHelper::generateTokenInfo)
                .toList();
    }
}
