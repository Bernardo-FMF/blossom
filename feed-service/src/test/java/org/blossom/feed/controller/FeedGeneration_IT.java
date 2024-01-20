package org.blossom.feed.controller;

import org.blossom.feed.AbstractContextBeans;
import org.blossom.feed.dto.FeedDto;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.grpc.service.GrpcClientActivityService;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostByUserRepository;
import org.blossom.feed.repository.LocalUserPostCountRepository;
import org.blossom.feed.repository.LocalUserRepository;
import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeedGeneration_IT extends AbstractContextBeans {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    @Autowired
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @MockBean
    private GrpcClientSocialService grpcClientSocialService;

    @MockBean
    private GrpcClientActivityService grpcClientActivityService;

    private KafkaFutureExecutor kafkaFutureExecutor;

    private final Map<Integer, Integer> postUserMap = new HashMap<>();

    @BeforeEach
    void setUp() throws InterruptedException {
        kafkaFutureExecutor = new KafkaFutureExecutor(kafkaTemplate, List.of("user-resource-event-feed"));

        postUserMap.put(1, 1);
        postUserMap.put(2, 1);
        postUserMap.put(3, 1);
        postUserMap.put(4, 1);
        postUserMap.put(5, 5);
        postUserMap.put(6, 5);
        postUserMap.put(7, 5);
        postUserMap.put(8, 2);
        postUserMap.put(9, 2);
        postUserMap.put(10, 3);

        Mockito.when(grpcClientSocialService.getUserFollowers(1)).thenReturn(List.of(2, 3, 4, 5));
        Mockito.when(grpcClientSocialService.getUserFollowers(2)).thenReturn(List.of(1));
        Mockito.when(grpcClientSocialService.getUserFollowers(3)).thenReturn(List.of(1, 2));
        Mockito.when(grpcClientSocialService.getUserFollowers(4)).thenReturn(List.of(1));
        Mockito.when(grpcClientSocialService.getUserFollowers(5)).thenReturn(List.of(1, 2, 3, 4));

        Map<String, MetadataDto> metadataDtoMap = new HashMap<>();
        metadataDtoMap.put("postId1", null);
        metadataDtoMap.put("postId2", null);
        metadataDtoMap.put("postId3", null);

        Mockito.when(grpcClientActivityService.getMetadata(1, List.of("postId1", "postId2", "postId3"))).thenReturn(metadataDtoMap);
        Mockito.when(grpcClientActivityService.getMetadata(null, List.of("postId1", "postId2", "postId3"))).thenReturn(metadataDtoMap);
    }

    @Order(1)
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void handleUserCreationKafkaMessage(int id) throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaUserResource resource = KafkaUserResource.builder()
                .id(id)
                .username("user" + id)
                .fullName("user " + id)
                .imageUrl("imgUrl" + id)
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.USER, resource);

        Runnable validationCallback = () -> {
            Optional<LocalUser> optionalLocalUser = localUserRepository.findById(id);
            Assertions.assertTrue(optionalLocalUser.isPresent());

            LocalUser localUser = optionalLocalUser.get();

            Assertions.assertEquals(id, localUser.getId());
            Assertions.assertEquals("user" + id, localUser.getUsername());
            countDownLatch.countDown();
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    //create posts
    @Order(2)
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void handlePostCreationKafkaMessage(int id) throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Integer userId = postUserMap.get(id);

        KafkaPostResource resource = KafkaPostResource.builder()
                .id("postId" + id)
                .userId(userId)
                .media(new String[] {})
                .description("this is post " + id)
                .createdAt(Instant.now().plusSeconds(id))
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.POST, resource);

        Runnable validationCallback = countDownLatch::countDown;

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    //get user feed
    @Order(3)
    @Test
    void getUserFeed() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/feed")
                        .queryParam("pageLimit", "100")
                        .queryParam("page", "0")
                        .header("username", "user1")
                        .header("userId", "1")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        FeedDto feedDto = objectMapper.readValue(getResult.getResponse().getContentAsString(), FeedDto.class);
    }

    @Order(4)
    @Test
    void getGenericUserFeed() {

    }
}