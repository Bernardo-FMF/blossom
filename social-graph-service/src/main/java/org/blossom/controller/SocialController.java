package org.blossom.controller;

import org.blossom.dto.GraphUserDto;
import org.blossom.dto.RecommendationsDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.dto.SocialRelationDto;
import org.blossom.exception.FollowNotValidException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.model.CommonUserDetails;
import org.blossom.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/social")
public class SocialController {
    @Autowired
    private SocialService socialService;

    @PostMapping
    public ResponseEntity<String> createSocialRelation(@RequestBody SocialRelationDto socialRelationDto, Authentication authentication) throws FollowNotValidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(socialService.createSocialRelation(socialRelationDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSocialRelation(SocialRelationDto socialRelationDto, Authentication authentication) throws FollowNotValidException, UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.deleteSocialRelation(socialRelationDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/self")
    public ResponseEntity<GraphUserDto> getUserSocialGraph(Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.getUserSocialGraph(((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/follow-recommendation")
    public ResponseEntity<RecommendationsDto> getFollowRecommendations(SearchParametersDto searchParameters, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.getFollowRecommendations(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
