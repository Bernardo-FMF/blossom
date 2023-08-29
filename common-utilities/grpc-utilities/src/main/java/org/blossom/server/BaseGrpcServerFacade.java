package org.blossom.server;

import io.grpc.*;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Log4j2
public abstract class BaseGrpcServerFacade {
    protected IGrpcServerConfiguration grpcConfiguration;
    protected BindableService[] services;
    protected Server server;

    public BaseGrpcServerFacade(IGrpcServerConfiguration grpcConfiguration, BindableService[] services) {
        this.grpcConfiguration = grpcConfiguration;
        this.services = services;
        this.server = buildServer(grpcConfiguration.getFreePort());
    }

    public int getPort() {
        return grpcConfiguration.getFreePort();
    }

    private Server buildServer(final int port) {
        ServerBuilder<?> serverBuilder = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create());
        for (BindableService service: services) {
            serverBuilder = serverBuilder.addService(service);
        }
        return serverBuilder.build();
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
                        BaseGrpcServerFacade.this.stop();
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
