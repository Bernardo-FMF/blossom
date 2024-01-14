package org.blossom.activity.grpc.server;

import io.grpc.BindableService;
import org.blossom.activity.configuration.GrpcConfiguration;
import org.blossom.activity.grpc.service.ActivityGrpcService;
import org.blossom.server.BaseGrpcServerFacade;
import org.springframework.stereotype.Component;

@Component
public class GrpcServer extends BaseGrpcServerFacade {
    public GrpcServer(GrpcConfiguration grpcConfiguration, ActivityGrpcService activityService) {
        super(grpcConfiguration, new BindableService[] {activityService});
    }
}