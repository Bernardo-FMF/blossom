package org.blossom.auth.service;

import jakarta.transaction.Transactional;
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
import org.blossom.auth.grpc.GrpcClientImageService;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.mapper.impl.*;
import org.blossom.auth.repository.PasswordResetRepository;
import org.blossom.auth.repository.RefreshTokenRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.repository.VerificationTokenRepository;
import org.blossom.auth.strategy.impl.JwtTokenStrategy;
import org.blossom.model.dto.ValidatedUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
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

    @Autowired
    private MultiFactorAuthService multiFactorAuthService;

    @Autowired
    private LoggedUserDtoMapper loggedUserDtoMapper;

    @Autowired
    private GrpcClientImageService imageService;

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

        emailService.sendVerificationEmail(userDtoMapper.toDto(newUser, verificationToken));

        return genericDtoMapper.toDto("User registered successfully", newUser.getId(), null);
    }

    public UserTokenDto login(String email) throws EmailNotInUseException {
        Optional<User> optionalUser = userRepository.findByEmailAndVerifiedIsTrue(email);
        if (optionalUser.isEmpty()) {
            throw new EmailNotInUseException("User not found");
        }

        User user = optionalUser.get();

        LoggedUserDto loggedUserDto = loggedUserDtoMapper.toDto(user);

        if (user.isMfaEnabled()) {
            return userTokenDtoMapper.toDto(loggedUserDto);
        }

        String token = jwtTokenStrategy.generateToken(user);

        RefreshToken refreshToken = refreshTokenFactory.buildEntity(user);

        refreshTokenRepository.save(refreshToken);

        TokenDto tokenDto = tokenDtoMapper.toDto(token, refreshToken.getToken());

        return userTokenDtoMapper.toDto(loggedUserDto, tokenDto);
    }

    public ValidatedUserDto validateToken(String token) throws UserNotFoundException {
        ValidatedUserDto validatedUserDto = jwtTokenStrategy.validateToken(token);
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(validatedUserDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();
        validatedUserDto.setAuthorities(user.getAuthorities());

        return validatedUserDto;
    }

    public GenericResponseDto requestPasswordRecovery(PasswordRecoveryRequestDto passwordRecoveryRequestDto) throws EmailNotInUseException {
        Optional<User> optionalUser = userRepository.findByEmailAndVerifiedIsTrue(passwordRecoveryRequestDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new EmailNotInUseException("User with that email does not exist");
        }

        User user = optionalUser.get();

        PasswordReset passwordReset = passwordResetFactory.buildEntity(user);

        passwordResetRepository.save(passwordReset);

        emailService.sendPasswordRecoveryEmail(userDtoMapper.toDto(user, passwordReset));

        return genericDtoMapper.toDto("Password recovery request completed successfully", user.getId(), null);
    }

    public GenericResponseDto recoverPassword(PasswordRecoveryDto passwordRecoveryDto) throws UserNotFoundException, InvalidTokenException, TokenNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(passwordRecoveryDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        Optional<PasswordReset> optionalPasswordReset = passwordResetRepository.findById(passwordRecoveryDto.getUserId());
        if (optionalPasswordReset.isEmpty()) {
            throw new TokenNotFoundException("Token was not requested for this user");
        }

        PasswordReset passwordReset = optionalPasswordReset.get();

        if (!passwordReset.getToken().equals(passwordRecoveryDto.getToken())) {
            throw new InvalidTokenException("Token is invalid");
        }

        if (Instant.now().isAfter(passwordReset.getExpirationDate())) {
            passwordResetRepository.delete(passwordReset);

            throw new InvalidTokenException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(passwordRecoveryDto.getNewPassword()));
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

    @Transactional
    public GenericResponseDto deleteTokens(int userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        refreshTokenRepository.deleteByUserId(userId);

        return genericDtoMapper.toDto("Logged out all active sessions successfully", userId, null);
    }

    public GenericResponseDto deleteToken(int userId, String token) throws UserNotFoundException, InvalidTokenException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByTokenAndUserId(token, userId);
        if (optionalRefreshToken.isEmpty()) {
            throw new InvalidTokenException("Token does not exist");
        }

        RefreshToken refreshToken = optionalRefreshToken.get();

        if (refreshToken.getUser().getId() != userId) {
            throw new InvalidTokenException("Invalid token for user");
        }

        refreshTokenRepository.delete(refreshToken);

        return genericDtoMapper.toDto("Logged out all active sessions successfully", userId, null);
    }

    public GenericResponseDto updateEmail(EmailUpdateDto emailUpdateDto, int userId) throws UserNotFoundException, EmailInUseException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if (!user.getEmail().equals(emailUpdateDto.getOldEmail())) {
            throw new UserNotFoundException("Email does not match");
        }

        if (!passwordEncoder.matches(emailUpdateDto.getPasswordConfirmation(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect credentials");
        }

        if (userRepository.existsByEmail(emailUpdateDto.getNewEmail())) {
            throw new EmailInUseException("Email is already in use");
        }

        user.setEmail(emailUpdateDto.getNewEmail());
        userRepository.save(user);

        return genericDtoMapper.toDto("Email updated successfully", userId, Map.of("newEmail", emailUpdateDto.getNewEmail()));
    }

    @Transactional
    public GenericResponseDto deleteAccount(int userId) throws UserNotFoundException, FileDeleteException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        refreshTokenRepository.deleteByUserId(userId);
        verificationTokenRepository.deleteById(userId);
        passwordResetRepository.deleteById(userId);
        userRepository.delete(user);

        if (user.getImageUrl() != null) {
            imageService.deleteImage(user.getImageUrl());
        }

        messageService.publishDelete(user);

        return genericDtoMapper.toDto("Deleted user successfully", userId, null);
    }

    public TokenDto refreshToken(String token, int userId) throws InvalidTokenException, UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByTokenAndUserId(token, userId);
        if (optionalRefreshToken.isEmpty()) {
            throw new InvalidTokenException("Token does not exist");
        }

        RefreshToken refreshToken = optionalRefreshToken.get();

        if (Instant.now().isAfter(refreshToken.getExpirationDate())) {
            refreshTokenRepository.delete(refreshToken);

            throw new InvalidTokenException("Token has expired");
        }

        User user = optionalUser.get();

        return tokenDtoMapper.toDto(jwtTokenStrategy.generateToken(user), refreshToken.getToken());
    }

    public GenericResponseDto generateMfaQrCode(int userId) throws InvalidOperationException, UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if (user.isMfaEnabled()) {
            throw new InvalidOperationException("Multi-factor authentication already enabled");
        }

        if (Objects.isNull(user.getSecret())) {
            user.setSecret(multiFactorAuthService.generateNewSecret());

            userRepository.save(user);
        }

        String qrCode = multiFactorAuthService.generateQrCodeImageUri(user.getSecret());

        return genericDtoMapper.toDto("Qr code generated successfully", userId, Map.of("qrCode", qrCode));
    }

    public GenericResponseDto enableMfa(MfaValidationDto mfaValidationDto, int userId) throws UserNotFoundException, InvalidOperationException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if (user.isMfaEnabled()) {
            throw new InvalidOperationException("Multi-factor authentication already enabled");
        }

        if (Objects.isNull(user.getSecret())) {
            throw new InvalidOperationException("Multi-factor authentication enabling failed");
        }

        if (!multiFactorAuthService.isOtpValid(user.getSecret(), mfaValidationDto.getCode())) {
            throw new BadCredentialsException("Incorrect multi-factor authentication code");
        }

        user.setMfaEnabled(true);

        userRepository.save(user);

        return genericDtoMapper.toDto("Multi-factor authentication successfully enabled", userId, null);
    }

    public GenericResponseDto disableMfa(int userId) throws UserNotFoundException, InvalidOperationException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        if (!user.isMfaEnabled()) {
            throw new InvalidOperationException("Multi-factor authentication already disabled");
        }

        user.setMfaEnabled(false);
        user.setSecret(null);

        userRepository.save(user);

        return genericDtoMapper.toDto("Multi-factor authentication successfully disabled", userId, null);
    }

    public UserTokenDto validateMfa(MfaVerificationDto mfaVerificationDto) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmailAndVerifiedIsTrue(mfaVerificationDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();
        if (!multiFactorAuthService.isOtpValid(user.getSecret(), mfaVerificationDto.getCode())) {
            throw new BadCredentialsException("Incorrect multi-factor authentication code");
        }

        String token = jwtTokenStrategy.generateToken(user);

        RefreshToken refreshToken = refreshTokenFactory.buildEntity(user);

        refreshTokenRepository.save(refreshToken);

        LoggedUserDto loggedUserDto = loggedUserDtoMapper.toDto(user);

        TokenDto tokenDto = tokenDtoMapper.toDto(token, refreshToken.getToken());

        return userTokenDtoMapper.toDto(loggedUserDto, tokenDto);
    }

    public LoggedUserDto getLoggedUser(Integer userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user = optionalUser.get();

        return loggedUserDtoMapper.toDto(user);
    }

    public GenericResponseDto updatePassword(PasswordChangeDto passwordChangeDto, int userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect credentials");
        }

        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordConfirmation())) {
            throw new BadCredentialsException("Incorrect credentials");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));

        userRepository.save(user);

        return genericDtoMapper.toDto("Password updated successfully", userId, null);
    }
}
