package org.blossom.controller;

import org.blossom.dto.CommentInfoDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.dto.UpdatedCommentDto;
import org.blossom.dto.UserCommentsDto;
import org.blossom.exception.CommentNotFoundException;
import org.blossom.exception.OperationNotAllowedException;
import org.blossom.exception.PostNotFoundException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.model.CommonUserDetails;
import org.blossom.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Integer> createComment(@RequestBody CommentInfoDto commentInfoDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, CommentNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(commentInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Integer commentId, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, CommentNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(commentId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable("commentId") Integer commentId, @RequestBody UpdatedCommentDto updatedCommentDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, CommentNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(commentId, updatedCommentDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserCommentsDto> getUserComments(@PathVariable("userId") Integer userId, SearchParametersDto searchParametersDto, Authentication authentication) throws UserNotFoundException, OperationNotAllowedException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getUserComments(userId, searchParametersDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}