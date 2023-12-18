package org.blossom.auth.controller;

import org.blossom.auth.AbstractContextBeans;
import org.blossom.auth.dto.*;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.model.ErrorMessage;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.service.AuthService;
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
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Order(1)
    @Test
    void registerUser_successfulRegistration() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("johnDoe01");
        registerDto.setEmail("john.doe@test.pt");
        registerDto.setFullName("John Doe");
        registerDto.setPassword("password");

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
        Assertions.assertEquals("johnDoe01", user.getUsername());
        Assertions.assertEquals("john.doe@test.pt", user.getEmail());
        Assertions.assertEquals("John Doe", user.getFullName());
    }

    @Order(2)
    @Test
    void registerUser_errorUsernameAlreadyExists() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("johnDoe01");
        registerDto.setEmail("john.doe@test.pt");
        registerDto.setFullName("John Doe");
        registerDto.setPassword("password");

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
        registerDto.setUsername("johnDoe02");
        registerDto.setEmail("john.doe@test.pt");
        registerDto.setFullName("John Doe");
        registerDto.setPassword("password");

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
        loginDto.setUsername("johnDoe01");
        loginDto.setPassword("password");

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
        Assertions.assertEquals("johnDoe01", tokenDto.getUsername());
        Assertions.assertEquals(1, tokenDto.getAuthorities().size());

        Optional<SimpleGrantedAuthority> authority = tokenDto.getAuthorities().stream().findFirst();
        Assertions.assertTrue(authority.isPresent());
        Assertions.assertEquals("USER", authority.get().getAuthority());
    }

    @Order(5)
    @Test
    void loginUserAndValidate_wrongPassword() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("johnDoe01");
        loginDto.setPassword("password1");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(loginResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Bad credentials", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorDto.getStatus());
    }

    @Order(6)
    @Test
    void loginUserAndValidate_userDoesNotExist() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("johnDoe02");
        loginDto.setPassword("password");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(loginResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("Bad credentials", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorDto.getStatus());
    }

    @Order(7)
    @Test
    void recoverPassword_recoverSuccessful() throws Exception {
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setEmail("john.doe@test.pt");

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
        Assertions.assertEquals("johnDoe01", userDto.getUsername());
        Assertions.assertEquals("john.doe@test.pt", userDto.getEmail());

        String recoveryToken = userDto.getToken();

        Optional<User> optionalUser = userRepository.findById(requestResponseDto.getResourceId());
        Assertions.assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();
        Assertions.assertEquals(user.getPasswordResetToken().getToken(), recoveryToken);

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setUserId(requestResponseDto.getResourceId());
        passwordChangeDto.setToken(recoveryToken);
        passwordChangeDto.setNewPassword("password1");

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
        Assertions.assertEquals("johnDoe01", userDto.getUsername());
        Assertions.assertEquals("john.doe@test.pt", userDto.getEmail());

        Assertions.assertNotEquals(updatedUser.getPassword(), user.getPassword());
        Assertions.assertNull(updatedUser.getPasswordResetToken());
    }
}