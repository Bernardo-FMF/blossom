package org.blossom.auth.service;

import org.blossom.auth.dto.*;
import org.blossom.auth.email.EmailService;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.RefreshToken;
import org.blossom.auth.entity.User;
import org.blossom.auth.entity.VerificationToken;
import org.blossom.auth.exception.*;
import org.blossom.auth.factory.impl.PasswordResetFactory;
import org.blossom.auth.factory.impl.RefreshTokenFactory;
import org.blossom.auth.factory.impl.UserFactory;
import org.blossom.auth.factory.impl.VerificationTokenFactory;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.mapper.impl.*;
import org.blossom.auth.repository.PasswordResetRepository;
import org.blossom.auth.repository.RefreshTokenRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.repository.VerificationTokenRepository;
import org.blossom.auth.strategy.impl.JwtTokenStrategy;
import org.blossom.model.dto.ValidatedUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private PasswordResetFactory passwordResetFactory;

    @Autowired
    private JwtTokenStrategy jwtTokenStrategy;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaMessageService messageService;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private UsersDtoMapper usersDtoMapper;

    @Autowired
    private RefreshTokenFactory refreshTokenFactory;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private TokenDtoMapper tokenDtoMapper;

    @Autowired
    private UserTokenDtoMapper userTokenDtoMapper;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private VerificationTokenFactory verificationTokenFactory;

    public GenericResponseDto saveUser(RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
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

        VerificationToken verificationToken = verificationTokenFactory.buildEntity(newUser);

        verificationTokenRepository.save(verificationToken);

        //TODO: do this asynchronously
        emailService.sendVerificationEmail(userDtoMapper.toDto(newUser, verificationToken));

        return genericDtoMapper.toDto("User registered successfully", newUser.getId(), null);
    }

    public UserTokenDto generateToken(String email) throws UserNotFoundException, EmailNotInUseException {
        Optional<User> optionalUser = userRepository.findByEmailAndVerifiedIsTrue(email);
        if (optionalUser.isEmpty()) {
            throw new EmailNotInUseException("User not found");
        }

        User user = optionalUser.get();

        String token = jwtTokenStrategy.generateToken(user);

        RefreshToken refreshToken = refreshTokenFactory.buildEntity(user);

        refreshTokenRepository.save(refreshToken);

        SimplifiedUserDto simplifiedUserDto = usersDtoMapper.toDto(user);

        TokenDto tokenDto = tokenDtoMapper.toDto(token, refreshToken.getToken());

        return userTokenDtoMapper.toDto(simplifiedUserDto, tokenDto);
    }

    public ValidatedUserDto validateToken(String token) throws UserNotFoundException {
        ValidatedUserDto validatedUserDto = jwtTokenStrategy.validateToken(token);
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(validatedUserDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        return validatedUserDto;
    }

    public GenericResponseDto requestPasswordRecovery(PasswordRecoveryDto passwordRecoveryDto) throws EmailNotInUseException, UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmailAndVerifiedIsTrue(passwordRecoveryDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new EmailNotInUseException("User with that email does not exist");
        }

        User user = optionalUser.get();

        PasswordReset passwordReset = passwordResetFactory.buildEntity(user);

        passwordResetRepository.save(passwordReset);

        //TODO: do this asynchronously
        emailService.sendPasswordRecoveryEmail(userDtoMapper.toDto(user, passwordReset));

        return genericDtoMapper.toDto("Password recovery request completed successfully", user.getId(), null);
    }

    public GenericResponseDto changePassword(PasswordChangeDto passwordChangeDto) throws UserNotFoundException, InvalidTokenException, TokenNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(passwordChangeDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        Optional<PasswordReset> optionalPasswordReset = passwordResetRepository.findById(passwordChangeDto.getUserId());
        if (optionalPasswordReset.isEmpty()) {
            throw new TokenNotFoundException("Token was not requested for this user");
        }

        PasswordReset passwordReset = optionalPasswordReset.get();

        if (!passwordReset.getToken().equals(passwordChangeDto.getToken())) {
            throw new InvalidTokenException("Token is invalid");
        }

        if (Instant.now().isAfter(passwordReset.getExpirationDate())) {
            passwordResetRepository.delete(passwordReset);

            throw new InvalidTokenException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

        passwordResetRepository.delete(passwordReset);

        return genericDtoMapper.toDto("Password changed successfully", user.getId(), null);
    }

    public GenericResponseDto verifyUser(EmailVerificationDto emailVerificationDto) throws UserNotFoundException, InvalidOperationException, InvalidTokenException {
        Optional<User> optionalUser = userRepository.findById(emailVerificationDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if (user.isVerified()) {
            throw new InvalidOperationException("Cannot validate email");
        }

        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findById(emailVerificationDto.getUserId());
        if (optionalVerificationToken.isEmpty()) {
            throw new InvalidTokenException("Token has expired");
        }

        VerificationToken verificationToken = optionalVerificationToken.get();

        if (!verificationToken.getToken().equals(emailVerificationDto.getToken())) {
            throw new InvalidTokenException("Token is invalid");
        }

        if (Instant.now().isAfter(verificationToken.getExpirationDate())) {
            verificationTokenRepository.delete(verificationToken);

            throw new InvalidTokenException("Token has expired");
        }

        user.setVerified(true);

        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        messageService.publishCreation(user);

        return genericDtoMapper.toDto("Email verified successfully", user.getId(), null);
    }
}
