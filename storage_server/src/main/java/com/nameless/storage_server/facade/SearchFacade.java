package com.nameless.storage_server.facade;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.dto.SearchResponseDTO;
import com.nameless.storage_server.entity.token.Token;
import com.nameless.storage_server.exception.InvalidRequestException;
import com.nameless.storage_server.facade.interfaces.AbstractOperationFacade;
import com.nameless.storage_server.service.normalize.splitter.SearchTermSplitter;
import com.nameless.storage_server.service.normalize.steps.LemmatizationStep;
import com.nameless.storage_server.service.normalize.steps.NormalizationStep;
import com.nameless.storage_server.service.normalize.steps.StemmingStep;
import com.nameless.storage_server.service.search.TokenInfoHelper;
import com.nameless.storage_server.service.search.strategy.TokenRetrievalStrategy;
import com.nameless.storage_server.service.search.strategy.TokenRetrievalStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchFacade extends AbstractOperationFacade<SearchRequestDto, List<SearchResponseDTO>> {

    private final TokenRetrievalStrategyFactory strategyFactory;
    private final SearchTermSplitter searchTermSplitter;

    @Autowired
    public SearchFacade(TokenRetrievalStrategyFactory strategyFactory,
                        SearchTermSplitter searchTermSplitter) {
        this.strategyFactory = strategyFactory;
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
        List<String> words = searchTermSplitter.handle(request.getSearchTerm());

        // TODO: Enum instead of List.of(...)
        List<NormalizationStep> notmalizationStratigies = List.of(new LemmatizationStep(), new StemmingStep());

        List<String> normalizedQuery = words.stream()
                .flatMap(word -> notmalizationStratigies.stream()
                        .map(normalizationStrategy -> normalizationStrategy.normalize(word)))
                .collect(Collectors.toList());

        // Get token retrieval strategy
        TokenRetrievalStrategy retrievalStrategy = strategyFactory.getStrategy(
                request.isExactMatch()
        );

        //TODO: make interface - Check
        List<Token> tokens = retrievalStrategy.retrieveTokens(
                request,
                normalizedQuery,
                request.getTokenTypeDto().toString()
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

    private List<SearchResponseDTO> processTokens(List<Token> tokens) {
        return tokens.stream()
                .map(TokenInfoHelper::generateTokenInfo)
                .toList();
    }
}
