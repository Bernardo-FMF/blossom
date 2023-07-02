package org.blossom.image.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@Getter
public class GrpcConfiguration {
    @Value("${grpc.server.port}")
    private int serverPort;

    @Value("${grpc.server.awaitTerminationInSeconds}")
    private int serverAwaitTerminationInSeconds;

    public int getFreePort() {
        if (serverPort != 0) {
            return serverPort;
        }

        try (ServerSocket socket = new ServerSocket(0)) {
            this.serverPort = socket.getLocalPort();
            return socket.getLocalPort();
        } catch (IOException e) {
            // Handle the exception
            throw new IllegalStateException("Failed to find an available port", e);
        }
    }
}