package org.blossom.auth.controller;

import org.blossom.auth.AbstractContextBeans;
import org.blossom.auth.dto.*;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.model.ErrorMessage;
import org.blossom.auth.repository.UserRepository;
import org.blossom.model.dto.TokenDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends AbstractContextBeans {
    public static final String USERNAME_1 = "johnDoe01";
    public static final String EMAIL_1 = "john.doe@test.pt";
    public static final String NAME_1 = "John Doe";
    public static final String PASSWORD_1 = "password";

    @Autowired
    private UserRepository userRepository;

    @Order(1)
    @Test
    void registerUser_successfulRegistration() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername(USERNAME_1);
        registerDto.setEmail(EMAIL_1);
        registerDto.setFullName(NAME_1);
        registerDto.setPassword(PASSWORD_1);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

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
    }

    @Order(2)
    @Test
    void registerUser_errorUsernameAlreadyExists() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername(USERNAME_1);
        registerDto.setEmail(EMAIL_1);
        registerDto.setFullName(NAME_1);
        registerDto.setPassword(PASSWORD_1);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(registerResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Username is already in use", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.CONFLICT, errorDto.getStatus());
    }

    @Order(3)
    @Test
    void registerUser_errorEmailAlreadyExists() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername(USERNAME_1 + "Mock");
        registerDto.setEmail(EMAIL_1);
        registerDto.setFullName(NAME_1);
        registerDto.setPassword(PASSWORD_1);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(registerResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Email is already in use", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.CONFLICT, errorDto.getStatus());
    }

    @Order(4)
    @Test
    void loginUserAndValidate_successfulLogin() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(USERNAME_1);
        loginDto.setPassword(PASSWORD_1);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andReturn();

        String jwt = loginResult.getResponse().getContentAsString();

        MvcResult validateResult = mockMvc.perform(get("/api/v1/auth/validate?token=" + jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        TokenDto tokenDto = objectMapper.readValue(validateResult.getResponse().getContentAsString(), TokenDto.class);

        Assertions.assertEquals(1, tokenDto.getUserId());
        Assertions.assertEquals(USERNAME_1, tokenDto.getUsername());
        Assertions.assertEquals(1, tokenDto.getAuthorities().size());

        Optional<SimpleGrantedAuthority> authority = tokenDto.getAuthorities().stream().findFirst();
        Assertions.assertTrue(authority.isPresent());
        Assertions.assertEquals("USER", authority.get().getAuthority());
    }

    @Order(5)
    @Test
    void loginUserAndValidate_wrongPassword() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(USERNAME_1);
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
        loginDto.setUsername(USERNAME_1 + "Mock");
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

        Optional<User> optionalUser = userRepository.findById(requestResponseDto.getResourceId());
        Assertions.assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();
        Assertions.assertEquals(user.getPasswordResetToken().getToken(), recoveryToken);

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

        Assertions.assertNotEquals(updatedUser.getPassword(), user.getPassword());
        Assertions.assertNull(updatedUser.getPasswordResetToken());
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