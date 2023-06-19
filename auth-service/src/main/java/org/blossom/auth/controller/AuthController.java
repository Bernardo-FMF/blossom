package org.blossom.auth.controller;

import org.blossom.auth.dto.LoginDto;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(registerDto));
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return "Login";
    }

    @GetMapping("/validate")
    public String validate() {
        return "Validated";
    }
}