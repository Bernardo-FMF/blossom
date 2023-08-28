package org.blossom.facade;

public interface IGrpcConfiguration {
    String getServerName();

    int getClientAwaitTerminationInSeconds();
}
