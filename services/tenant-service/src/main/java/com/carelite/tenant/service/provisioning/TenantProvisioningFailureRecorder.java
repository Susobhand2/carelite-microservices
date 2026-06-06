package com.carelite.tenant.service.provisioning;

import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.exception.TenantNotFoundException;
import com.carelite.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantProvisioningFailureRecorder {
  private final TenantRepository tenantRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markAndRecordFailure(String tenantId, String currentStep) {
    Tenant tenant =
        tenantRepository
            .findById(tenantId)
            .orElseThrow(() -> new TenantNotFoundException(tenantId));

    tenant.failProvisioning(currentStep);
    tenantRepository.save(tenant);
  }
}
