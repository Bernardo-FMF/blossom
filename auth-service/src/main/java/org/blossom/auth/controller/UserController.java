package org.blossom.auth.controller;

import org.blossom.auth.exception.UserNotFoundException;
import org.blossom.auth.service.UserService;
import org.blossom.common.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfileImage(@PathVariable("userId") Integer userId, @RequestParam("file") MultipartFile file, Authentication authentication)
            throws IOException, InterruptedException, UserNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.updateUserImage(userId,
                        ((CommonUserDetails)authentication.getPrincipal()).getUserId(), file));
    }
}