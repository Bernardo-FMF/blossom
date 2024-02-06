package org.blossom.message.controller;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.message.AbstractContextBeans;
import org.blossom.message.dto.LocalTokenDto;
import org.blossom.message.dto.MessageOperationDto;
import org.blossom.message.dto.PublishMessageDto;
import org.blossom.message.dto.UserDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.User;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.enums.ChatType;
import org.blossom.message.interceptor.WebSocketConnectInterceptor;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.UserRepository;
import org.blossom.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
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
class WsMessageSendingIntegrationTest extends AbstractContextBeans {
    public static final String WS_URL = "ws://localhost:8080/ws-chat";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @MockBean
    private WebSocketConnectInterceptor<?> webSocketConnectInterceptor;

    private KafkaFutureExecutor kafkaFutureExecutor;

    private static final Map<Integer, StompSession> stompSessionMap = new HashMap<>();
    private static final Map<Integer, String> sessionMap = new HashMap<>();

    @BeforeEach
    void setup() {
        Map<ResourceType, List<String>> topicMap = new HashMap<>();
        topicMap.put(ResourceType.USER, List.of("user-resource-event-message"));
        topicMap.put(ResourceType.SOCIAL_FOLLOW, List.of("social-follow-resource-event-message"));
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
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void handleUserCreationKafkaMessage(int id) throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaUserResource resource = KafkaUserResource.builder()
                .id(id)
                .username("user" + id)
                .fullName("user " + id)
                .imageUrl(null)
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.USER, resource);

        Runnable validationCallback = () -> {
            Optional<User> optionalLocalUser = userRepository.findById(id);
            Assertions.assertTrue(optionalLocalUser.isPresent());

            User user = optionalLocalUser.get();

            Assertions.assertEquals(id, user.getId());
            Assertions.assertEquals("user" + id, user.getUsername());
            countDownLatch.countDown();
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    @Order(3)
    @Test
    void handleSocialFollowCreationKafkaMessage() throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        KafkaSocialFollowResource resource = KafkaSocialFollowResource.builder()
                .initiatingUser(1)
                .receivingUser(2)
                .isMutualFollow(true)
                .createdAt(Instant.now())
                .build();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.SOCIAL_FOLLOW, resource);

        Runnable validationCallback = () -> {
            List<Chat> userChats = chatRepository.findByUserId(1, PageRequest.of(0, 10)).getContent();

            Assertions.assertEquals(1, userChats.size());

            Chat chat = userChats.get(0);
            Assertions.assertEquals(ChatType.PRIVATE, chat.getChatType());

            Set<User> participants = chat.getParticipants();
            Assertions.assertEquals(2, participants.size());
            List<Integer> list = participants.stream().map(User::getId).toList();
            Assertions.assertTrue(list.containsAll(List.of(1, 2)));

            countDownLatch.countDown();
        };

        Consumer<Throwable> errorCallback = Assertions::fail;

        kafkaFutureExecutor.execute(resourceEvent, 3, new FutureCallback<>(validationCallback, errorCallback));

        countDownLatch.await();
    }

    @Order(4)
    @Test
    void handleWebsocketMessageCreation() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        PublishMessageDto publishMessageDto = new PublishMessageDto();
        publishMessageDto.setContent("content");

        StompSession user1Session = stompSessionMap.get(1);
        StompSession user2Session = stompSessionMap.get(2);

        StompHeaders headers = new StompHeaders();
        headers.setDestination("/exchange/amq.direct/chat.message-user" + sessionMap.get(2));
        headers.set("simpSessionId", sessionMap.get(2));

        Queue<MessageOperationDto> messages = new ConcurrentLinkedQueue<>();

        user2Session.subscribe(headers, new StompFrameHandler() {
            @Override
            public @Nonnull Type getPayloadType(@Nullable StompHeaders headers) {
                return MessageOperationDto.class;
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                messages.add((MessageOperationDto) payload);
                countDownLatch.countDown();
            }
        });

        user1Session.send("/app/chat/1/publishMessage", publishMessageDto);

        boolean await = countDownLatch.await(30, TimeUnit.SECONDS);

        Assertions.assertTrue(await);

        Assertions.assertEquals(1, messages.size());

        MessageOperationDto polledMessage = messages.poll();
        Assertions.assertNotNull(polledMessage);

        Assertions.assertEquals(BroadcastType.MESSAGE_CREATED, polledMessage.getType());
        Assertions.assertEquals(1, polledMessage.getMessage().getUser().getId());
        Assertions.assertEquals(1, polledMessage.getMessage().getChat().getId());
        Assertions.assertEquals("content", polledMessage.getMessage().getContent());
        Assertions.assertTrue(polledMessage.getMessage().getChat().getParticipants().stream().map(UserDto::getId).toList().containsAll(List.of(1, 2)));
    }

    // get chat messages

    // update message

    // get chat messages

    // delete message

    // get chat messages (should be none)

}