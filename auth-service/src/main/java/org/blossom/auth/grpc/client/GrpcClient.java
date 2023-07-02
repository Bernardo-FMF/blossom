package org.blossom.auth.grpc.client;

import io.grpc.ManagedChannel;
import lombok.extern.log4j.Log4j2;
import org.blossom.imagecontract.ImageContractGrpc;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class GrpcClient {

    @Lazy
    private final GrpcConfiguration grpcConfiguration;

    @Lazy
    private final ManagedChannel managedChannel;

    private final ImageContractGrpc.ImageContractStub nonBlockStub;
    private final ImageContractGrpc.ImageContractBlockingStub blockStub;

    public GrpcClient(final GrpcConfiguration grpcConfiguration, final ManagedChannel managedChannel) {
        this.grpcConfiguration = grpcConfiguration;
        this.managedChannel = managedChannel;
        this.nonBlockStub = buildImageContractNonBlockStub();
        this.blockStub = buildImageContractBlockStub();
    }

    public void start() {
        if (Objects.nonNull(managedChannel)) {
            log.info(String.format("gRPC client is starting. Configured server located on service: %s", grpcConfiguration.getServerName()));
            addShutdownHook();
        } else {
            log.error("gRPC client channel is null");
        }
    }

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

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Shutting down gRPC client");
                    try {
                        GrpcClient.this.stop();
                    } catch (Exception e) {
                        log.error("There was an error shutting down gRPC client", e);
                    }
                    log.info("gRPC client shut down");
                })
        );
    }

    private ImageContractGrpc.ImageContractBlockingStub buildImageContractBlockStub() {
        return ImageContractGrpc.newBlockingStub(managedChannel);
    }

    private ImageContractGrpc.ImageContractStub buildImageContractNonBlockStub() {
        return ImageContractGrpc.newStub(managedChannel);
    }

    public ImageContractGrpc.ImageContractStub getNonBlockStub() {
        return nonBlockStub;
    }

    public ImageContractGrpc.ImageContractBlockingStub getBlockStub() {
        return blockStub;
    }
}