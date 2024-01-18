package org.blossom.container;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.CassandraContainer;

public class CassandraContainerBuilder {
    public static final String CASSANDRA_IMAGE_NAME = "cassandra:latest";

    @SneakyThrows
    public static CassandraContainer<?> getCassandraContainer() {
        try (CassandraContainer<?> container = new CassandraContainer<>(CASSANDRA_IMAGE_NAME)) {
            container.start();

            Assertions.assertTrue(container.isCreated());
            Assertions.assertTrue(container.isRunning());
            return container;
        }
    }
}