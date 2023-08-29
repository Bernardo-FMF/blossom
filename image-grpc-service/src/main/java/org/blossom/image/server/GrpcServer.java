package org.blossom.image.server;

import io.grpc.BindableService;
import lombok.extern.log4j.Log4j2;
import org.blossom.image.configuration.GrpcConfiguration;
import org.blossom.image.service.ImageService;
import org.blossom.server.BaseGrpcServerFacade;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class GrpcServer extends BaseGrpcServerFacade {
    public GrpcServer(GrpcConfiguration grpcConfiguration, ImageService imageService) {
        super(grpcConfiguration, new BindableService[] {imageService});
    }
}