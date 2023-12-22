package org.blossom.post.controller;

import org.blossom.model.CommonUserDetails;
import org.blossom.post.dto.*;
import org.blossom.post.exception.*;
import org.blossom.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(PostInfoDto postInfoDto, Authentication authentication) throws IOException, InterruptedException, PostNotValidException, FileUploadException {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId") String postId, Authentication authentication) throws PostNotFoundException, OperationNotAllowedException, FileDeleteException {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AggregateUserPostsDto> getPostsByUser(@PathVariable("userId") Integer userId, SearchParametersDto searchParameters) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findByUser(userId, searchParameters));
    }

    @GetMapping("/{postId}/identifier")
    public ResponseEntity<PostIdentifierDto> getPostIdentifier(@PathVariable("postId") String postId) throws PostNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostIdentifier(postId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostWithUserDto> getPost(@PathVariable("postId") String postId) throws PostNotFoundException, UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }
}