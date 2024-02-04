package org.blossom.notification.kafka;

import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.model.EventType;
import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.blossom.notification.AbstractContextBeans;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.repository.FollowNotificationRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

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
public class SocialFollowKafkaMessageIntegrationTest extends AbstractContextBeans {
    @Autowired
    private FollowNotificationRepository followNotificationRepository;

    private KafkaFutureExecutor kafkaFutureExecutor;

    @BeforeEach
    void setup() {
        Map<ResourceType, List<String>> topicMap = new HashMap<>();
        topicMap.put(ResourceType.SOCIAL_FOLLOW, List.of("social-follow-resource-event-notification"));
        kafkaFutureExecutor = new KafkaFutureExecutor(kafkaTemplate, topicMap);

        UserDto userDto1 = UserDto.builder()
                .userId(1)
                .username("user1")
                .fullName("user 1")
                .imageUrl(null)
                .build();

        UserDto userDto2 = UserDto.builder()
                .userId(2)
                .username("user2")
                .fullName("user 2")
                .imageUrl(null)
                .build();

        ResponseEntity<UserDto> responseUser1 = ResponseEntity.of(Optional.of(userDto1));
        ResponseEntity<UserDto> responseUser2 = ResponseEntity.of(Optional.of(userDto2));

        Mockito.when(authClient.getUser(1)).thenReturn(responseUser1);
        Mockito.when(authClient.getUser(2)).thenReturn(responseUser2);
    }

    @Order(1)
    @Test
    void handleSocialFollowCreationKafkaMessage() throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaSocialFollowResource resource = KafkaSocialFollowResource.builder()
                .initiatingUser(1)
                .receivingUser(2)
                .createdAt(Instant.now())
                .isMutualFollow(true)
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.SOCIAL_FOLLOW, resource);

        Runnable validationCallback = () -> {
            List<FollowNotification> followNotifications = followNotificationRepository.findByRecipientIdAndIsDeliveredFalse(2, PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "followedAt"))).getContent();

            Assertions.assertEquals(1, followNotifications.size());

            FollowNotification notification = followNotifications.get(0);
            Assertions.assertEquals(2, notification.getRecipientId());
            Assertions.assertEquals(1, notification.getSenderId());

            countDownLatch.countDown();
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

}
