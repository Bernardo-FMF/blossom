package org.blossom.social;

import io.grpc.ManagedChannel;
import org.blossom.activitycontract.ActivityContractGrpc;
import org.blossom.facade.BaseGrpcClientFacade;
import org.blossom.facade.IGrpcConfiguration;
import org.springframework.stereotype.Component;

@Component
public class MockGrpcClient extends BaseGrpcClientFacade {
    private final ActivityContractGrpc.ActivityContractStub nonBlockStub;
    private final ActivityContractGrpc.ActivityContractBlockingStub blockStub;

    public MockGrpcClient(IGrpcConfiguration grpcConfiguration, ManagedChannel managedChannel) {
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
