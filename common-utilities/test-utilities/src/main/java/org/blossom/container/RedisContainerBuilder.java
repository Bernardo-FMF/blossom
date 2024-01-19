package org.blossom.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisContainerBuilder {
    public static final String REDIS_IMAGE_NAME = "redis:7.2.3-bookworm";

    public GenericContainer<?> getRedisContainer() {
        try (GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE_NAME))) {
            container
                    .withExposedPorts(6379)
                    .start();
            return container;
        }
    }
}
