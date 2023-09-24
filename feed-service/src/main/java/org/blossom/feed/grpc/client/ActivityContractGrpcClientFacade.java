package org.blossom.feed.grpc.client;

import io.grpc.ManagedChannel;
import org.blossom.activitycontract.ActivityContractGrpc;
import org.blossom.facade.BaseGrpcClientFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ActivityContractGrpcClientFacade extends BaseGrpcClientFacade {
    private final ActivityContractGrpc.ActivityContractStub nonBlockStub;
    private final ActivityContractGrpc.ActivityContractBlockingStub blockStub;

    public ActivityContractGrpcClientFacade(GrpcActivityConfiguration grpcConfiguration, @Qualifier("activity-grpc-channel") ManagedChannel managedChannel) {
        super(grpcConfiguration, managedChannel);
        this.nonBlockStub = buildNonBlockingStub();
        this.blockStub = buildBlockingStub();
    }

    @Override
    public ActivityContractGrpc.ActivityContractBlockingStub buildBlockingStub() {
        return ActivityContractGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public ActivityContractGrpc.ActivityContractStub buildNonBlockingStub() {
        return ActivityContractGrpc.newStub(managedChannel);
    }

    @Override
    public ActivityContractGrpc.ActivityContractBlockingStub getBlockingStub() {
        return blockStub;
    }

    @Override
    public ActivityContractGrpc.ActivityContractStub getNonBlockingStub() {
        return nonBlockStub;
    }
}
