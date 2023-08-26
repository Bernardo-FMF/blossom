package org.blossom.controller;

import org.blossom.dto.*;
import org.blossom.exception.*;
import org.blossom.model.CommonUserDetails;
import org.blossom.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interaction")
public class InteractionController {
    @Autowired
    private InteractionService interactionService;

    @GetMapping("/like/self")
    public ResponseEntity<UserInteractionsDto> getUserLikes(SearchParametersDto searchParametersDto, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.getUserLikes(searchParametersDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/save/self")
    public ResponseEntity<UserInteractionsDto> getUserSaves(SearchParametersDto searchParametersDto, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.getUserSaves(searchParametersDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PostMapping("/like")
    public ResponseEntity<GenericCreationDto> createLike(InteractionInfoDto interactionInfoDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, InteractionAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.createLike(interactionInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PostMapping("/save")
    public ResponseEntity<GenericCreationDto> createSave(InteractionInfoDto interactionInfoDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, InteractionAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.createSave(interactionInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping("/like/{interactionId}")
    public ResponseEntity<String> deleteLike(@PathVariable("interactionId") Integer interactionId, Authentication authentication) throws UserNotFoundException, PostNotFoundException, InteractionNotFoundException, OperationNotAllowedException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.deleteLike(interactionId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping("/save/{interactionId}")
    public ResponseEntity<String> deleteSave(@PathVariable("interactionId") Integer interactionId, Authentication authentication) throws UserNotFoundException, PostNotFoundException, InteractionNotFoundException, OperationNotAllowedException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.deleteSave(interactionId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/post/{postId}/save")
    public ResponseEntity<InteractionDto> getPostSaveByUser(@PathVariable("postId") String postId, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.findSave(postId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/post/{postId}/like")
    public ResponseEntity<InteractionDto> getPostLikeByUser(@PathVariable("postId") String postId, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.findLike(postId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
