package com.carelite.clinic.config;

import com.carelite.tenancy.TenantContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantContextConfig {

  @Bean
  public TenantContextFilter tenantContextFilter() {
    return new TenantContextFilter();
  }
}
