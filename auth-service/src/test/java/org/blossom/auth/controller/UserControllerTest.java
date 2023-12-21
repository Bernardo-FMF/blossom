package org.blossom.auth.controller;

import org.blossom.auth.CommonRequestHelper;
import org.blossom.auth.dto.GenericResponseDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.grpc.GrpcClientImageService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends CommonRequestHelper {
    private static final String USERNAME_1 = "johnDoe01";
    private static final String EMAIL_1 = "john.doe@test.pt";
    private static final String NAME_1 = "John Doe";
    private static final String PASSWORD_1 = "password";
    private static final String MOCK_IMAGE_URL = "mockUrl";

    @MockBean
    private GrpcClientImageService imageService;

    @Order(1)
    @Test
    void registerUsers() throws Exception {
        MvcResult registerUser = registerUser(USERNAME_1, EMAIL_1, NAME_1, PASSWORD_1, MockMvcResultMatchers.status().isCreated());

        GenericResponseDto responseDto = objectMapper.readValue(registerUser.getResponse().getContentAsString(), GenericResponseDto.class);
        Assertions.assertEquals(1, responseDto.getResourceId());
        Assertions.assertEquals("User registered successfully", responseDto.getResponseMessage());
    }

    @Order(2)
    @Test
    void changeProfileImage_successful() throws Exception {
        Mockito.when(imageService.uploadImage(any())).thenReturn(MOCK_IMAGE_URL);

        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        MvcResult validateResult = mockMvc.perform(multipart("/api/v1/user/profile-image")
                        .file(file)
                        .header("username", USERNAME_1)
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto responseDto = objectMapper.readValue(validateResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals(1, responseDto.getResourceId());
        Assertions.assertEquals("Image changed successfully", responseDto.getResponseMessage());
        Assertions.assertEquals(1, responseDto.getMetadata().size());
        Assertions.assertEquals(MOCK_IMAGE_URL, responseDto.getMetadata().get("url"));

        Optional<User> optionalUser = userRepository.findById(responseDto.getResourceId());
        Assertions.assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();

        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals(MOCK_IMAGE_URL, user.getImageUrl());
    }

    @Order(3)
    @Test
    void getUserById_userExists() throws Exception {
        MvcResult validateResult = mockMvc.perform(get("/api/v1/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        SimplifiedUserDto simplifiedUserDto = objectMapper.readValue(validateResult.getResponse().getContentAsString(), SimplifiedUserDto.class);

        Assertions.assertEquals(1, simplifiedUserDto.getId());
        Assertions.assertEquals(MOCK_IMAGE_URL, simplifiedUserDto.getImageUrl());
        Assertions.assertEquals(USERNAME_1, simplifiedUserDto.getUsername());
        Assertions.assertEquals(NAME_1, simplifiedUserDto.getFullName());
    }
}
