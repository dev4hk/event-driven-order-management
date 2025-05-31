package org.example.shippingservice.config;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.example.shippingservice.command.interceptor.ShippingCommandInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShippingServiceConfig {

    @Autowired
    public void registerShippingCommandInterceptor(ApplicationContext context, CommandGateway commandGateway) {
        commandGateway.registerDispatchInterceptor(context.getBean(ShippingCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("shipping-group",
                conf -> PropagatingErrorHandler.instance());
    }

}
