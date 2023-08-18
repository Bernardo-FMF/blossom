package org.blossom.facade;

import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;

public interface IStubAccessor {
    AbstractBlockingStub<?> buildBlockingStub();

    AbstractAsyncStub<?> buildNonBlockingStub();

    AbstractBlockingStub<?> getBlockingStub();

    AbstractAsyncStub<?> getNonBlockingStub();

}
