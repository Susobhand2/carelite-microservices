package com.carelite.tenant.api.rest;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.api.OnboardTenantResponse;
import com.carelite.tenant.service.TenantOnboardingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

  private final TenantOnboardingService service;

  @PostMapping("/onboard")
  public ResponseEntity<OnboardTenantResponse> onboardTenant(
      @Valid @RequestBody OnboardTenantRequest request,
      @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey) {
    OnboardTenantResponse response = service.onboard(request, idempotencyKey.trim());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
