package org.blossom.controller;

import org.blossom.dto.PostInfoDto;
import org.blossom.exception.UserNotFoundException;
import org.blossom.model.CommonUserDetails;
import org.blossom.service.PostService;
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
    public ResponseEntity<String> createPost(PostInfoDto postInfoDto, Authentication authentication) throws UserNotFoundException, IOException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postInfoDto, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

}
