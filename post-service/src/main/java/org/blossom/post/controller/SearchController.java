package org.blossom.post.controller;

import org.blossom.post.dto.AggregatePostsDto;
import org.blossom.post.dto.SearchParametersDto;
import org.blossom.post.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post-search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/simple-hashtag-lookup")
    public ResponseEntity<AggregatePostsDto> simpleHashtagLookup(SearchParametersDto searchParameters) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.postHashtagLookup(searchParameters));
    }
}
