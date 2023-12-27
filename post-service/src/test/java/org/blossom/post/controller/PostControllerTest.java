package org.blossom.post.controller;

import org.blossom.post.AbstractContextBeans;
import org.blossom.post.client.UserClient;
import org.blossom.post.dto.*;
import org.blossom.post.entity.Post;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostControllerTest extends AbstractContextBeans {
    private static final String MOCK_IMAGE_URL = "mockUrl";

    private static final List<String> ids = new ArrayList<>();

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private UserClient userClient;

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
    void createPost_withImage() throws Exception {
        Mockito.when(imageService.uploadImages(any())).thenReturn(new String[]{MOCK_IMAGE_URL});

        MockMultipartFile file = new MockMultipartFile("mediaFiles", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        MvcResult postResult = mockMvc.perform(multipart("/api/v1/post")
                        .file(file)
                        .header("username", "firstUser")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(postResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Post created successfully", genericResponseDto.getResponseMessage());

        Optional<Post> post = postRepository.findById(genericResponseDto.getResourceId());

        Assertions.assertTrue(post.isPresent());
        Assertions.assertEquals(1, post.get().getUserId());
        Assertions.assertEquals(1, post.get().getMedia().length);
        Assertions.assertEquals(MOCK_IMAGE_URL, post.get().getMedia()[0]);

        ids.add(genericResponseDto.getResourceId());
    }

    @Order(2)
    @Test
    void createPost_withText() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", "this is a #test");

        MvcResult postResult = mockMvc.perform(multipart("/api/v1/post")
                        .params(params)
                        .header("username", "firstUser")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(postResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Post created successfully", genericResponseDto.getResponseMessage());

        Optional<Post> post = postRepository.findById(genericResponseDto.getResourceId());

        Assertions.assertTrue(post.isPresent());
        Assertions.assertEquals(1, post.get().getUserId());
        Assertions.assertEquals("this is a #test", post.get().getDescription());
        Assertions.assertEquals(1, post.get().getHashtags().length);
        Assertions.assertEquals("test", post.get().getHashtags()[0]);
        Assertions.assertEquals(0, post.get().getMedia().length);

        ids.add(genericResponseDto.getResourceId());
    }

    @Order(3)
    @Test
    void createPost_withImageAndText() throws Exception {
        Mockito.when(imageService.uploadImages(any())).thenReturn(new String[]{MOCK_IMAGE_URL});

        MockMultipartFile file = new MockMultipartFile("mediaFiles", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", "this is a #test");

        MvcResult postResult = mockMvc.perform(multipart("/api/v1/post")
                        .file(file)
                        .params(params)
                        .header("username", "firstUser")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(postResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Post created successfully", genericResponseDto.getResponseMessage());

        Optional<Post> post = postRepository.findById(genericResponseDto.getResourceId());

        Assertions.assertTrue(post.isPresent());
        Assertions.assertEquals(1, post.get().getUserId());
        Assertions.assertEquals("this is a #test", post.get().getDescription());
        Assertions.assertEquals(1, post.get().getHashtags().length);
        Assertions.assertEquals("test", post.get().getHashtags()[0]);
        Assertions.assertEquals(1, post.get().getMedia().length);
        Assertions.assertEquals(MOCK_IMAGE_URL, post.get().getMedia()[0]);

        ids.add(genericResponseDto.getResourceId());
    }

    @Order(4)
    @Test
    void getUserPosts_success() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/post/user/1")
                        .queryParam("pageLimit", "100")
                        .queryParam("page", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AggregateUserPostsDto aggregateUserPostsDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), AggregateUserPostsDto.class);

        List<PostDto> posts = aggregateUserPostsDto.getPosts();
        Assertions.assertEquals(ids.size(), posts.size());

        // Guarantee the posts are in descending order in terms of creation
        for (int i = 0; i < ids.size(); i++) {
            Assertions.assertEquals(ids.get(i), posts.get(posts.size() - i - 1).getId());
        }

        Assertions.assertEquals(1, aggregateUserPostsDto.getUserId());

        Assertions.assertEquals(ids.size(), aggregateUserPostsDto.getPaginationInfo().getTotalElements());
        Assertions.assertEquals(0, aggregateUserPostsDto.getPaginationInfo().getCurrentPage());
        Assertions.assertEquals(1, aggregateUserPostsDto.getPaginationInfo().getTotalPages());
        Assertions.assertTrue(aggregateUserPostsDto.getPaginationInfo().isEof());
    }

    @Order(5)
    @Test
    void getPostIdentifier_success() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/post/" + ids.get(0) + "/identifier"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostIdentifierDto postIdentifierDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), PostIdentifierDto.class);

        Assertions.assertEquals(ids.get(0), postIdentifierDto.getPostId());
        Assertions.assertEquals(1, postIdentifierDto.getUserId());
    }

    @Order(6)
    @Test
    void getPost_success() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/post/" + ids.get(0)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostWithUserDto postWithUserDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), PostWithUserDto.class);

        Assertions.assertEquals(ids.get(0), postWithUserDto.getId());
        Assertions.assertEquals(1, postWithUserDto.getUser().getId());
        Assertions.assertEquals("firstUser", postWithUserDto.getUser().getUsername());
    }

    @Order(7)
    @Test
    void deletePost_success() throws Exception {
        MvcResult deleteResult = mockMvc.perform(delete("/api/v1/post/" + ids.get(0))
                        .header("username", "firstUser")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(deleteResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Post deleted successfully", genericResponseDto.getResponseMessage());
        Assertions.assertEquals(ids.get(0), genericResponseDto.getResourceId());

        Optional<Post> post = postRepository.findById(ids.get(0));

        Assertions.assertFalse(post.isPresent());
    }
}