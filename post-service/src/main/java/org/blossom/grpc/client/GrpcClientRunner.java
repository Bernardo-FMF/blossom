package org.blossom.grpc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientRunner implements ApplicationRunner {
    @Autowired
    private ImageContractGrpcClientFacade grpcClient;

    @Override
    public void run(ApplicationArguments args) {
        grpcClient.start();
    }
}