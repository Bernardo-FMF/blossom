package org.blossom.client;

import org.blossom.kafka.model.LocalPost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("post-service")
public interface PostClient {
    @GetMapping("/{postId}")
    ResponseEntity<LocalPost> getPostIdentifier(@PathVariable("postId") String postId);
}
