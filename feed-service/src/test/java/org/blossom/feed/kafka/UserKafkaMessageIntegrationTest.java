package org.blossom.feed.kafka;

import org.blossom.feed.AbstractContextBeans;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.entity.LocalUserPostCount;
import org.blossom.feed.repository.LocalUserPostCountRepository;
import org.blossom.feed.repository.LocalUserRepository;
import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.model.EventType;
import org.blossom.model.KafkaUserResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserKafkaMessageIntegrationTest extends AbstractContextBeans {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    private KafkaFutureExecutor kafkaFutureExecutor;

    @BeforeEach
    void setUp() {
        kafkaFutureExecutor = new KafkaFutureExecutor(kafkaTemplate, List.of("user-resource-event-feed"));
    }

    @Order(1)
    @Test
    void handleUserCreationKafkaMessage() throws ExecutionException, InterruptedException {
        KafkaUserResource resource = KafkaUserResource.builder()
                .id(1)
                .username("user1")
                .fullName("user 1")
                .imageUrl("imgUrl")
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.USER, resource);

        Runnable validationCallback = () -> {
            Optional<LocalUser> optionalLocalUser = localUserRepository.findById(1);
            Optional<LocalUserPostCount> optionalLocalUserPostCount = localUserPostCountRepository.findById(1);
            Assertions.assertTrue(optionalLocalUser.isPresent());
            Assertions.assertTrue(optionalLocalUserPostCount.isPresent());

            LocalUser localUser = optionalLocalUser.get();
            LocalUserPostCount localUserPostCount = optionalLocalUserPostCount.get();

            Assertions.assertEquals(1, localUser.getId());
            Assertions.assertEquals("user1", localUser.getUsername());
            Assertions.assertEquals(1, localUserPostCount.getUserId());
            Assertions.assertEquals(0, localUserPostCount.getPostCount());
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));
    }

    @Order(2)
    @Test
    void handleUserUpdateKafkaMessage() throws ExecutionException, InterruptedException {
        KafkaUserResource resource = KafkaUserResource.builder()
                .id(1)
                .username("user1")
                .fullName("user 1")
                .imageUrl("newImgUrl")
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.UPDATE, ResourceType.USER, resource);

        Runnable validationCallback = () -> {
            Optional<LocalUser> optionalLocalUser = localUserRepository.findById(1);
            Assertions.assertTrue(optionalLocalUser.isPresent());

            LocalUser localUser = optionalLocalUser.get();

            Assertions.assertEquals(1, localUser.getId());
            Assertions.assertEquals("user1", localUser.getUsername());
            Assertions.assertEquals("newImgUrl", localUser.getImageUrl());
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));
    }

    @Order(3)
    @Test
    void handleUserDeleteKafkaMessage() throws ExecutionException, InterruptedException {
        KafkaUserResource resource = KafkaUserResource.builder()
                .id(1)
                .username("user1")
                .fullName("user 1")
                .imageUrl("newImgUrl")
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.DELETE, ResourceType.USER, resource);

        Runnable validationCallback = () -> {
            Optional<LocalUser> optionalLocalUser = localUserRepository.findById(1);
            Optional<LocalUserPostCount> optionalLocalUserPostCount = localUserPostCountRepository.findById(1);
            Assertions.assertFalse(optionalLocalUser.isPresent());
            Assertions.assertFalse(optionalLocalUserPostCount.isPresent());
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));
    }
}