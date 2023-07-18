package org.blossom.auth.service;

import org.blossom.auth.converter.UserConverter;
import org.blossom.auth.delta.DeltaEngine;
import org.blossom.auth.delta.markable.UserMarkable;
import org.blossom.auth.dto.PasswordChangeDto;
import org.blossom.auth.dto.PasswordRecoveryDto;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.dto.UserDto;
import org.blossom.auth.email.EmailService;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.Role;
import org.blossom.auth.entity.User;
import org.blossom.auth.enums.RoleEnum;
import org.blossom.auth.exception.*;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.repository.RoleRepository;
import org.blossom.auth.repository.TokenRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.security.TokenGenerator;
import org.blossom.model.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaMessageService messageService;

    public String saveUser(RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        if (userRepository.existsByUsername(registerDto.getUserName())) {
            throw new UsernameInUseException("Username is already in use");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailInUseException("Email is already in use");
        }

        Optional<Role> role = roleRepository.findByName(RoleEnum.USER);

        if (role.isEmpty()) {
            throw new NoRoleFoundException("User role is not present");
        }

        User newUser = userRepository.save(Objects.requireNonNull(userConverter.convert(registerDto, role.get())));

        messageService.publishCreation(newUser);
        return "User registered successfully";
    }

    public String generateToken(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        if (!user.get().isActive()) {
            throw new UsernameNotFoundException("User is not active");
        }

        return tokenGenerator.generateToken(user.get());
    }

    public TokenDto validateToken(String token) {
        return tokenGenerator.validateToken(token);
    }

    public String requestPasswordRecovery(PasswordRecoveryDto passwordRecoveryDto) throws EmailNotInUseException {
        Optional<User> optionalUser = userRepository.findByEmail(passwordRecoveryDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new EmailNotInUseException("Email not in use");
        }

        String token = tokenGenerator.generateUuidToken();

        User user = optionalUser.get();

        UserMarkable userMarkable = new UserMarkable()
                .markResetPasswordToken(
                        PasswordReset.builder()
                                .user(user)
                                .id(user.getId())
                                .token(token)
                                .expirationDate(LocalDateTime.now().plusHours(1))
                                .build());

        DeltaEngine<UserMarkable, User> deltaEngine = new DeltaEngine<>((markable, entity) -> {
            if (markable.isMarkedResetPasswordToken()) {
                entity.setPasswordResetToken(markable.getDelegate().getPasswordResetToken());
            }
        });

        deltaEngine.applyDelta(userMarkable, user);

        userRepository.save(user);

        emailService.sendPasswordRecoveryEmail(
                UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .token(user.getPasswordResetToken().getToken())
                        .expirationDate(user.getPasswordResetToken().getExpirationDate())
                        .build());

        return "Password recovery request completed successfully";
    }

    public String changePassword(PasswordChangeDto passwordChangeDto) throws UserNotFoundException, InvalidTokenException {
        Optional<User> optionalUser = userRepository.findById(passwordChangeDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if (!user.getPasswordResetToken().getToken().equals(passwordChangeDto.getToken())) {
            throw new InvalidTokenException("Token is invalid");
        }

        if (LocalDateTime.now().isAfter(user.getPasswordResetToken().getExpirationDate())) {
            throw new InvalidTokenException("Token has expired");
        }

        UserMarkable userMarkable = new UserMarkable()
                .markPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));

        DeltaEngine<UserMarkable, User> deltaEngine = new DeltaEngine<>((markable, entity) -> {
            if (markable.isMarkedPassword()) {
                entity.setPassword(markable.getDelegate().getPassword());
            }
        });

        deltaEngine.applyDelta(userMarkable, user);

        PasswordReset passwordReset = user.getPasswordResetToken();

        user.setPasswordResetToken(null);

        userRepository.save(user);
        tokenRepository.delete(passwordReset);

        return "Password changed successfully";
    }
}
