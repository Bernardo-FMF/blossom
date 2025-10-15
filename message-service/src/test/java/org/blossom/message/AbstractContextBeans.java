package org.blossom.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blossom.message.client.AuthClient;
import org.blossom.model.ResourceEvent;
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
    protected KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthClient authClient;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgresDbContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresDbContainer.getUsername(),
                    "spring.datasource.password=" + postgresDbContainer.getPassword(),
                    "server.port=" + 8080,
                    "websocket.origins=*",
                    "websocket.test.enabled=true",
                    "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
                    "spring.kafka.producer.properties.spring.json.type.mapping=event:org.blossom.model.ResourceEvent",
                    "spring.redis.host=" + redisContainer.getHost(),
                    "spring.redis.port=" + redisContainer.getFirstMappedPort(),
                    "broker.host=" + rabbitMqContainer.getHost(),
                    "broker.port=" + rabbitMqContainer.getMappedPort(61613),
                    "broker.username=" + rabbitMqContainer.getEnvMap().get("RABBITMQ_DEFAULT_USER"),
                    "broker.password=" + rabbitMqContainer.getEnvMap().get("RABBITMQ_DEFAULT_PASS"),
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
