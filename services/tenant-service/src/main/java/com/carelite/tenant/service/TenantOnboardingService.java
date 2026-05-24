package com.carelite.tenant.service;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.api.OnboardTenantResponse;

public interface TenantOnboardingService {

  OnboardTenantResponse onboard(OnboardTenantRequest request, String idempotencyKey);
}
