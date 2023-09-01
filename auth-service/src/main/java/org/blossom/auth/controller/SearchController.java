package org.blossom.auth.controller;

import org.blossom.auth.dto.SearchParametersDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.dto.UsersDto;
import org.blossom.auth.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/simple-lookup")
    public ResponseEntity<UsersDto> simpleUserLookup(SearchParametersDto searchParameters) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.userLookup(searchParameters));
    }

    @GetMapping("/username-lookup")
    public ResponseEntity<SimplifiedUserDto> usernameLookup(@RequestParam("username") String username) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.userLookupByUsername(username));
    }
}
