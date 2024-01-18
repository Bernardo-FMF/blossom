package org.blossom.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerBuilder {
    private static final String KAFKA_IMAGE_NAME = "confluentinc/cp-kafka:7.3.0";
    private static final String ZOOKEEPER_IMAGE_NAME = "confluentinc/cp-zookeeper:7.3.0";

    public static GenericContainer<?> getZookeeperContainer() {
        try (GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(ZOOKEEPER_IMAGE_NAME))) {
            container
                    .withExposedPorts(2181)
                    .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
                    .start();
            return container;
        }
    }

    public static KafkaContainer getKafkaContainer(GenericContainer<?> zookeeperContainer) {
        try (KafkaContainer container = new KafkaContainer(DockerImageName.parse(KAFKA_IMAGE_NAME))) {
            container
                    .dependsOn(zookeeperContainer)
                    .start();
            return container;
        }
    }
}
