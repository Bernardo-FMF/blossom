package org.blossom.notification.controller;

import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.model.EventType;
import org.blossom.model.KafkaMessageResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.blossom.notification.AbstractContextBeans;
import org.blossom.notification.dto.LocalTokenDto;
import org.blossom.notification.dto.NotificationMessagesDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.repository.MessageNotificationRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageControllerIntegrationTest extends AbstractContextBeans {
    @Autowired
    private MessageNotificationRepository messageNotificationRepository;

    private KafkaFutureExecutor kafkaFutureExecutor;

    @BeforeEach
    void setup() {
        Map<ResourceType, List<String>> topicMap = new HashMap<>();
        topicMap.put(ResourceType.MESSAGE, List.of("message-resource-event-notification"));
        kafkaFutureExecutor = new KafkaFutureExecutor(kafkaTemplate, topicMap);

        LocalTokenDto userToken1 = new LocalTokenDto();
        userToken1.setUserId(1);
        userToken1.setUsername("user1");

        LocalTokenDto userToken2 = new LocalTokenDto();
        userToken2.setUserId(2);
        userToken2.setUsername("user2");

        ResponseEntity<LocalTokenDto> responseToken1 = ResponseEntity.of(Optional.of(userToken1));
        ResponseEntity<LocalTokenDto> responseToken2 = ResponseEntity.of(Optional.of(userToken2));

        Mockito.when(authClient.validate("token1")).thenReturn(responseToken1);
        Mockito.when(authClient.validate("token2")).thenReturn(responseToken2);

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
    void handleMessageCreationKafkaMessage() throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaMessageResource resource = KafkaMessageResource.builder()
                .id(1)
                .senderId(1)
                .recipientsIds(new Integer[] {2})
                .chatId(1)
                .content("Hello")
                .isDeleted(false)
                .updatedAt(null)
                .createdAt(Instant.now())
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.MESSAGE, resource);

        Runnable validationCallback = () -> {
            List<MessageNotification> messageNotifications = messageNotificationRepository.findByMessageId(1);

            Assertions.assertEquals(1, messageNotifications.size());

            MessageNotification notification = messageNotifications.get(0);
            Assertions.assertEquals(2, notification.getRecipientId());
            Assertions.assertEquals(1, notification.getSenderId());
            Assertions.assertEquals(1, notification.getChatId());
            Assertions.assertEquals("Hello", notification.getContent());

            countDownLatch.countDown();
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    @Order(2)
    @Test
    void getUserMessageNotifications() throws Exception {
        MvcResult messageNotificationsResult = mockMvc.perform(get("/api/v1/notification/message")
                        .queryParam("pageLimit", "100")
                        .queryParam("page", "0")
                        .header("username", "user2")
                        .header("userId", "2")
                        .header("userRoles", "USER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        NotificationMessagesDto notificationMessagesDto = objectMapper.readValue(messageNotificationsResult.getResponse().getContentAsString(), NotificationMessagesDto.class);

        Assertions.assertEquals(2, notificationMessagesDto.getUserId());
        Assertions.assertEquals(1, notificationMessagesDto.getNotificationMessages().size());
        Assertions.assertEquals(1, notificationMessagesDto.getPaginationInfo().getTotalElements());
        Assertions.assertEquals(1, notificationMessagesDto.getNotificationMessages().get(0).getId());
        Assertions.assertEquals("Hello", notificationMessagesDto.getNotificationMessages().get(0).getContent());
        Assertions.assertEquals(1, notificationMessagesDto.getNotificationMessages().get(0).getChatId());
        Assertions.assertEquals(1, notificationMessagesDto.getNotificationMessages().get(0).getUser().getId());
    }
}