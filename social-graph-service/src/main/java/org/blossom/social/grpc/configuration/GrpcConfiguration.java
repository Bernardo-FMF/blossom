package org.blossom.social.grpc.configuration;

import org.blossom.server.IGrpcServerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration implements IGrpcServerConfiguration {
    @Value("${grpc.server.port}")
    private int serverPort;

    @Value("${grpc.server.awaitTerminationInSeconds}")
    private int serverAwaitTerminationInSeconds;

    @Override
    public void setServerPort(int localPort) {
        this.serverPort = localPort;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public int getServerAwaitTerminationInSeconds() {
        return serverAwaitTerminationInSeconds;
    }
}
