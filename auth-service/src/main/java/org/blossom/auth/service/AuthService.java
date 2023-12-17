package org.blossom.auth.service;

import org.blossom.auth.dto.PasswordChangeDto;
import org.blossom.auth.dto.PasswordRecoveryDto;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.dto.UserDto;
import org.blossom.auth.email.EmailService;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.*;
import org.blossom.auth.factory.impl.UserFactory;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.repository.TokenRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.strategy.impl.JwtTokenStrategy;
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
    private UserFactory userFactory;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtTokenStrategy jwtTokenStrategy;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaMessageService messageService;

    public String saveUser(RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UsernameInUseException("Username is already in use");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailInUseException("Email is already in use");
        }

        User factoryUser = userFactory.buildEntity(registerDto);
        if (Objects.isNull(factoryUser)) {
            throw new NoRoleFoundException("User role is not present");
        }

        User newUser = userRepository.save(factoryUser);

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

        return jwtTokenStrategy.generateToken(user.get());
    }

    public TokenDto validateToken(String token) throws UserNotFoundException {
        TokenDto tokenDto = jwtTokenStrategy.validateToken(token);
        if (!userRepository.existsById(tokenDto.getUserId())) {
            throw new UserNotFoundException("User does not exist");
        }
        return tokenDto;
    }

    public String requestPasswordRecovery(PasswordRecoveryDto passwordRecoveryDto) throws EmailNotInUseException {
        Optional<User> optionalUser = userRepository.findByEmail(passwordRecoveryDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new EmailNotInUseException("Email not in use");
        }

        String token = jwtTokenStrategy.generateGenericToken();

        User user = optionalUser.get();

        PasswordReset passwordReset = PasswordReset.builder()
                .user(user)
                .id(user.getId())
                .token(token)
                .expirationDate(LocalDateTime.now().plusHours(1))
                .build();

        user.setPasswordResetToken(passwordReset);

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

        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));

        PasswordReset passwordReset = user.getPasswordResetToken();

        user.setPasswordResetToken(null);

        userRepository.save(user);
        tokenRepository.delete(passwordReset);

        return "Password changed successfully";
    }
}
