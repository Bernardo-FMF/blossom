package org.blossom.feed.controller;

import org.blossom.feed.dto.FeedDto;
import org.blossom.feed.dto.SearchParametersDto;
import org.blossom.feed.exception.UserNotFoundException;
import org.blossom.feed.service.FeedService;
import org.blossom.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedDto> getFeed(SearchParametersDto searchParametersDto, Authentication authentication) throws UserNotFoundException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(authentication != null ? feedService.getUserFeed(((CommonUserDetails) authentication.getPrincipal()).getUserId(), searchParametersDto) : feedService.getGenericFeed(searchParametersDto));
    }
}
