package org.blossom.controller;

import jakarta.validation.Valid;
import org.blossom.dto.LocalUsersDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/user-lookup")
    public ResponseEntity<LocalUsersDto> searchUserResources(@Valid SearchParametersDto searchParameters) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.userLookup(searchParameters));
    }
}
