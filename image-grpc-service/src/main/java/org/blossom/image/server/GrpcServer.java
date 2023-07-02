package org.blossom.image.server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import lombok.extern.log4j.Log4j2;
import org.blossom.image.configuration.GrpcConfiguration;
import org.blossom.image.service.ImageService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import io.grpc.Server;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class GrpcServer {
    @Lazy
    private final GrpcConfiguration grpcConfiguration;

    @Lazy
    private final ImageService imageService;

    private final Server server;

    public GrpcServer(final GrpcConfiguration grpcConfiguration, ImageService imageService) {
        this.grpcConfiguration = grpcConfiguration;
        this.imageService = imageService;
        this.server = buildServer(grpcConfiguration.getFreePort());
    }

    public int getPort() {
        return grpcConfiguration.getFreePort();
    }

    private Server buildServer(final int port) {
        return Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(imageService)
                .build();
    }

    public void start() {
        if (Objects.nonNull(server)) {
            log.info("gRPC server is starting");
            try {
                server.start();
                log.info(String.format("gRPC server started and listening on port: %d", grpcConfiguration.getFreePort()));
                displayAvailableServices();
                addShutdownHook();
            } catch (Throwable e) {
                log.error("There was an error starting gRPC server", e);
            }
        } else {
            log.error("gRPC server is null");
        }
    }

    public void stop() throws InterruptedException {
        if (Objects.nonNull(server)) {
            int awaitTerminationInSeconds = grpcConfiguration.getServerAwaitTerminationInSeconds();
            if (0 < awaitTerminationInSeconds) {
                server.shutdown()
                        .awaitTermination(
                                awaitTerminationInSeconds,
                                TimeUnit.SECONDS
                        );
            } else {
                server.shutdown()
                        .awaitTermination();
            }
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Shutting down gRPC server");
                    try {
                        GrpcServer.this.stop();
                    } catch (Exception e) {
                        log.error("There was an error shutting down gRPC server", e);
                    }
                    log.info("gRPC server shut down");
                })
        );
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (Objects.nonNull(server)) {
            server.awaitTermination();
        }
    }

    private void displayAvailableServices() {
        server.getServices()
                .forEach(s -> log.info(String.format("Available gRPC service: %s", s.getServiceDescriptor().getName())));
    }
}