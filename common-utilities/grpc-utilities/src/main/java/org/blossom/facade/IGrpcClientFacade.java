package org.blossom.facade;

public interface IGrpcClientFacade extends IStubAccessor {
    void start();
    void stop() throws InterruptedException;
}
