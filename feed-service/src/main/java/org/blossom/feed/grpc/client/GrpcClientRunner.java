package org.blossom.feed.grpc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientRunner implements ApplicationRunner {
    @Autowired
    private SocialContractGrpcClientFacade socialGrpcClient;

    @Autowired
    private ActivityContractGrpcClientFacade activityGrpcClient;

    @Override
    public void run(ApplicationArguments args) {
        socialGrpcClient.start();
        activityGrpcClient.start();
    }
}