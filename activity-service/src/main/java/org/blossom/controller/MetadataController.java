package org.blossom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interaction-metadata")
public class MetadataController {
    @GetMapping("/{postId}")
    public ResponseEntity<String> getPostInteractionMetadata() {
        return null;
    }
}
