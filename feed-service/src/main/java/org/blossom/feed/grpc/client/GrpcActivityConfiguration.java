package org.blossom.feed.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.blossom.facade.IGrpcConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GrpcActivityConfiguration implements IGrpcConfiguration {
    @Value("${grpc.client.awaitTerminationInSeconds}")
    private int clientAwaitTerminationInSeconds;

    @Value("${grpc.server.names.activity}")
    private String serverName;

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getClientAwaitTerminationInSeconds() {
        return clientAwaitTerminationInSeconds;
    }

    @Bean(name = "activity-grpc-channel")
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