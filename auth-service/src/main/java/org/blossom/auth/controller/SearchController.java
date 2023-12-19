package org.blossom.auth.controller;

import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/simple-lookup")
    public ResponseEntity<UsersDto> simpleUserLookup(SearchParametersDto searchParameters) {
        log.info("Received request on endpoint /user-search/simple-lookup: Looking up users that match the string {}, page {}, limit {}", searchParameters.getContains(), searchParameters.getPage(), searchParameters.getPageLimit());
        return ResponseEntity.status(HttpStatus.OK).body(searchService.userLookup(searchParameters));
    }

    @GetMapping("/username-lookup")
    public ResponseEntity<SimplifiedUserDto> usernameLookup(@RequestParam("username") String username) {
        log.info("Received request on endpoint /user-search/username-lookup: Looking up user with username {}", username);
        return ResponseEntity.status(HttpStatus.OK).body(searchService.userLookupByUsername(username));
    }
}
