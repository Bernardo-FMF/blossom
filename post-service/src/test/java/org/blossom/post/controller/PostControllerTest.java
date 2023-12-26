package org.blossom.post.controller;

import org.blossom.model.EventType;
import org.blossom.model.KafkaUserResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.blossom.post.AbstractContextBeans;
import org.blossom.post.dto.*;
import org.blossom.post.entity.Post;
import org.blossom.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostControllerTest extends AbstractContextBeans {
    private static final String MOCK_IMAGE_URL = "mockUrl";

    private static final List<String> IDS = new ArrayList<>();

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Autowired
    private PostRepository postRepository;

    @Order(1)
    @Test
    void createUsers() throws InterruptedException {
        // Wait for kafka to stabilize
        Thread.sleep(5000);

        ResourceEvent resourceEvent1 = createUserMessage(1, "firstUser");
        ResourceEvent resourceEvent2 = createUserMessage(2, "secondUser");


        kafkaTemplate.send("user-resource-event-post", resourceEvent1);
        kafkaTemplate.send("user-resource-event-post", resourceEvent2);

        // Wait for kafka messages to be processed
        Thread.sleep(5000);
    }

    @Order(2)
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

        IDS.add(genericResponseDto.getResourceId());
    }

    @Order(3)
    @Test
    void createPost_withText() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", "value1");

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
        Assertions.assertEquals("value1", post.get().getDescription());
        Assertions.assertEquals(0, post.get().getMedia().length);

        IDS.add(genericResponseDto.getResourceId());
    }

    @Order(4)
    @Test
    void createPost_withImageAndText() throws Exception {
        Mockito.when(imageService.uploadImages(any())).thenReturn(new String[]{MOCK_IMAGE_URL});

        MockMultipartFile file = new MockMultipartFile("mediaFiles", "filename.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", "value1");

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
        Assertions.assertEquals("value1", post.get().getDescription());
        Assertions.assertEquals(1, post.get().getMedia().length);
        Assertions.assertEquals(MOCK_IMAGE_URL, post.get().getMedia()[0]);

        IDS.add(genericResponseDto.getResourceId());
    }

    @Order(5)
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
        Assertions.assertEquals(IDS.size(), posts.size());

        // Guarantee the posts are in descending order in terms of creation
        for (int i = 0; i < IDS.size(); i++) {
            Assertions.assertEquals(IDS.get(i), posts.get(posts.size() - i - 1).getId());
        }

        Assertions.assertEquals(1, aggregateUserPostsDto.getUserId());

        Assertions.assertEquals(IDS.size(), aggregateUserPostsDto.getPaginationInfo().getTotalElements());
        Assertions.assertEquals(0, aggregateUserPostsDto.getPaginationInfo().getCurrentPage());
        Assertions.assertEquals(1, aggregateUserPostsDto.getPaginationInfo().getTotalPages());
        Assertions.assertTrue(aggregateUserPostsDto.getPaginationInfo().isEof());
    }

    @Order(6)
    @Test
    void getPostIdentifier_success() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/post/" + IDS.get(0) + "/identifier"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostIdentifierDto postIdentifierDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), PostIdentifierDto.class);

        Assertions.assertEquals(IDS.get(0), postIdentifierDto.getPostId());
        Assertions.assertEquals(1, postIdentifierDto.getUserId());
    }

    @Order(7)
    @Test
    void getPost_success() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/post/" + IDS.get(0)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostWithUserDto postWithUserDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), PostWithUserDto.class);

        Assertions.assertEquals(IDS.get(0), postWithUserDto.getId());
        Assertions.assertEquals(1, postWithUserDto.getUser().getId());
        Assertions.assertEquals("firstUser", postWithUserDto.getUser().getUsername());
    }

    @Order(8)
    @Test
    void deletePost_success() throws Exception {
        MvcResult deleteResult = mockMvc.perform(delete("/api/v1/post/" + IDS.get(0))
                        .header("username", "firstUser")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(deleteResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Post deleted successfully", genericResponseDto.getResponseMessage());
        Assertions.assertEquals(IDS.get(0), genericResponseDto.getResourceId());

        Optional<Post> post = postRepository.findById(IDS.get(0));

        Assertions.assertFalse(post.isPresent());
    }

    private static ResourceEvent createUserMessage(int id, String username) {
        KafkaUserResource resource = KafkaUserResource.builder()
                .id(id)
                .username(username)
                .fullName(username + " " + id)
                .imageUrl("image-url")
                .build();
        return new ResourceEvent(EventType.CREATE, ResourceType.USER, resource);
    }
}