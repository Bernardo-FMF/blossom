package org.blossom.facade;

import io.grpc.ManagedChannel;
import lombok.extern.log4j.Log4j2;
import org.blossom.configuration.GrpcConfiguration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Log4j2
public abstract class BaseGrpcClientFacade implements IGrpcClientFacade {
    protected GrpcConfiguration grpcConfiguration;

    protected ManagedChannel managedChannel;

    public BaseGrpcClientFacade(GrpcConfiguration grpcConfiguration, ManagedChannel managedChannel) {
        this.grpcConfiguration = grpcConfiguration;
        this.managedChannel = managedChannel;
    }

    @Override
    public void start() {
        if (Objects.nonNull(managedChannel)) {
            log.info(String.format("gRPC client is starting. Configured server located on service: %s", grpcConfiguration.getServerName()));
            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> {
                        log.info("Shutting down gRPC client");
                        try {
                            BaseGrpcClientFacade.this.stop();
                        } catch (Exception e) {
                            log.error("There was an error shutting down gRPC client", e);
                        }
                        log.info("gRPC client shut down");
                    })
            );
        } else {
            log.error("gRPC client channel is null");
        }
    }

    @Override
    public void stop() throws InterruptedException {
        if (Objects.nonNull(managedChannel)) {
            int awaitTerminationInSeconds = grpcConfiguration.getClientAwaitTerminationInSeconds();
            managedChannel.shutdown()
                    .awaitTermination(
                            awaitTerminationInSeconds,
                            TimeUnit.SECONDS
                    );
        }
    }
}
