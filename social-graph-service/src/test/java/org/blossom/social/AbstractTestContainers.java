package org.blossom.social;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Log4j2
public abstract class AbstractTestContainers {
    @Container
    protected static final Neo4jContainer<?> neo4jContainer = buildDbContainer();

    private static Neo4jContainer<?> buildDbContainer() {
        try (Neo4jContainer<?> container = new Neo4jContainer<>("neo4j:5.12.0-community-bullseye")) {
            container.start();

            Assertions.assertTrue(container.isCreated());
            Assertions.assertTrue(container.isRunning());
            return container;
        }
    }

    @AfterAll
    static void afterAll() {
        if (neo4jContainer != null) {
            neo4jContainer.stop();

            Assertions.assertFalse(neo4jContainer.isRunning());
        }
    }
}
