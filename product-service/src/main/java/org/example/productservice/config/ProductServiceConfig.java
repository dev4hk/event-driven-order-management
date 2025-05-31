package org.example.productservice.config;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.example.productservice.command.interceptor.ProductCommandInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductServiceConfig {

    @Autowired
    public void registerProductCommandInterceptor(ApplicationContext context, CommandGateway commandGateway) {
        commandGateway.registerDispatchInterceptor(context.getBean(ProductCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("product-group",
                conf -> PropagatingErrorHandler.instance());
    }

}
