package org.blossom.message;

import lombok.extern.log4j.Log4j2;
import org.blossom.container.KafkaContainerBuilder;
import org.blossom.container.PostgresContainerBuilder;
import org.blossom.container.RabbitMqContainerBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Log4j2
public abstract class AbstractTestContainers {
    @Container
    protected static final PostgreSQLContainer<?> postgresDbContainer = PostgresContainerBuilder.getPostgresContainer("auth-db", "root", "root");

    @Container
    protected static final GenericContainer<?> rabbitMqContainer = RabbitMqContainerBuilder.getRabbitMqContainer();

    @Container
    protected static final GenericContainer<?> zookeeperContainer = KafkaContainerBuilder.getZookeeperContainer();

    @Container
    protected static final KafkaContainer kafkaContainer = KafkaContainerBuilder.getKafkaContainer(zookeeperContainer);

    @AfterAll
    static void afterAll() {
        if (postgresDbContainer != null) {
            postgresDbContainer.stop();

            Assertions.assertFalse(postgresDbContainer.isRunning());
        }

        rabbitMqContainer.stop();
        Assertions.assertFalse(rabbitMqContainer.isRunning());

        kafkaContainer.stop();
        Assertions.assertFalse(kafkaContainer.isRunning());

        zookeeperContainer.stop();
        Assertions.assertFalse(zookeeperContainer.isRunning());
    }
}
