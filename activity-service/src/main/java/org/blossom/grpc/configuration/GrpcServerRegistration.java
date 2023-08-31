package org.blossom.grpc.configuration;

import org.blossom.grpc.server.GrpcServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.stereotype.Component;

@Component
public class GrpcServerRegistration implements BeanPostProcessor {
    @Autowired
    private EurekaServiceRegistry eurekaServiceRegistry;

    @Autowired
    private Registration registration;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof GrpcServer) {
            EurekaRegistration eurekaRegistration = (EurekaRegistration) registration;
            eurekaRegistration.getMetadata().put("grpc-port", String.valueOf(((GrpcServer) bean).getPort()));
            eurekaServiceRegistry.register(eurekaRegistration);
        }
        return bean;
    }

}