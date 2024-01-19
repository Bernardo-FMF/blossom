package org.blossom.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
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

    @MockBean(name = "social-grpc-channel")
    private ManagedChannel managedChannelSocial;

    @MockBean(name = "activity-grpc-channel")
    private ManagedChannel managedChannelActivity;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.cassandra.keyspace-name=" + "feedSpace",
                    "spring.data.cassandra.contact-points=" + cassandraDbContainer.getHost(),
                    "spring.data.cassandra.port=" + cassandraDbContainer.getFirstMappedPort(),
                    "spring.data.cassandra.local-datacenter=" + "datacenter1",
                    "server.port=" + 8080,
                    "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
                    "spring.kafka.producer.properties.spring.json.type.mapping=event:org.blossom.model.ResourceEvent",
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
