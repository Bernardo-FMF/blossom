package org.blossom.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import org.blossom.model.ResourceEvent;
import org.blossom.post.grpc.service.GrpcClientActivityService;
import org.blossom.post.grpc.service.GrpcClientImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(initializers = { AbstractContextBeans.Initializer.class })
public abstract class AbstractContextBeans extends AbstractTestContainers {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean(name = "image-grpc-channel")
    private ManagedChannel managedChannelImage;

    @MockBean(name = "activity-grpc-channel")
    private ManagedChannel managedChannelActivity;

    @MockBean
    protected GrpcClientImageService imageService;

    @MockBean
    protected GrpcClientActivityService activityService;

    @MockBean
    protected KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoDbContainer.getHost(),
                    "spring.data.mongodb.port=" + mongoDbContainer.getFirstMappedPort(),
                    "spring.data.mongodb.database=" + "post-db",
                    "spring.redis.host=" + redisContainer.getHost(),
                    "spring.redis.port=" + redisContainer.getFirstMappedPort(),
                    "server.port=" + 8080,
                    "spring.kafka.bootstrap-servers=localhost:8080",
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
