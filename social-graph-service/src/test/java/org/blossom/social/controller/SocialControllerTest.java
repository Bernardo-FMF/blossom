package org.blossom.social.controller;

import org.blossom.social.AbstractContextBeans;
import org.blossom.social.dto.GenericResponseDto;
import org.blossom.social.dto.SocialRelationDto;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.repository.SocialRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SocialControllerTest extends AbstractContextBeans {
    @Autowired
    private SocialRepository socialRepository;

    @Order(1)
    @Test
    void createUsers() {
        for (int i = 1; i < 5; i++) {
            GraphUser user = GraphUser.builder().userId(i).build();

            socialRepository.save(user);
        }
    }

    @Order(2)
    @Test
    void followUser_successful() throws Exception {
        SocialRelationDto socialRelationDto = new SocialRelationDto();
        socialRelationDto.setReceivingUser(2);

        MvcResult postResult = mockMvc.perform(post("/api/v1/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialRelationDto))
                        .header("username", "firstUser")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(postResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Follow was created successfully", genericResponseDto.getResponseMessage());
        Assertions.assertEquals(1, genericResponseDto.getResourceId());

        Optional<GraphUser> optionalGraphUser = socialRepository.findById(1);
        Assertions.assertTrue(optionalGraphUser.isPresent());

        GraphUser graphUser = optionalGraphUser.get();
        Assertions.assertEquals(1, graphUser.getFollowing().size());
        Assertions.assertEquals(2, graphUser.getFollowing().stream().findFirst().get().getUserId());
    }
}