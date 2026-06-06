package com.carelite.tenant.service.provisioning.impl;

import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.service.provisioning.TenantStorageProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "carelite.provisioning.mode",
        havingValue = "noop",
        matchIfMissing = true)
public class NoopTenantStorageProvisioner implements TenantStorageProvisioner {

  @Override
  public void provision(Tenant tenant) {}
}
