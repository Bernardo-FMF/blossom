package org.blossom.grpc.server;

import io.grpc.BindableService;
import org.blossom.grpc.configuration.GrpcConfiguration;
import org.blossom.grpc.service.ActivityGrpcService;
import org.blossom.server.BaseGrpcServerFacade;
import org.springframework.stereotype.Component;

@Component
public class GrpcServer extends BaseGrpcServerFacade {
    public GrpcServer(GrpcConfiguration grpcConfiguration, ActivityGrpcService activityService) {
        super(grpcConfiguration, new BindableService[] {activityService});
    }
}