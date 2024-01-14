package org.blossom.social.controller;

import org.blossom.social.dto.*;
import org.blossom.social.exception.FollowNotValidException;
import org.blossom.social.exception.UserNotFoundException;
import org.blossom.model.CommonUserDetails;
import org.blossom.social.service.SocialService;
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
    public ResponseEntity<GenericResponseDto> createSocialRelation(@RequestBody SocialRelationDto socialRelationDto, Authentication authentication) throws FollowNotValidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(socialService.createSocialRelation(socialRelationDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping
    public ResponseEntity<GenericResponseDto> deleteSocialRelation(SocialRelationDto socialRelationDto, Authentication authentication) throws FollowNotValidException, UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.deleteSocialRelation(socialRelationDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/follow-metadata")
    public ResponseEntity<FollowMetadataDto> getFollowMetadata(@RequestParam("id") Integer id, Authentication authentication) throws UserNotFoundException {
        Integer userId = authentication != null ? ((CommonUserDetails) authentication.getPrincipal()).getUserId() : null;
        return ResponseEntity.status(HttpStatus.OK).body(socialService.getFollowMetadata(id, userId));
    }

    @GetMapping("/follower")
    public ResponseEntity<GraphUserDto> getUserFollowers(SearchParametersDto searchParameters, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.getUserFollowers(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/following")
    public ResponseEntity<GraphUserDto> getUserFollowings(SearchParametersDto searchParameters, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.getUserFollowings(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/follow-recommendation")
    public ResponseEntity<RecommendationsDto> getFollowRecommendations(SearchParametersDto searchParameters, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(socialService.getFollowRecommendations(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
