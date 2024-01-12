package org.blossom.post.grpc.client;

import io.grpc.ManagedChannel;
import org.blossom.facade.BaseGrpcClientFacade;
import org.blossom.imagecontract.ImageContractGrpc;
import org.blossom.post.configuration.GrpcImageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ImageContractGrpcClientFacade extends BaseGrpcClientFacade {
    private final ImageContractGrpc.ImageContractStub nonBlockStub;
    private final ImageContractGrpc.ImageContractBlockingStub blockStub;

    @Autowired
    public ImageContractGrpcClientFacade(GrpcImageConfiguration grpcConfiguration, @Qualifier("image-grpc-channel") ManagedChannel managedChannel) {
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