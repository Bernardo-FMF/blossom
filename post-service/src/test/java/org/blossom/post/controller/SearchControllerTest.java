package org.blossom.post.controller;

import org.blossom.post.AbstractContextBeans;
import org.blossom.post.client.UserClient;
import org.blossom.post.dto.AggregatePostsDto;
import org.blossom.post.dto.PostInfoDto;
import org.blossom.post.dto.UserDto;
import org.blossom.post.entity.Post;
import org.blossom.post.factory.impl.PostFactory;
import org.blossom.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchControllerTest extends AbstractContextBeans {
    private static final String MOCK_IMAGE_URL = "mockUrl";

    private static final List<String> ids = new ArrayList<>();

    @MockBean
    private UserClient userClient;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostFactory postFactory;

    @BeforeEach
    void createUsers() {
        UserDto user1 = UserDto.builder()
                .id(1)
                .username("firstUser")
                .fullName("")
                .imageUrl(MOCK_IMAGE_URL)
                .build();

        ResponseEntity<UserDto> responseEntity = ResponseEntity.status(HttpStatus.OK).body(user1);
        Mockito.when(userClient.getUserById(anyInt())).thenReturn(responseEntity);
    }

    @Order(1)
    @Test
    void createPosts() {
        PostInfoDto postInfoDto1 = PostInfoDto.builder()
                .userId(1)
                .text("This is #xpto1")
                .hashtags(new String[] {"xpto1"})
                .build();

        PostInfoDto postInfoDto2 = PostInfoDto.builder()
                .userId(1)
                .text("This is #xpto1")
                .hashtags(new String[] {"xpto1"})
                .build();

        PostInfoDto postInfoDto3 = PostInfoDto.builder()
                .userId(1)
                .text("This is #foo1")
                .hashtags(new String[] {"foo1"})
                .build();

        List<Post> posts = postRepository.saveAll(List.of(postFactory.buildEntity(postInfoDto1), postFactory.buildEntity(postInfoDto2), postFactory.buildEntity(postInfoDto3)));
        for (Post post: posts) {
            ids.add(post.getId());
        }
    }

    @Order(2)
    @Test
    void searchPosts_byHashtag() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/post-search/simple-hashtag-lookup")
                        .queryParam("pageLimit", "100")
                        .queryParam("page", "0")
                        .queryParam("query", "xpto1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AggregatePostsDto aggregatePostsDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), AggregatePostsDto.class);

        Assertions.assertEquals(2, aggregatePostsDto.getPosts().size());

        Assertions.assertEquals(2, aggregatePostsDto.getPaginationInfo().getTotalElements());
        Assertions.assertEquals(0, aggregatePostsDto.getPaginationInfo().getCurrentPage());
        Assertions.assertEquals(1, aggregatePostsDto.getPaginationInfo().getTotalPages());
        Assertions.assertTrue(aggregatePostsDto.getPaginationInfo().isEof());
    }
}