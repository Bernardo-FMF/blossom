package org.blossom.notification.controller;

import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.model.EventType;
import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.blossom.notification.AbstractContextBeans;
import org.blossom.notification.dto.GenericResponseDto;
import org.blossom.notification.dto.NotificationFollowsDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.repository.FollowNotificationRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowControllerIntegrationTest extends AbstractContextBeans {
    @Autowired
    private FollowNotificationRepository followNotificationRepository;

    private KafkaFutureExecutor kafkaFutureExecutor;

    @BeforeEach
    void setup() {
        Map<ResourceType, List<String>> topicMap = new HashMap<>();
        topicMap.put(ResourceType.SOCIAL_FOLLOW, List.of("social-follow-resource-event-notification"));
        kafkaFutureExecutor = new KafkaFutureExecutor(kafkaTemplate, topicMap);

        UserDto userDto1 = UserDto.builder()
                .id(1)
                .username("user1")
                .fullName("user 1")
                .imageUrl(null)
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2)
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

        Runnable validationCallback = countDownLatch::countDown;

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    @Order(2)
    @Test
    void getUserFollowNotifications() throws Exception {
        MvcResult messageNotificationsResult = mockMvc.perform(get("/api/v1/notification/follow")
                        .queryParam("pageLimit", "100")
                        .queryParam("page", "0")
                        .header("username", "user2")
                        .header("userId", "2")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        NotificationFollowsDto notificationFollowsDto = objectMapper.readValue(messageNotificationsResult.getResponse().getContentAsString(), NotificationFollowsDto.class);

        Assertions.assertEquals(2, notificationFollowsDto.getUserId());
        Assertions.assertEquals(1, notificationFollowsDto.getNotificationFollows().size());
        Assertions.assertEquals(1, notificationFollowsDto.getPaginationInfo().getTotalElements());
        Assertions.assertEquals(2, notificationFollowsDto.getNotificationFollows().get(0).getUserId());
        Assertions.assertEquals(1, notificationFollowsDto.getNotificationFollows().get(0).getFollower().getId());

        List<FollowNotification> allFollowNotifications = followNotificationRepository.findAll();
        Assertions.assertEquals(1, allFollowNotifications.size());
        Assertions.assertEquals(notificationFollowsDto.getNotificationFollows().get(0).getId(), allFollowNotifications.get(0).getId());
        Assertions.assertFalse(allFollowNotifications.get(0).isDelivered());
    }

    @Order(3)
    @Test
    void acknowledgeUserFollowNotifications() throws Exception {
        String notificationId = followNotificationRepository.findAll().get(0).getId();

        MvcResult acknowledgeResult = mockMvc.perform(patch("/api/v1/notification/follow/" + notificationId + "/received")
                        .header("username", "user2")
                        .header("userId", "2")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        GenericResponseDto genericResponseDto = objectMapper.readValue(acknowledgeResult.getResponse().getContentAsString(), GenericResponseDto.class);

        Assertions.assertEquals("Notification updated successfully", genericResponseDto.getResponseMessage());
        Assertions.assertEquals(notificationId, genericResponseDto.getResourceId());

        Assertions.assertTrue(followNotificationRepository.findAll().get(0).isDelivered());
    }
}