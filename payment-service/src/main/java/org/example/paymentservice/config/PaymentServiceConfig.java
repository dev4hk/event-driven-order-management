package org.example.paymentservice.config;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.example.paymentservice.command.interceptor.PaymentCommandInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentServiceConfig {

    @Autowired
    public void registerPaymentCommandInterceptor(ApplicationContext context, CommandGateway commandGateway) {
        commandGateway.registerDispatchInterceptor(context.getBean(PaymentCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("payment-group",
                conf -> PropagatingErrorHandler.instance());
    }

}
