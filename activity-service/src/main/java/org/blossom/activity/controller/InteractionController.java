package org.blossom.activity.controller;

import org.blossom.activity.dto.*;
import org.blossom.activity.exception.*;
import org.blossom.activity.service.InteractionService;
import org.blossom.model.CommonUserDetails;
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
    public ResponseEntity<GenericResponseDto> createLike(@RequestBody InteractionInfoDto interactionInfoDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, InteractionAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.createLike(interactionInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PostMapping("/save")
    public ResponseEntity<GenericResponseDto> createSave(@RequestBody InteractionInfoDto interactionInfoDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, InteractionAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.createSave(interactionInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping("/like/{interactionId}")
    public ResponseEntity<GenericResponseDto> deleteLike(@PathVariable("interactionId") Integer interactionId, Authentication authentication) throws UserNotFoundException, PostNotFoundException, InteractionNotFoundException, OperationNotAllowedException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.deleteLike(interactionId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping("/save/{interactionId}")
    public ResponseEntity<GenericResponseDto> deleteSave(@PathVariable("interactionId") Integer interactionId, Authentication authentication) throws UserNotFoundException, PostNotFoundException, InteractionNotFoundException, OperationNotAllowedException {
        return ResponseEntity.status(HttpStatus.OK).body(interactionService.deleteSave(interactionId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
