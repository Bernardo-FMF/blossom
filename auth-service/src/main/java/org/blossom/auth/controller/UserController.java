package org.blossom.auth.controller;

import lombok.extern.log4j.Log4j2;
import org.blossom.auth.dto.GenericResponseDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.exception.FileDeleteException;
import org.blossom.auth.exception.FileUploadException;
import org.blossom.auth.exception.UserNotFoundException;
import org.blossom.auth.service.UserService;
import org.blossom.model.CommonUserDetails;
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
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponseDto> updateProfileImage(@RequestParam("file") MultipartFile file, Authentication authentication)
            throws IOException, InterruptedException, UserNotFoundException, FileUploadException, FileDeleteException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /user/profile-image: Updating profile image for user with id {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.updateUserImage(userId, file));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SimplifiedUserDto> getUserById(@PathVariable("userId") Integer userId) throws UserNotFoundException {
        log.info("Received request on endpoint /user/{userId}: Fetching user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }
}