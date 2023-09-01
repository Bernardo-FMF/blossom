package org.blossom.client;

import org.blossom.kafka.inbound.model.LocalUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("auth-service")
public interface UserClient {
    @GetMapping("/api/v1/user/{userId}")
    ResponseEntity<LocalUser> getUserById(@PathVariable("userId") Integer userId);
}
