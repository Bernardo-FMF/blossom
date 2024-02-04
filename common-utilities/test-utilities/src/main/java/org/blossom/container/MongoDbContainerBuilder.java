package org.blossom.container;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.MongoDBContainer;

public class MongoDbContainerBuilder {
    private static final String MONGODB_IMAGE_NAME = "mongo:7.0";

    public static MongoDBContainer getMongoDbContainer() {
        try (MongoDBContainer container = new MongoDBContainer(MONGODB_IMAGE_NAME)) {
            container.withExposedPorts(27017);
            container.start();

            Assertions.assertTrue(container.isCreated());
            Assertions.assertTrue(container.isRunning());
            return container;
        }
    }
}
