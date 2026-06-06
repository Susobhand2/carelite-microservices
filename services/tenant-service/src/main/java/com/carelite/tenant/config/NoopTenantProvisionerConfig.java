package com.carelite.tenant.config;

import com.carelite.tenant.service.provisioning.TenantCognitoProvisioner;
import com.carelite.tenant.service.provisioning.TenantStorageProvisioner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NoopTenantProvisionerConfig {

  @Bean
  @ConditionalOnMissingBean(TenantStorageProvisioner.class)
  public TenantStorageProvisioner noopTenantStorageProvisioner() {
    return tenant -> {};
  }

  @Bean
  @ConditionalOnMissingBean(TenantCognitoProvisioner.class)
  public TenantCognitoProvisioner noopTenantCognitoProvisioner() {
    return tenant -> {};
  }
}
