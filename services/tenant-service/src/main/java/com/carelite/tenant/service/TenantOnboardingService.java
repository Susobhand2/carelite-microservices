package com.carelite.tenant.service;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.api.OnboardTenantResponse;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;

public interface TenantOnboardingService {

  OnboardTenantResponse onboard(OnboardTenantRequest request, String idempotencyKey);

  List<OnboardTenantResponse> listTenants();

  OnboardTenantResponse getTenant(String tenantIdOrSlug, Jwt jwt);

  OnboardTenantResponse suspendTenant(String tenantId);

  OnboardTenantResponse activateTenant(String tenantId);

  OnboardTenantResponse retryProvisioning(String tenantId);
}
