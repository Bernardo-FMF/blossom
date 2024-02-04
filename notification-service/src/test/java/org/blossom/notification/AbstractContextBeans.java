package org.blossom.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blossom.model.ResourceEvent;
import org.blossom.notification.client.AuthClient;
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

    @Autowired
    protected KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @MockBean
    protected AuthClient authClient;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoDbContainer.getHost(),
                    "spring.data.mongodb.port=" + mongoDbContainer.getFirstMappedPort(),
                    "spring.data.mongodb.database=" + "notification-db",
                    "server.port=" + 8080,
                    "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
                    "spring.kafka.producer.properties.spring.json.type.mapping=event:org.blossom.model.ResourceEvent",
                    "broker.host=" + rabbitMqContainer.getHost(),
                    "broker.port=" + rabbitMqContainer.getMappedPort(61613),
                    "broker.username=" + rabbitMqContainer.getEnvMap().get("RABBITMQ_DEFAULT_USER"),
                    "broker.password=" + rabbitMqContainer.getEnvMap().get("RABBITMQ_DEFAULT_PASS"),
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
