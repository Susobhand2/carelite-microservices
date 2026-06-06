package com.carelite.tenant.service.provisioning.impl;

import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.service.provisioning.TenantCognitoProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoopTenantCognitoProvisioner implements TenantCognitoProvisioner {

  @Override
  public void provision(Tenant tenant) {}
}
