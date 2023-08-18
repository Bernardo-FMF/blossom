package org.blossom.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
public class GrpcConfiguration {
    @Value("${grpc.client.awaitTerminationInSeconds}")
    private int clientAwaitTerminationInSeconds;

    @Value("${grpc.client.withDeadlineAfterInSeconds}")
    private int clientWithDeadlineAfterInSeconds;

    @Value("${grpc.server.name}")
    private String serverName;

    @Bean
    public ManagedChannel grpcChannel(DiscoveryClient discoveryClient) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serverName);
        ServiceInstance instance = instances.get(0);
        String host = instance.getHost();
        int grpcPort = Integer.parseInt(instance.getMetadata().get("grpc-port"));

        return ManagedChannelBuilder.forAddress(host, grpcPort)
                .usePlaintext()
                .build();
    }
}