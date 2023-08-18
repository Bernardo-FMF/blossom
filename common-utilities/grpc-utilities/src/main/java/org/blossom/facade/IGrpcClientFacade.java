package org.blossom.facade;

public interface IGrpcClientFacade {
    void start();
    void stop() throws InterruptedException;
}
