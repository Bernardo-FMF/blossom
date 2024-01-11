package org.blossom.activity.client;

import org.blossom.activity.dto.PostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("post-service")
public interface PostClient {
    @GetMapping("/api/v1/post/{postId}/identifier")
    ResponseEntity<PostDto> getPostIdentifier(@PathVariable("postId") String postId);
}
