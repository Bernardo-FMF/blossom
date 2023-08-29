package org.blossom.grpc.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GrpcServerRunner implements ApplicationRunner {

    @Autowired
    private GrpcServer grpcServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        grpcServer.start();
        grpcServer.blockUntilShutdown();
    }

}