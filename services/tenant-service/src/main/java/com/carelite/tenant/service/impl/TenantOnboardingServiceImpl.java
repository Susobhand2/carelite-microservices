package com.carelite.tenant.service.impl;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.api.OnboardTenantResponse;
import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.domain.TenantStatus;
import com.carelite.tenant.events.TenantEventPublisher;
import com.carelite.tenant.exception.DuplicateTenantSlugException;
import com.carelite.tenant.exception.IdempotencyConflictException;
import com.carelite.tenant.exception.TenantLifecycleException;
import com.carelite.tenant.exception.TenantNotFoundException;
import com.carelite.tenant.mapper.TenantMapper;
import com.carelite.tenant.repository.TenantRepository;
import com.carelite.tenant.service.RequestHashingService;
import com.carelite.tenant.service.TenantOnboardingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantOnboardingServiceImpl implements TenantOnboardingService {
  private final TenantRepository tenantRepository;
  private final TenantMapper mapper;
  private final RequestHashingService requestHashingService;
  private final TenantEventPublisher tenantEventPublisher;

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

    Tenant savedTenant = tenantRepository.saveAndFlush(tenant);
    tenantEventPublisher.publishProvisioningRequested(savedTenant);
    return mapper.maptoTenantResponse(savedTenant);
  }

  @Transactional(readOnly = true)
  public List<OnboardTenantResponse> listTenants() {
    return tenantRepository.findAll().stream().map(mapper::maptoTenantResponse).toList();
  }

  @Transactional(readOnly = true)
  public OnboardTenantResponse getTenant(String tenantIdOrSlug, Jwt jwt) {
    String normalizedIdentifier = tenantIdOrSlug.trim().toLowerCase();
    Tenant tenant =
        tenantRepository
            .findById(normalizedIdentifier)
            .or(() -> tenantRepository.findBySlug(normalizedIdentifier))
            .orElseThrow(() -> new TenantNotFoundException(tenantIdOrSlug));

    if (!canViewTenant(tenant, jwt)) {
      throw new AccessDeniedException("Not allowed to view tenant " + tenant.getTenantId());
    }

    return mapper.maptoTenantResponse(tenant);
  }

  private boolean canViewTenant(Tenant tenant, Jwt jwt) {
    List<String> groups = jwt.getClaimAsStringList("cognito:groups");
    if (groups == null) {
      return false;
    }

    return groups.contains("SUPER_ADMIN")
        || groups.contains(tenant.getTenantId() + "_CLINIC_ADMIN");
  }

  @Transactional
  public OnboardTenantResponse suspendTenant(String tenantId) {
    Tenant tenant = findTenantById(tenantId);
    tenant.suspend();
    return mapper.maptoTenantResponse(tenantRepository.save(tenant));
  }

  @Transactional
  public OnboardTenantResponse activateTenant(String tenantId) {
    Tenant tenant = findTenantById(tenantId);
    tenant.activate();
    return mapper.maptoTenantResponse(tenantRepository.save(tenant));
  }

  @Transactional
  public OnboardTenantResponse retryProvisioning(String tenantId) {
    Tenant tenant = findTenantById(tenantId);
    if (tenant.getStatus() != TenantStatus.FAILED) {
      throw new TenantLifecycleException(
          "Only FAILED tenants can retry provisioning: " + tenant.getTenantId());
    }

    tenant.retryProvisioning();
    Tenant savedTenant = tenantRepository.saveAndFlush(tenant);
    tenantEventPublisher.publishProvisioningRequested(savedTenant);
    return mapper.maptoTenantResponse(savedTenant);
  }

  private Tenant findTenantById(String tenantId) {
    String normalizedTenantId = tenantId.trim().toLowerCase();
    return tenantRepository
        .findById(normalizedTenantId)
        .orElseThrow(() -> new TenantNotFoundException(tenantId));
  }
}
