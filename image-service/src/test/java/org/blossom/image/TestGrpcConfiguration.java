package org.blossom.image;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.blossom.facade.IGrpcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.blossom.image.AbstractContextBeans.GRPC_PORT;

@Configuration
public class TestGrpcConfiguration implements IGrpcConfiguration {
    @Bean
    public ManagedChannel buildManagedChannel() {
        return ManagedChannelBuilder.forAddress("localhost", GRPC_PORT)
                .usePlaintext()
                .build();
    }

    @Override
    public String getServerName() {
        return "image-service";
    }

    @Override
    public int getClientAwaitTerminationInSeconds() {
        return 0;
    }
}
