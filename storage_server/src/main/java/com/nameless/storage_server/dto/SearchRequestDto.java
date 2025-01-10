package com.nameless.storage_server.dto;

public class SearchRequestDto {
    private String searchTerm;
    private boolean useLemmatization;
    private boolean useStemming;
    private boolean exactMatch;
    private TokenTypeDto tokenTypeDto;  // Add the token type

    public SearchRequestDto() {
        this.useLemmatization = true;
        this.useStemming = true;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getTokenTypeDto() {
        return tokenTypeDto.toString();
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

    public boolean isUseStemming() {
        return useStemming;
    }

    public void setUseStemming(boolean useStemming) {
        this.useStemming = useStemming;
    }

    public boolean isUseLemmatization() {
        return useLemmatization;
    }

    public void setUseLemmatization(boolean useLemmatization) {
        this.useLemmatization = useLemmatization;
    }
}
