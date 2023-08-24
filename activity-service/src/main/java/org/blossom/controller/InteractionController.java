package org.blossom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interaction")
public class InteractionController {
    @GetMapping("/like/user/{id}")
    public ResponseEntity<String> getUserLikes() {
        return null;
    }

    @GetMapping("/save/user/{id}")
    public ResponseEntity<String> getUserSaves() {
        return null;
    }

    @PostMapping("/like")
    public ResponseEntity<String> createLike() {
        return null;
    }

    @DeleteMapping("/like/{interactionId}")
    public ResponseEntity<String> deleteLike() {
        return null;
    }

    @PostMapping("/save")
    public ResponseEntity<String> createSave() {
        return null;
    }

    @DeleteMapping("/save/{interactionId}")
    public ResponseEntity<String> deleteSave() {
        return null;
    }
}
