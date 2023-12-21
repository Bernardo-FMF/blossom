package org.blossom.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.services.s3.S3Client;

@ContextConfiguration(initializers = { AbstractContextBeans.Initializer.class })
public abstract class AbstractContextBeans {
    public static final int GRPC_PORT = 8081;

    @MockBean
    private EurekaServiceRegistry eurekaServiceRegistry;

    @MockBean
    private EurekaRegistration registration;

    @MockBean
    protected S3Client s3Client;

    @Autowired
    protected MockGrpcClient mockGrpcClient;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "server.port=" + 8080,
                    "grpc.server.port=" + GRPC_PORT,
                    "aws.region=us-east-1",
                    "aws.s3.mock=false",
                    "aws.s3.path=/",
                    "aws.s3.buckets.imageBucket=bucket1",
                    "eureka.client.enabled=false"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
