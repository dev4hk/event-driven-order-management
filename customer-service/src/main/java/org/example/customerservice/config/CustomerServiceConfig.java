package org.example.customerservice.config;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.customerservice.command.interceptor.CustomerCommandInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerServiceConfig {

    @Autowired
    public void registerCustomerCommandInterceptor(ApplicationContext context, CommandGateway commandGateway) {
        commandGateway.registerDispatchInterceptor(context.getBean(CustomerCommandInterceptor.class));
    }

}
