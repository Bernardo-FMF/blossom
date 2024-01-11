package org.blossom.post.grpc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientRunner implements ApplicationRunner {
    @Autowired
    private ImageContractGrpcClientFacade imageGrpcClient;

    @Autowired
    private ActivityContractGrpcClientFacade activityGrpcClient;

    @Override
    public void run(ApplicationArguments args) {
        imageGrpcClient.start();
        activityGrpcClient.start();
    }
}