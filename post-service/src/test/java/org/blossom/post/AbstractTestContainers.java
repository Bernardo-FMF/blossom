package org.blossom.post;

import lombok.extern.log4j.Log4j2;
import org.blossom.container.MongoDbContainerBuilder;
import org.blossom.container.RedisContainerBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Log4j2
public abstract class AbstractTestContainers {
    @Container
    protected static final MongoDBContainer mongoDbContainer = MongoDbContainerBuilder.getMongoDbContainer();

    @Container
    protected static final GenericContainer<?> redisContainer = RedisContainerBuilder.getRedisContainer();

    @AfterAll
    static void afterAll() {
        if (mongoDbContainer != null) {
            mongoDbContainer.stop();
            redisContainer.stop();

            Assertions.assertFalse(mongoDbContainer.isRunning());
            Assertions.assertFalse(redisContainer.isRunning());
        }
    }
}
