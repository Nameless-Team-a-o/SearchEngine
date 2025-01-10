package com.nameless.storage_server.controller;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    //TODO : user only can search for his only own projects and can select project
    public ResponseEntity<?> search(@RequestBody SearchRequestDto request ,@RequestHeader("Authorization") String token) {
        return searchService.searchTokens(request , token);
    }

    @GetMapping("/class")
    public ResponseEntity<?> searchClass(@RequestParam("id") Long id  ,@RequestHeader("Authorization") String token){
        return  searchService.searchClass(id,token);
    }
}
