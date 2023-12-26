package org.blossom.post.client;

import org.blossom.post.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("auth-service")
public interface UserClient {
    @GetMapping("/api/v1/user/{userId}")
    ResponseEntity<UserDto> getUserById(@PathVariable("userId") Integer userId);
}
