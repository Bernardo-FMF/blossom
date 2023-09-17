package org.blossom.controller;

import org.blossom.dto.MetadataDto;
import org.blossom.model.CommonUserDetails;
import org.blossom.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataController {
    @Autowired
    private MetadataService metadataService;

    @GetMapping("/{postId}")
    public ResponseEntity<MetadataDto> getPostMetadata(@PathVariable("postId") String postId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(metadataService.getPostMetadata(postId, authentication != null ? ((CommonUserDetails) authentication.getPrincipal()).getUserId() : null));
    }
}
