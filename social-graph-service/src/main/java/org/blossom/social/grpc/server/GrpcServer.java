package org.blossom.social.grpc.server;

import io.grpc.BindableService;
import lombok.extern.log4j.Log4j2;
import org.blossom.social.grpc.configuration.GrpcConfiguration;
import org.blossom.social.grpc.service.SocialGrpcService;
import org.blossom.server.BaseGrpcServerFacade;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class GrpcServer extends BaseGrpcServerFacade {
    public GrpcServer(GrpcConfiguration grpcConfiguration, SocialGrpcService socialService) {
        super(grpcConfiguration, new BindableService[] {socialService});
    }
}