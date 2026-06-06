package com.carelite.tenant.service.provisioning;

import com.carelite.tenant.domain.Tenant;

public interface TenantStorageProvisioner {

  void provision(Tenant tenant);
}
