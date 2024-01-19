package org.blossom.feed;

import lombok.extern.log4j.Log4j2;
import org.blossom.container.CassandraContainerBuilder;
import org.blossom.container.KafkaContainerBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Log4j2
public abstract class AbstractTestContainers {
    @Container
    protected static final CassandraContainer<?> cassandraDbContainer = CassandraContainerBuilder.getCassandraContainer();

    @Container
    protected static final GenericContainer<?> zookeeperContainer = KafkaContainerBuilder.getZookeeperContainer();

    @Container
    protected static final KafkaContainer kafkaContainer = KafkaContainerBuilder.getKafkaContainer(zookeeperContainer);

    @AfterAll
    static void afterAll() {
        if (cassandraDbContainer != null) {
            cassandraDbContainer.stop();

            Assertions.assertFalse(cassandraDbContainer.isRunning());
        }

        kafkaContainer.stop();
        Assertions.assertFalse(kafkaContainer.isRunning());

        zookeeperContainer.stop();
        Assertions.assertFalse(zookeeperContainer.isRunning());
    }
}
