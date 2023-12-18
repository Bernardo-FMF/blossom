package org.blossom.auth.controller;

import org.blossom.auth.AbstractContextBeans;
import org.blossom.auth.dto.GenericResponseDto;
import org.blossom.auth.dto.LoginDto;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.model.ErrorMessage;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.service.AuthService;
import org.blossom.model.dto.TokenDto;
import org.junit.jupiter.api.*;
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
}