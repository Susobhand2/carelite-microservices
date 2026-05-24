package com.carelite.tenant.service;

import com.carelite.tenant.api.OnboardTenantRequest;

public interface RequestHashingService {
  public String hash(OnboardTenantRequest request);
}
