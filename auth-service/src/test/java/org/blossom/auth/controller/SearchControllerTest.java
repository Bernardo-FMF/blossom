package org.blossom.auth.controller;

import org.blossom.auth.CommonRequestHelper;
import org.blossom.auth.dto.GenericResponseDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.dto.UsersDto;
import org.blossom.auth.exception.model.ErrorMessage;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchControllerTest extends CommonRequestHelper {
    private static final String USERNAME_1 = "default";
    private static final String EMAIL_1 = "default@test.pt";
    private static final String NAME_1 = "Default";
    private static final String PASSWORD_1 = "password";

    private static final HashMap<String, Set<Integer>> patternMap = new HashMap<>();

    @Order(1)
    @Test
    void registerUsers() throws Exception {
        MvcResult registerUser1 = registerUser(USERNAME_1 + "xpto", "a" + EMAIL_1, NAME_1 + "foo", PASSWORD_1, MockMvcResultMatchers.status().isCreated());
        MvcResult registerUser2 = registerUser(USERNAME_1 + "xyz", "b" + EMAIL_1, NAME_1 + "xpto", PASSWORD_1, MockMvcResultMatchers.status().isCreated());
        MvcResult registerUser3 = registerUser(USERNAME_1 + "dfg", "c" + EMAIL_1, NAME_1 + "xyz", PASSWORD_1, MockMvcResultMatchers.status().isCreated());
        MvcResult registerUser4 = registerUser(USERNAME_1 + "foo", "d" + EMAIL_1, NAME_1 + "dfg", PASSWORD_1, MockMvcResultMatchers.status().isCreated());
        MvcResult registerUser5 = registerUser(USERNAME_1 + "abc", "e" + EMAIL_1, NAME_1 + "abc", PASSWORD_1, MockMvcResultMatchers.status().isCreated());

        GenericResponseDto responseDto1 = objectMapper.readValue(registerUser1.getResponse().getContentAsString(), GenericResponseDto.class);
        GenericResponseDto responseDto2 = objectMapper.readValue(registerUser2.getResponse().getContentAsString(), GenericResponseDto.class);
        GenericResponseDto responseDto3 = objectMapper.readValue(registerUser3.getResponse().getContentAsString(), GenericResponseDto.class);
        GenericResponseDto responseDto4 = objectMapper.readValue(registerUser4.getResponse().getContentAsString(), GenericResponseDto.class);
        GenericResponseDto responseDto5 = objectMapper.readValue(registerUser5.getResponse().getContentAsString(), GenericResponseDto.class);

        patternMap.put("xpto", Set.of(responseDto1.getResourceId(), responseDto2.getResourceId()));
        patternMap.put("foo", Set.of(responseDto1.getResourceId(), responseDto4.getResourceId()));
        patternMap.put("xyz", Set.of(responseDto2.getResourceId(), responseDto3.getResourceId()));
        patternMap.put("abc", Set.of(responseDto5.getResourceId()));
    }

    @Order(2)
    @Test
    void searchSpecificUser_userExists() throws Exception {
        MvcResult searchResult = mockMvc.perform(get("/api/v1/user-search/username-lookup?username=" + USERNAME_1 + "xpto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        SimplifiedUserDto simplifiedUserDto = objectMapper.readValue(searchResult.getResponse().getContentAsString(), SimplifiedUserDto.class);
        Assertions.assertEquals(1, simplifiedUserDto.getId());
        Assertions.assertEquals(USERNAME_1 + "xpto", simplifiedUserDto.getUsername());
        Assertions.assertEquals(NAME_1 + "foo", simplifiedUserDto.getFullName());
    }

    @Order(3)
    @Test
    void searchSpecificUser_userDoesNotExist() throws Exception {
        MvcResult searchResult = mockMvc.perform(get("/api/v1/user-search/username-lookup?username=" + USERNAME_1 + "xpto2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorMessage errorDto = objectMapper.readValue(searchResult.getResponse().getContentAsString(), ErrorMessage.class);
        Assertions.assertEquals("User not found", errorDto.getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, errorDto.getStatus());
    }

    @Order(4)
    @Test
    void searchUserByPattern_usersExist() throws Exception {
        searchUserByPattern("xpto");
        searchUserByPattern("xyz");
        searchUserByPattern("foo");
        searchUserByPattern("abc");
    }

    private void searchUserByPattern(String pattern) throws Exception {
        MvcResult searchResult = mockMvc.perform(get("/api/v1/user-search/simple-lookup?pageLimit=100&page=0&contains=" + pattern)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UsersDto usersDto = objectMapper.readValue(searchResult.getResponse().getContentAsString(), UsersDto.class);
        Assertions.assertEquals(patternMap.get(pattern).size(), usersDto.getUsers().size());
        Assertions.assertEquals(patternMap.get(pattern), usersDto.getUsers().stream().map(SimplifiedUserDto::getId).collect(Collectors.toSet()));
        Assertions.assertEquals(patternMap.get(pattern).size(), usersDto.getPaginationInfo().getTotalElements());
        Assertions.assertEquals(0, usersDto.getPaginationInfo().getCurrentPage());
        Assertions.assertEquals(1, usersDto.getPaginationInfo().getTotalPages());
        Assertions.assertTrue(usersDto.getPaginationInfo().isEof());
    }
}
