package org.blossom.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blossom.model.ResourceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(initializers = { AbstractContextBeans.Initializer.class })
public abstract class AbstractContextBeans extends AbstractTestContainers {
    public static final int GRPC_PORT = 8081;

    @Autowired
    protected MockGrpcClient mockGrpcClient;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private EurekaServiceRegistry eurekaServiceRegistry;

    @MockBean
    private EurekaRegistration registration;

    @MockBean
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.neo4j.uri=" + neo4jContainer.getBoltUrl(),
                    "spring.neo4j.authentication.username=" + "neo4j",
                    "spring.neo4j.authentication.password=" + neo4jContainer.getAdminPassword(),
                    "server.port=" + 8080,
                    "grpc.server.port=" + 8081,
                    "spring.kafka.bootstrap-servers=" + "localhost:8080",
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
