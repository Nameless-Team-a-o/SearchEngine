package com.nameless.storage_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchRequestDto {
    private Long projectId;
    private String searchTerm;
    private boolean exactMatch;
    private TokenTypeDto tokenTypeDto;



    public SearchRequestDto(Long projectId, TokenTypeDto tokenTypeDto, boolean exactMatch, String searchTerm) {
        this.projectId = projectId;
        this.tokenTypeDto = tokenTypeDto;
        this.exactMatch = exactMatch;
        this.searchTerm = searchTerm;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public TokenTypeDto getTokenTypeDto() {
        return tokenTypeDto;
    }

    public void setTokenTypeDto(TokenTypeDto tokenTypeDto) {
        this.tokenTypeDto = tokenTypeDto;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
