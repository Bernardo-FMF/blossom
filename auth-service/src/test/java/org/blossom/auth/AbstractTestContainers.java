package org.blossom.auth;

import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.ValidateResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Log4j2
public abstract class AbstractTestContainers {
    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer = buildDbContainer();

    @Container
    protected static final KafkaContainer kafkaContainer = getKafkaContainer();

    private static KafkaContainer getKafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));
    }

    private static PostgreSQLContainer<?> buildDbContainer() {
        try (PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16.1")) {
            container
                    .withDatabaseName("auth-db")
                    .withUsername("root")
                    .withPassword("root");


            container.start();

            Assertions.assertTrue(container.isCreated());
            Assertions.assertTrue(container.isRunning());

            return container;
        }
    }

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgreSQLContainer.getJdbcUrl(),
                        postgreSQLContainer.getUsername(),
                        postgreSQLContainer.getPassword()
                )
                .load();

        flyway.migrate();

        ValidateResult migrationResult = flyway.validateWithResult();

        Assertions.assertTrue(migrationResult.validationSuccessful);
        Assertions.assertNull(migrationResult.errorDetails);
    }


    @AfterAll
    static void afterAll() {
        if (postgreSQLContainer != null) {
            postgreSQLContainer.stop();

            Assertions.assertFalse(postgreSQLContainer.isRunning());
        }

        kafkaContainer.stop();
        Assertions.assertFalse(kafkaContainer.isRunning());
    }
}
