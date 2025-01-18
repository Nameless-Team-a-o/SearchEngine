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
}
