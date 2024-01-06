package org.blossom.auth.controller;

import org.blossom.auth.CommonRequestHelper;
import org.blossom.auth.dto.*;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.model.ErrorMessage;
import org.blossom.model.dto.ValidatedUserDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends CommonRequestHelper {
    private static final String USERNAME_1 = "johnDoe01";
    private static final String EMAIL_1 = "john.doe@test.pt";
    private static final String NAME_1 = "John Doe";
    private static final String PASSWORD_1 = "password";

    @Order(1)
    @Test
    void registerUserAndValidateEmail_successfulRegistration() throws Exception {
        ArgumentCaptor<UserDto> userDtoArgumentCaptor = ArgumentCaptor.forClass(UserDto.class);

        MvcResult registerResult = registerUser(USERNAME_1, EMAIL_1, NAME_1, PASSWORD_1, MockMvcResultMatchers.status().isCreated());

        GenericResponseDto responseDto = objectMapper.readValue(registerResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals(1, responseDto.getResourceId());
        Assertions.assertEquals("User registered successfully", responseDto.getResponseMessage());

        Optional<User> optionalUser = userRepository.findById(responseDto.getResourceId());
        Assertions.assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();

        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals(USERNAME_1, user.getUsername());
        Assertions.assertEquals(EMAIL_1, user.getEmail());
        Assertions.assertEquals(NAME_1, user.getFullName());
        Assertions.assertFalse(user.isVerified());

        Mockito.verify(emailService).sendVerificationEmail(userDtoArgumentCaptor.capture());

        UserDto userDto = userDtoArgumentCaptor.getValue();

        EmailVerificationDto emailVerificationDto = new EmailVerificationDto();
        emailVerificationDto.setToken(userDto.getToken());
        emailVerificationDto.setUserId(userDto.getId());

        MvcResult result1 = mockMvc.perform(post("/api/v1/auth/email-verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailVerificationDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto responseDto1 = objectMapper.readValue(result1.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals(1, responseDto1.getResourceId());
        Assertions.assertEquals("Email verified successfully", responseDto1.getResponseMessage());
    }

    @Order(2)
    @Test
    void registerUser_errorUsernameAlreadyExists() throws Exception {
        MvcResult registerResult = registerUser(USERNAME_1, EMAIL_1, NAME_1, PASSWORD_1, MockMvcResultMatchers.status().isConflict());

        ErrorMessage errorDto = objectMapper.readValue(registerResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Username is already in use", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.CONFLICT, errorDto.getStatus());
    }

    @Order(3)
    @Test
    void registerUser_errorEmailAlreadyExists() throws Exception {
        MvcResult registerResult = registerUser(USERNAME_1 + "Mock", EMAIL_1, NAME_1, PASSWORD_1, MockMvcResultMatchers.status().isConflict());

        ErrorMessage errorDto = objectMapper.readValue(registerResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Email is already in use", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.CONFLICT, errorDto.getStatus());
    }

    @Order(4)
    @Test
    void loginUserAndValidate_successfulLogin() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(EMAIL_1);
        loginDto.setPassword(PASSWORD_1);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserTokenDto userTokenDto = objectMapper.readValue(loginResult.getResponse().getContentAsString(), UserTokenDto.class);

        MvcResult validateResult = mockMvc.perform(get("/api/v1/auth/validate?token=" + userTokenDto.getToken().getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ValidatedUserDto validatedUserDto = objectMapper.readValue(validateResult.getResponse().getContentAsString(), ValidatedUserDto.class);

        Assertions.assertEquals(1, validatedUserDto.getUserId());
        Assertions.assertEquals(USERNAME_1, validatedUserDto.getUsername());
        Assertions.assertEquals(1, validatedUserDto.getAuthorities().size());

        Optional<SimpleGrantedAuthority> authority = validatedUserDto.getAuthorities().stream().findFirst();
        Assertions.assertTrue(authority.isPresent());
        Assertions.assertEquals("USER", authority.get().getAuthority());
    }

    @Order(5)
    @Test
    void loginUserAndValidate_wrongPassword() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(EMAIL_1);
        loginDto.setPassword(PASSWORD_1 + "1");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(loginResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Bad credentials", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, errorDto.getStatus());
    }

    @Order(6)
    @Test
    void loginUserAndValidate_userDoesNotExist() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(USERNAME_1 + "Mock");
        loginDto.setPassword(PASSWORD_1);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(loginResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Bad credentials", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, errorDto.getStatus());
    }

    @Order(7)
    @Test
    void recoverPassword_recoverSuccessful() throws Exception {
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setEmail(EMAIL_1);

        ArgumentCaptor<UserDto> userDtoArgumentCaptor = ArgumentCaptor.forClass(UserDto.class);

        MvcResult recoveryRequestResult = mockMvc.perform(post("/api/v1/auth/password-recovery-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto requestResponseDto = objectMapper.readValue(recoveryRequestResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals(1, requestResponseDto.getResourceId());
        Assertions.assertEquals("Password recovery request completed successfully", requestResponseDto.getResponseMessage());

        Mockito.verify(emailService).sendPasswordRecoveryEmail(userDtoArgumentCaptor.capture());

        UserDto userDto = userDtoArgumentCaptor.getValue();
        Assertions.assertEquals(1, userDto.getId());
        Assertions.assertEquals(USERNAME_1, userDto.getUsername());
        Assertions.assertEquals(EMAIL_1, userDto.getEmail());

        String recoveryToken = userDto.getToken();

        Optional<PasswordReset> optionalPasswordReset = passwordResetRepository.findById(requestResponseDto.getResourceId());

        Optional<User> optionalUser = userRepository.findById(requestResponseDto.getResourceId());
        User user = optionalUser.orElse(null);

        Assertions.assertTrue(optionalPasswordReset.isPresent());

        PasswordReset passwordReset = optionalPasswordReset.get();
        Assertions.assertEquals(passwordReset.getToken(), recoveryToken);

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setUserId(requestResponseDto.getResourceId());
        passwordChangeDto.setToken(recoveryToken);
        passwordChangeDto.setNewPassword(PASSWORD_1 + "1");

        MvcResult recoveryResult = mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto recoveryResponseDto = objectMapper.readValue(recoveryResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals(1, recoveryResponseDto.getResourceId());
        Assertions.assertEquals("Password changed successfully", recoveryResponseDto.getResponseMessage());

        Optional<User> optionalUpdatedUser = userRepository.findById(requestResponseDto.getResourceId());
        Assertions.assertTrue(optionalUpdatedUser.isPresent());

        User updatedUser = optionalUpdatedUser.get();
        Assertions.assertEquals(1, userDto.getId());
        Assertions.assertEquals(USERNAME_1, userDto.getUsername());
        Assertions.assertEquals(EMAIL_1, userDto.getEmail());

        Optional<PasswordReset> optionalPasswordReset1 = passwordResetRepository.findById(requestResponseDto.getResourceId());

        Assertions.assertNotEquals(updatedUser.getPassword(), user.getPassword());
        Assertions.assertTrue(optionalPasswordReset1.isEmpty());
    }

    @Order(8)
    @Test
    void recoverPassword_errorEmailNotFound() throws Exception {
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setEmail("Mock" + EMAIL_1);

        MvcResult recoveryRequestResult = mockMvc.perform(post("/api/v1/auth/password-recovery-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(recoveryRequestResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("User with that email does not exist", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, errorDto.getStatus());
    }

    @Order(9)
    @Test
    void recoverPassword_errorUserNotFound() throws Exception {
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setUserId(2);
        passwordChangeDto.setToken("fakeToken");
        passwordChangeDto.setNewPassword(PASSWORD_1 + "2");

        MvcResult recoveryRequestResult = mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(recoveryRequestResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("User does not exist", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, errorDto.getStatus());
    }

    @Order(10)
    @Test
    void recoverPassword_errorTokenNotRequested() throws Exception {
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setUserId(1);
        passwordChangeDto.setToken("fakeToken");
        passwordChangeDto.setNewPassword(PASSWORD_1 + "2");

        MvcResult recoveryRequestResult = mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(recoveryRequestResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Token was not requested for this user", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, errorDto.getStatus());
    }

    @Order(11)
    @Test
    void recoverPassword_errorWrongToken() throws Exception {
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setEmail(EMAIL_1);

        MvcResult recoveryRequestResult = mockMvc.perform(post("/api/v1/auth/password-recovery-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto requestResponseDto = objectMapper.readValue(recoveryRequestResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals(1, requestResponseDto.getResourceId());
        Assertions.assertEquals("Password recovery request completed successfully", requestResponseDto.getResponseMessage());

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setUserId(requestResponseDto.getResourceId());
        passwordChangeDto.setToken("fakeToken");
        passwordChangeDto.setNewPassword(PASSWORD_1 + "1");

        Optional<User> optionalUser = userRepository.findById(requestResponseDto.getResourceId());
        Assertions.assertTrue(optionalUser.isPresent());

        MvcResult recoveryResult = mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(recoveryResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Token is invalid", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorDto.getStatus());

        Optional<User> optionalUpdatedUser = userRepository.findById(requestResponseDto.getResourceId());
        Assertions.assertTrue(optionalUpdatedUser.isPresent());

        Assertions.assertEquals(optionalUpdatedUser.get().getPassword(), optionalUser.get().getPassword());
    }

    @Order(12)
    @Test
    void getSelf_successful() throws Exception {
        MvcResult validateResult = mockMvc.perform(get("/api/v1/auth/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", USERNAME_1)
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        SimplifiedUserDto userDto = objectMapper.readValue(validateResult.getResponse().getContentAsString(), SimplifiedUserDto.class);

        Assertions.assertEquals(1, userDto.getId());
        Assertions.assertEquals(USERNAME_1, userDto.getUsername());
        Assertions.assertEquals(NAME_1, userDto.getFullName());
    }
}