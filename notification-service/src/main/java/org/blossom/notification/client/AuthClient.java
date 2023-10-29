package org.blossom.notification.client;

import org.blossom.notification.dto.LocalTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
public interface AuthClient {
    @GetMapping("/api/v1/auth/validate")
    ResponseEntity<LocalTokenDto> validate(@RequestParam("token") String token);
}
