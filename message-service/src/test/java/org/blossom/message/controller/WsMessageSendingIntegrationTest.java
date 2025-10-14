package org.blossom.message.controller;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.blossom.future.FutureCallback;
import org.blossom.future.KafkaFutureExecutor;
import org.blossom.message.AbstractContextBeans;
import org.blossom.message.dto.*;
import org.blossom.message.entity.User;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.interceptor.WebSocketConnectInterceptor;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.UserRepository;
import org.blossom.model.EventType;
import org.blossom.model.KafkaUserResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
    void createChat() throws Exception {
        ChatCreationDto chatCreationDto = new ChatCreationDto();
        chatCreationDto.setName("xpto");
        chatCreationDto.setInitialParticipants(List.of(1, 2));
        chatCreationDto.setGroup(false);

        MvcResult result = mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("username", "user1")
                        .header("userId", "1")
                        .header("userRoles", "USER")
                        .content(objectMapper.writeValueAsString(chatCreationDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ChatDto chatDto = objectMapper.readValue(result.getResponse().getContentAsString(), ChatDto.class);

        Assertions.assertEquals("xpto", chatDto.getName());
        Assertions.assertFalse(chatDto.isGroup());
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