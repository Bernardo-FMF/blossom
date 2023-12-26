package org.blossom.post;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Log4j2
public abstract class AbstractTestContainers {
    @Container
    protected static final MongoDBContainer mongoDbContainer = buildDbContainer();

    @Container
    protected static GenericContainer<?> zookeeperContainer = getZookeeperContainer();

    @Container
    protected static final KafkaContainer kafkaContainer = getKafkaContainer();

    @Container
    protected static final GenericContainer<?> redisContainer = getRedisContainer();

    private static GenericContainer<?> getZookeeperContainer() {
        try (GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper:7.3.0"))) {
            container
                    .withExposedPorts(2181)
                    .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
                    .start();
            return container;
        }
    }

    private static GenericContainer<?> getRedisContainer() {
        try (GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("redis:7.2.3-bookworm"))) {
            container
                    .withExposedPorts(6379)
                    .start();
            return container;
        }
    }

    private static KafkaContainer getKafkaContainer() {
        try (KafkaContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))) {
            container
                    .dependsOn(zookeeperContainer)
                    .start();
            return container;
        }
    }

    private static MongoDBContainer buildDbContainer() {
        try (MongoDBContainer container = new MongoDBContainer("mongo:7.0")) {
            container.withExposedPorts(27017);
            container.start();

            Assertions.assertTrue(container.isCreated());
            Assertions.assertTrue(container.isRunning());
            return container;
        }
    }
}
