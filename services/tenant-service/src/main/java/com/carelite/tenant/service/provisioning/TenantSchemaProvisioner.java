package com.carelite.tenant.service.provisioning;

import com.carelite.tenant.domain.Tenant;

public interface TenantSchemaProvisioner {

  void provision(Tenant tenant);
}
