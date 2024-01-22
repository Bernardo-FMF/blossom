package org.blossom.container;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerBuilder {
    private static final String POSTGRES_IMAGE_NAME = "postgres:16.1";

    public static PostgreSQLContainer<?> getPostgresContainer(String dbName, String username, String password) {
        try (PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME)) {
            container
                    .withDatabaseName(dbName)
                    .withUsername(username)
                    .withPassword(password)
                    .start();

            Assertions.assertTrue(container.isCreated());
            Assertions.assertTrue(container.isRunning());

            return container;
        }
    }
}
