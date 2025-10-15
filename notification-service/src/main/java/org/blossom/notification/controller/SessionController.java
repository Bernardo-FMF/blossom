package org.blossom.notification.controller;

import org.blossom.model.CommonUserDetails;
import org.blossom.notification.cache.SessionCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-session")
public class SessionController {
    @Autowired
    private SessionCacheService sessionCacheService;

    @GetMapping
    public ResponseEntity<String[]> getSessionIds(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(sessionCacheService.getFromCache(((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
