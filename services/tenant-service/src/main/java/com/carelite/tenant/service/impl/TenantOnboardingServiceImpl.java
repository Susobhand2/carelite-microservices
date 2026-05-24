package com.carelite.tenant.service.impl;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.api.OnboardTenantResponse;
import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.exception.DuplicateTenantSlugException;
import com.carelite.tenant.exception.IdempotencyConflictException;
import com.carelite.tenant.mapper.TenantMapper;
import com.carelite.tenant.repository.TenantRepository;
import com.carelite.tenant.service.RequestHashingService;
import com.carelite.tenant.service.TenantOnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantOnboardingServiceImpl implements TenantOnboardingService {
  private final TenantRepository tenantRepository;
  private final JdbcTemplate jdbcTemplate;
  private final TenantMapper mapper;
  private final RequestHashingService requestHashingService;

  @Transactional
  public OnboardTenantResponse onboard(OnboardTenantRequest request, String idempotencyKey) {
    String normalizedSlug = request.getSlug().trim().toLowerCase();
    String schemaName = "clinic_" + normalizedSlug.replace("-", "_");
    String requestHash = requestHashingService.hash(request);

    return tenantRepository
        .findByIdempotencyKey(idempotencyKey)
        .map(
            tenant -> {
              if (!tenant.getRequestHash().equals(requestHash)) {
                throw new IdempotencyConflictException();
              }
              return OnboardTenantResponse.from(tenant);
            })
        .orElseGet(
            () ->
                createTenant(
                    request, idempotencyKey, normalizedSlug, schemaName, schemaName, requestHash));
  }

  private OnboardTenantResponse createTenant(
      OnboardTenantRequest request,
      String idempotencyKey,
      String slug,
      String schemaName,
      String tenantId,
      String requestHash) {
    if (tenantRepository.existsBySlug(slug)) {
      throw new DuplicateTenantSlugException(slug);
    }

    Tenant tenant =
        new Tenant(
            tenantId,
            slug,
            request.getClinicName().trim(),
            schemaName,
            request.getAdminEmail().trim().toLowerCase(),
            request.getAdminName().trim(),
            request.getTimezone().trim(),
            idempotencyKey,
            requestHash);

    tenantRepository.saveAndFlush(tenant);

    jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

    tenant.activate();

    // return OnboardTenantResponse.from(tenantRepository.save(tenant));
    return mapper.maptoTenantResponse(tenantRepository.save(tenant));
  }
}
