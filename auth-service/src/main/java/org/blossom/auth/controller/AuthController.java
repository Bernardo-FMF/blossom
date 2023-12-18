package org.blossom.auth.controller;

import lombok.extern.log4j.Log4j2;
import org.blossom.auth.dto.*;
import org.blossom.auth.exception.*;
import org.blossom.auth.service.AuthService;
import org.blossom.auth.service.UserService;
import org.blossom.model.CommonUserDetails;
import org.blossom.model.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Log4j2
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<GenericResponseDto> register(@RequestBody RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
        log.info("Received request on endpoint /register: Creating user with username: {}; email: {}; name: {}", registerDto.getUsername(), registerDto.getEmail(), registerDto.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.saveUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) throws LoginCredentialsException {
        log.info("Received request on endpoint /login: Login user {}", loginDto.getUsername());
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        if (authenticate.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.generateToken(loginDto.getUsername()));
        } else {
            throw new LoginCredentialsException("Credentials don't match");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenDto> validate(@RequestParam("token") String token) throws UserNotFoundException {
        log.info("Received request on endpoint /validate: Validating token {}", token);
        return ResponseEntity.status(HttpStatus.OK).body(authService.validateToken(token));
    }

    @PostMapping("/password-recovery-request")
    public ResponseEntity<GenericResponseDto> requestPasswordRecovery(@RequestBody PasswordRecoveryDto passwordRecoveryDto) throws EmailNotInUseException {
        log.info("Received request on endpoint /password-recovery-request: Recovering password for user {}", passwordRecoveryDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.requestPasswordRecovery(passwordRecoveryDto));
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<GenericResponseDto> passwordRecovery(@RequestBody PasswordChangeDto passwordChangeDto) throws UserNotFoundException, InvalidTokenException {
        log.info("Received request on endpoint /password-recovery: Recovering password for user {}", passwordChangeDto.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.changePassword(passwordChangeDto));
    }

    @GetMapping("/self")
    public ResponseEntity<SimplifiedUserDto> getSelf(Authentication authentication) throws UserNotFoundException {
        log.info("Received request on endpoint /self: Getting logged in user {}", ((CommonUserDetails) authentication.getPrincipal()).getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}