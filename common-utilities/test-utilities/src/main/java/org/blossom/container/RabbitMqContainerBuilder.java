package org.blossom.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RabbitMqContainerBuilder {
    private static final String RABBITMQ_IMAGE_NAME = "bernardofmf/blossom-custom-rabbitmq-stomp:latest";

    public static GenericContainer<?> getRabbitMqContainer() {
        try (GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(RABBITMQ_IMAGE_NAME))) {
            container
                    .withExposedPorts(15672, 61613)
                    .withEnv("RABBITMQ_DEFAULT_USER", "user")
                    .withEnv("RABBITMQ_DEFAULT_PASS", "password")
                    .start();

            return container;
        }
    }
}
