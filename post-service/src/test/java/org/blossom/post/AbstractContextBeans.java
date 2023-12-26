package org.blossom.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import org.blossom.post.grpc.GrpcClientImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(initializers = { AbstractContextBeans.Initializer.class })
public abstract class AbstractContextBeans extends AbstractTestContainers {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ManagedChannel managedChannel;

    @MockBean
    private GrpcClientImageService imageService;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoDbContainer.getHost(),
                    "spring.data.mongodb.port=" + mongoDbContainer.getFirstMappedPort(),
                    "spring.data.mongodb.database=" + "post-db",
                    "spring.redis.host=" + redisContainer.getHost(),
                    "spring.redis.port=" + redisContainer.getExposedPorts().get(0),
                    "server.port=" + 8080,
                    "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
