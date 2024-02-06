package org.blossom.notification.kafka;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.model.EventType;
import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.blossom.notification.AbstractContextBeans;
import org.blossom.notification.dto.LocalTokenDto;
import org.blossom.notification.dto.NotificationFollowDto;
import org.blossom.notification.dto.NotificationFollowOperationDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.interceptor.WebSocketConnectInterceptor;
import org.blossom.notification.repository.FollowNotificationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SocialFollowKafkaMessageIntegrationTest extends AbstractContextBeans {
    public static final String WS_URL = "ws://localhost:8080/ws-notification";

    @Autowired
    private FollowNotificationRepository followNotificationRepository;

    @MockBean
    private WebSocketConnectInterceptor<?> webSocketConnectInterceptor;

    private static final Map<Integer, StompSession> stompSessionMap = new HashMap<>();
    private static final Map<Integer, String> sessionMap = new HashMap<>();

    private KafkaFutureExecutor kafkaFutureExecutor;

    @BeforeEach
    void setup() {
        Map<ResourceType, List<String>> topicMap = new HashMap<>();
        topicMap.put(ResourceType.SOCIAL_FOLLOW, List.of("social-follow-resource-event-notification"));
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
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void createStompSessions(int id) throws ExecutionException, InterruptedException, TimeoutException {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "token" + id);

        ArgumentCaptor<SessionConnectEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SessionConnectEvent.class);

        StompSession stompSession = stompClient
                .connectAsync(WS_URL, webSocketHttpHeaders, stompHeaders, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        Mockito.verify(webSocketConnectInterceptor).onApplicationEvent(eventArgumentCaptor.capture());

        sessionMap.put(id, SimpMessageHeaderAccessor.getSessionId(eventArgumentCaptor.getValue().getMessage().getHeaders()));
        stompSessionMap.put(id, stompSession);
    }

    @Order(2)
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

        StompSession user2Session = stompSessionMap.get(2);

        StompHeaders headers = new StompHeaders();
        headers.setDestination("/exchange/amq.direct/notification.follow-user" + sessionMap.get(2));
        headers.set("simpSessionId", sessionMap.get(2));

        Queue<NotificationFollowOperationDto> notifications = new ConcurrentLinkedQueue<>();

        user2Session.subscribe(headers, new StompFrameHandler() {
            @Override
            public @Nonnull Type getPayloadType(@Nullable StompHeaders headers) {
                return NotificationFollowOperationDto.class;
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                notifications.add((NotificationFollowOperationDto) payload);
                countDownLatch.countDown();
            }
        });

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(() -> {}, errorCallback));

        countDownLatch.await();

        List<FollowNotification> followNotifications = followNotificationRepository.findAll();

        Assertions.assertEquals(1, followNotifications.size());
        Assertions.assertEquals(1, notifications.size());

        NotificationFollowDto polledNotification = Objects.requireNonNull(notifications.poll()).getNotification();
        FollowNotification dbNotification = followNotifications.get(0);

        Assertions.assertEquals(polledNotification.getId(), dbNotification.getId());
        Assertions.assertEquals(2, polledNotification.getUserId());
        Assertions.assertEquals(1, polledNotification.getFollower().getId());
        Assertions.assertTrue(dbNotification.isDelivered());
    }
}
