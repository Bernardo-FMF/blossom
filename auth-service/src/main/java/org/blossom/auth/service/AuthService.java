package org.blossom.auth.service;

import org.blossom.auth.converter.UserConverter;
import org.blossom.auth.delta.DeltaEngine;
import org.blossom.auth.delta.markable.UserMarkable;
import org.blossom.auth.dto.PasswordRecoveryDto;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.Role;
import org.blossom.auth.entity.User;
import org.blossom.auth.enums.RoleEnum;
import org.blossom.auth.exception.EmailInUseException;
import org.blossom.auth.exception.EmailNotInUseException;
import org.blossom.auth.exception.NoRoleFoundException;
import org.blossom.auth.exception.UsernameInUseException;
import org.blossom.auth.repository.RoleRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.security.PasswordTokenGenerator;
import org.blossom.auth.security.TokenGenerator;
import org.blossom.common.model.dto.TokenDto;
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
    private UserConverter userConverter;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private PasswordTokenGenerator passwordTokenGenerator;

    @Autowired
    private RoleRepository roleRepository;

    public String saveUser(RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UsernameInUseException("Username is already in use");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailInUseException("Email is already in use");
        }

        Optional<Role> role = roleRepository.findByName(RoleEnum.USER);

        if (role.isEmpty()) {
            throw new NoRoleFoundException("User role is not present");
        }

        userRepository.save(Objects.requireNonNull(userConverter.convert(registerDto, role.get())));
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

        String token = passwordTokenGenerator.generateToken();

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

        userRepository.saveAndFlush(user);

        return "Password recovery request completed successfully";
    }
}
