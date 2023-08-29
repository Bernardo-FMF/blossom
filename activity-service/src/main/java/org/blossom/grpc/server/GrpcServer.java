package org.blossom.grpc.server;

import io.grpc.BindableService;
import lombok.extern.log4j.Log4j2;
import org.blossom.grpc.configuration.GrpcConfiguration;
import org.blossom.grpc.service.ActivityGrpcService;
import org.blossom.server.BaseGrpcServerFacade;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class GrpcServer extends BaseGrpcServerFacade {
    public GrpcServer(GrpcConfiguration grpcConfiguration, ActivityGrpcService activityService) {
        super(grpcConfiguration, new BindableService[] {activityService});
    }
}