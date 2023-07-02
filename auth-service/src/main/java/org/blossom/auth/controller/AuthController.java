package org.blossom.auth.controller;

import org.blossom.auth.dto.LoginDto;
import org.blossom.auth.dto.PasswordRecoveryDto;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.exception.*;
import org.blossom.auth.service.AuthService;
import org.blossom.common.model.CommonUserDetails;
import org.blossom.common.model.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.saveUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) throws LoginCredentialsException {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        if (authenticate.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.generateToken(loginDto.getUsername()));
        } else {
            throw new LoginCredentialsException("Credentials don't match");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenDto> validate(@RequestParam("token") String token) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.validateToken(token));
    }

    @PostMapping("/password-recovery-request")
    public ResponseEntity<String> requestPasswordRecovery(@RequestBody PasswordRecoveryDto passwordRecoveryDto) throws EmailNotInUseException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.requestPasswordRecovery(passwordRecoveryDto));
    }
}