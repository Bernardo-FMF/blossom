package org.blossom.auth.controller;

import lombok.extern.log4j.Log4j2;
import org.blossom.auth.dto.*;
import org.blossom.auth.exception.*;
import org.blossom.auth.service.AuthService;
import org.blossom.auth.service.UserService;
import org.blossom.model.CommonUserDetails;
import org.blossom.model.dto.ValidatedUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        log.info("Received request on endpoint /auth/register: Creating user with username: {}; email: {}; name: {}", registerDto.getUsername(), registerDto.getEmail(), registerDto.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.saveUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenDto> login(@RequestBody LoginDto loginDto) throws UsernameNotFoundException, EmailNotInUseException {
        log.info("Received request on endpoint /auth/login: Login user {}", loginDto.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.login(loginDto.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponseDto> logout(@RequestParam("token") String token, Authentication authentication) throws UsernameNotFoundException, UserNotFoundException, InvalidTokenException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/logout: Logout user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.deleteToken(userId, token));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<GenericResponseDto> logoutAll(Authentication authentication) throws UsernameNotFoundException, UserNotFoundException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/logout: Logout all sessions for user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.deleteTokens(userId));
    }

    @PutMapping("/update-email")
    public ResponseEntity<GenericResponseDto> updateEmail(@RequestBody EmailUpdateDto emailUpdateDto, Authentication authentication) throws UserNotFoundException, EmailInUseException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/update-email: Updating email of user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.updateEmail(emailUpdateDto, userId));
    }

    @PostMapping("/email-verify")
    public ResponseEntity<GenericResponseDto> verifyEmail(@RequestBody EmailVerificationDto emailVerificationDto) throws UserNotFoundException, InvalidTokenException, InvalidOperationException {
        log.info("Received request on endpoint /auth/email-verify: Verifying user {}", emailVerificationDto.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.verifyUser(emailVerificationDto));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidatedUserDto> validate(@RequestParam("token") String token) throws UserNotFoundException {
        log.info("Received request on endpoint /auth/validate: Validating token {}", token);
        return ResponseEntity.status(HttpStatus.OK).body(authService.validateToken(token));
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestParam("refreshToken") String refreshToken, Authentication authentication) throws UserNotFoundException, InvalidTokenException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/refresh: Refreshing token for user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.refreshToken(refreshToken, userId));
    }

    @PostMapping("/password-recovery-request")
    public ResponseEntity<GenericResponseDto> requestPasswordRecovery(@RequestBody PasswordRecoveryRequestDto passwordRecoveryRequestDto) throws EmailNotInUseException, UserNotFoundException {
        log.info("Received request on endpoint /auth/password-recovery-request: Recovering password for user {}", passwordRecoveryRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.requestPasswordRecovery(passwordRecoveryRequestDto));
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<GenericResponseDto> passwordRecovery(@RequestBody PasswordRecoveryDto passwordRecoveryDto) throws UserNotFoundException, InvalidTokenException, TokenNotFoundException {
        log.info("Received request on endpoint /auth/password-recovery: Recovering password for user {}", passwordRecoveryDto.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.recoverPassword(passwordRecoveryDto));
    }

    @PutMapping("/update-password")
    public ResponseEntity<GenericResponseDto> updatePassword(@RequestBody PasswordChangeDto passwordChangeDto, Authentication authentication) throws UserNotFoundException, InvalidTokenException, TokenNotFoundException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/update-password: Changing password for user {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.updatePassword(passwordChangeDto, userId));
    }


    @GetMapping("/logged-user")
    public ResponseEntity<LoggedUserDto> getSelf(Authentication authentication) throws UserNotFoundException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/logged-user: Getting logged in user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.getLoggedUser(userId));
    }

    @DeleteMapping("/")
    public ResponseEntity<GenericResponseDto> deleteAccount(Authentication authentication) throws UserNotFoundException, FileDeleteException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth: Deleting user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.deleteAccount(userId));
    }

    @PostMapping("/mfa-qr-code")
    public ResponseEntity<GenericResponseDto> createMfaQrcode(Authentication authentication) throws UserNotFoundException, InvalidOperationException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/mfa-qr-code: Creating mfa qr code for user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.generateMfaQrCode(userId));
    }

    @PostMapping("/mfa")
    public ResponseEntity<GenericResponseDto> enableMfa(@RequestBody MfaValidationDto mfaValidationDto, Authentication authentication) throws UserNotFoundException, InvalidOperationException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/mfa: Enabling mfa for user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.enableMfa(mfaValidationDto, userId));
    }

    @DeleteMapping("/mfa")
    public ResponseEntity<GenericResponseDto> disableMfa(Authentication authentication) throws UserNotFoundException, InvalidOperationException {
        int userId = ((CommonUserDetails) authentication.getPrincipal()).getUserId();
        log.info("Received request on endpoint /auth/mfa: Disabling mfa for user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(authService.disableMfa(userId));
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<UserTokenDto> verifyMfa(@RequestBody MfaVerificationDto mfaVerificationDto) throws UserNotFoundException, BadCredentialsException {
        log.info("Received request on endpoint /auth/verify-mfa: Verifying mfa for user {}", mfaVerificationDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(authService.validateMfa(mfaVerificationDto));
    }
}