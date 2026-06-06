package com.carelite.tenant.api.rest;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.api.OnboardTenantResponse;
import com.carelite.tenant.service.TenantOnboardingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

  private final TenantOnboardingService service;

  @PostMapping("/onboard")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  public ResponseEntity<OnboardTenantResponse> onboardTenant(
      @Valid @RequestBody OnboardTenantRequest request,
      @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey) {
    OnboardTenantResponse response = service.onboard(request, idempotencyKey.trim());
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  public ResponseEntity<List<OnboardTenantResponse>> listTenants() {
    return ResponseEntity.ok(service.listTenants());
  }

  @GetMapping("/{tenantIdOrSlug}")
  public ResponseEntity<OnboardTenantResponse> getTenant(
      @PathVariable String tenantIdOrSlug, @AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.ok(service.getTenant(tenantIdOrSlug, jwt));
  }

  @PostMapping("/{tenantId}/suspend")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  public ResponseEntity<OnboardTenantResponse> suspendTenant(@PathVariable String tenantId) {
    return ResponseEntity.ok(service.suspendTenant(tenantId));
  }

  @PostMapping("/{tenantId}/activate")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  public ResponseEntity<OnboardTenantResponse> activateTenant(@PathVariable String tenantId) {
    return ResponseEntity.ok(service.activateTenant(tenantId));
  }

  @PostMapping("/{tenantId}/retry")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  public ResponseEntity<OnboardTenantResponse> retryProvisioning(@PathVariable String tenantId) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.retryProvisioning(tenantId));
  }
}
