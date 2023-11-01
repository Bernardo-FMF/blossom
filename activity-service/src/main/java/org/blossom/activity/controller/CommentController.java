package org.blossom.activity.controller;

import org.blossom.activity.dto.*;
import org.blossom.activity.exception.CommentNotFoundException;
import org.blossom.activity.exception.OperationNotAllowedException;
import org.blossom.activity.exception.PostNotFoundException;
import org.blossom.activity.exception.UserNotFoundException;
import org.blossom.activity.service.CommentService;
import org.blossom.model.CommonUserDetails;
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
    public ResponseEntity<GenericCreationDto> createComment(@RequestBody CommentInfoDto commentInfoDto, Authentication authentication) throws UserNotFoundException, PostNotFoundException, OperationNotAllowedException, CommentNotFoundException {
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

    @GetMapping("/user/self")
    public ResponseEntity<UserCommentsDto> getUserComments(SearchParametersDto searchParametersDto, Authentication authentication) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getUserComments(searchParametersDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostCommentsDto> getPostComments(@PathVariable("postId") String postId, SearchParametersDto searchParametersDto) throws PostNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getPostComments(postId, searchParametersDto));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<PostCommentsDto> getCommentReplies(@PathVariable("commentId") Integer commentId, SearchParametersDto searchParametersDto) throws CommentNotFoundException, PostNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentReplies(commentId, searchParametersDto));
    }
}