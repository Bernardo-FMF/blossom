package org.blossom.feed.kafka;

import org.blossom.feed.AbstractContextBeans;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.entity.LocalUserPostCount;
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
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostKafkaMessageIntegrationTest extends AbstractContextBeans {
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

    private KafkaFutureExecutor kafkaFutureExecutor;

    @BeforeEach
    void setUp() {
        Map<ResourceType, List<String>> topicMap = new HashMap<>();
        topicMap.put(ResourceType.USER, List.of("user-resource-event-feed"));
        kafkaFutureExecutor = new KafkaFutureExecutor(kafkaTemplate, topicMap);

        Mockito.when(grpcClientSocialService.getUserFollowers(1)).thenReturn(List.of(2, 3));
        Mockito.when(grpcClientSocialService.getUserFollowers(2)).thenReturn(List.of(1));
        Mockito.when(grpcClientSocialService.getUserFollowers(3)).thenReturn(List.of(1, 2));
    }

    @Order(1)
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
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

    @Order(2)
    @Test
    void handlePostCreationKafkaMessage() throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaPostResource resource = KafkaPostResource.builder()
                .id("postId1")
                .userId(1)
                .media(new String[] {})
                .hashtags(new String[] {})
                .description("this is post 1")
                .createdAt(Instant.now())
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.POST, resource);

        Runnable validationCallback = () -> {
            Optional<LocalUserPostCount> optionalLocalUserPostCount = localUserPostCountRepository.findById(1);
            Assertions.assertTrue(optionalLocalUserPostCount.isPresent());

            LocalUserPostCount localUserPostCount = optionalLocalUserPostCount.get();

            Assertions.assertEquals(1, localUserPostCount.getUserId());
            Assertions.assertEquals(1, localUserPostCount.getPostCount());

            List<LocalPostByUser> postIdsByUser = localPostByUserRepository.findByKeyUserIdIn(List.of(1));
            Assertions.assertFalse(postIdsByUser.isEmpty());
            Assertions.assertEquals("postId1", postIdsByUser.get(0).getPostId());

            long count2 = feedEntryRepository.countByKeyUserId(2);
            Assertions.assertEquals(1, count2);

            List<FeedEntry> feedEntries2 = feedEntryRepository.findByKeyUserId(2, PageRequest.of(0, 10)).getContent();
            Assertions.assertEquals(1, feedEntries2.size());
            Assertions.assertEquals("postId1", feedEntries2.get(0).getPostId());

            long count3 = feedEntryRepository.countByKeyUserId(3);
            Assertions.assertEquals(1, count3);

            List<FeedEntry> feedEntries3 = feedEntryRepository.findByKeyUserId(3, PageRequest.of(0, 10)).getContent();
            Assertions.assertEquals(1, feedEntries3.size());
            Assertions.assertEquals("postId1", feedEntries3.get(0).getPostId());
            countDownLatch.countDown();
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    @Order(3)
    @Test
    void handlePostDeleteKafkaMessage() throws InterruptedException, ExecutionException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaPostResource resource = KafkaPostResource.builder()
                .id("postId1")
                .userId(1)
                .media(new String[] {})
                .description("this is post 1")
                .createdAt(Instant.now())
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.DELETE, ResourceType.POST, resource);

        Consumer<Throwable> errorCallback = Assertions::fail;

        Runnable validationCallback = () -> {
            Optional<LocalUserPostCount> optionalLocalUserPostCount = localUserPostCountRepository.findById(1);
            Assertions.assertTrue(optionalLocalUserPostCount.isPresent());

            LocalUserPostCount localUserPostCount = optionalLocalUserPostCount.get();

            Assertions.assertEquals(1, localUserPostCount.getUserId());
            Assertions.assertEquals(0, localUserPostCount.getPostCount());

            List<LocalPostByUser> postIdsByUser = localPostByUserRepository.findByKeyUserIdIn(List.of(1));
            Assertions.assertTrue(postIdsByUser.isEmpty());

            long count2 = feedEntryRepository.countByKeyUserId(2);
            Assertions.assertEquals(0, count2);

            List<FeedEntry> feedEntries2 = feedEntryRepository.findByKeyUserId(2, PageRequest.of(0, 10)).getContent();
            Assertions.assertEquals(0, feedEntries2.size());

            long count3 = feedEntryRepository.countByKeyUserId(3);
            Assertions.assertEquals(0, count3);

            List<FeedEntry> feedEntries3 = feedEntryRepository.findByKeyUserId(3, PageRequest.of(0, 10)).getContent();
            Assertions.assertEquals(0, feedEntries3.size());
            countDownLatch.countDown();
        };

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }
}
