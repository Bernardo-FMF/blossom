package org.blossom.facade;

import io.grpc.ManagedChannel;
import org.blossom.configuration.GrpcConfiguration;
import org.blossom.imagecontract.ImageContractGrpc;
import org.springframework.stereotype.Component;

@Component
public class ImageContractGrpcClientFacade extends BaseGrpcClientFacade implements IStubAccessor {
    private final ImageContractGrpc.ImageContractStub nonBlockStub;
    private final ImageContractGrpc.ImageContractBlockingStub blockStub;

    public ImageContractGrpcClientFacade(GrpcConfiguration grpcConfiguration, ManagedChannel managedChannel) {
        super(grpcConfiguration, managedChannel);
        this.nonBlockStub = buildNonBlockingStub();
        this.blockStub = buildBlockingStub();
    }

    @Override
    public ImageContractGrpc.ImageContractBlockingStub buildBlockingStub() {
        return ImageContractGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public ImageContractGrpc.ImageContractStub buildNonBlockingStub() {
        return ImageContractGrpc.newStub(managedChannel);
    }

    @Override
    public ImageContractGrpc.ImageContractBlockingStub getBlockingStub() {
        return blockStub;
    }

    @Override
    public ImageContractGrpc.ImageContractStub getNonBlockingStub() {
        return nonBlockStub;
    }
}