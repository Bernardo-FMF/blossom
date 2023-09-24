package org.blossom.feed.grpc.client;

import io.grpc.ManagedChannel;
import org.blossom.facade.BaseGrpcClientFacade;
import org.blossom.feed.grpc.configuration.GrpcSocialConfiguration;
import org.blossom.socialcontract.SocialContractGrpc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SocialContractGrpcClientFacade extends BaseGrpcClientFacade {
    private final SocialContractGrpc.SocialContractStub nonBlockStub;
    private final SocialContractGrpc.SocialContractBlockingStub blockStub;

    public SocialContractGrpcClientFacade(GrpcSocialConfiguration grpcConfiguration, @Qualifier("social-grpc-channel") ManagedChannel managedChannel) {
        super(grpcConfiguration, managedChannel);
        this.nonBlockStub = buildNonBlockingStub();
        this.blockStub = buildBlockingStub();
    }

    @Override
    public SocialContractGrpc.SocialContractBlockingStub buildBlockingStub() {
        return SocialContractGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public SocialContractGrpc.SocialContractStub buildNonBlockingStub() {
        return SocialContractGrpc.newStub(managedChannel);
    }

    @Override
    public SocialContractGrpc.SocialContractBlockingStub getBlockingStub() {
        return blockStub;
    }

    @Override
    public SocialContractGrpc.SocialContractStub getNonBlockingStub() {
        return nonBlockStub;
    }
}
