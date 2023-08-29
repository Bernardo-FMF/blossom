package org.blossom.server;

import java.io.IOException;
import java.net.ServerSocket;

public interface IGrpcServerConfiguration {
    void setServerPort(int localPort);
    int getServerPort();
    int getServerAwaitTerminationInSeconds();

    default int getFreePort() {
        if (getServerPort() != 0) {
            return getServerPort();
        }

        try (ServerSocket socket = new ServerSocket(0)) {
            setServerPort(socket.getLocalPort());
            return socket.getLocalPort();
        } catch (IOException e) {
            // Handle the exception
            throw new IllegalStateException("Failed to find an available port", e);
        }
    }
}
