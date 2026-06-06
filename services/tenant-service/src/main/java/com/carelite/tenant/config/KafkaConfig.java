package com.carelite.tenant.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

  //    @Bean
  //    public DefaultErrorHandler kafkaErrorHandler() {
  //        DefaultErrorHandler handler = new DefaultErrorHandler(
  //                new FixedBackOff(30000L, 3L));
  //
  //        handler.addNotRetryableExceptions(NonRetryableProvisioningException.class);
  //        return handler;
  //    }
}
