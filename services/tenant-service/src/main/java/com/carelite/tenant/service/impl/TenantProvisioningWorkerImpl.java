package com.carelite.tenant.service.impl;

import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.exception.NonRetryableProvisioningException;
import com.carelite.tenant.exception.TenantNotFoundException;
import com.carelite.tenant.repository.TenantRepository;
import com.carelite.tenant.service.TenantProvisioningWorker;
import com.carelite.tenant.service.provisioning.TenantCognitoProvisioner;
import com.carelite.tenant.service.provisioning.TenantProvisioningFailureRecorder;
import com.carelite.tenant.service.provisioning.TenantSchemaProvisioner;
import com.carelite.tenant.service.provisioning.TenantStorageProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;

@Service
@RequiredArgsConstructor
public class TenantProvisioningWorkerImpl implements TenantProvisioningWorker {

  private final TenantRepository tenantRepository;
  private final TenantSchemaProvisioner schemaProvisioner;
  private final TenantCognitoProvisioner cognitoProvisioner;
  private final TenantStorageProvisioner storageProvisioner;
  private final TenantProvisioningFailureRecorder recorder;

  @Override
  @Transactional
  public void provision(String tenantId) {
    Tenant tenant =
        tenantRepository
            .findById(tenantId)
            .orElseThrow(() -> new TenantNotFoundException(tenantId));

    String currentStep = "SCHEMA";
    try {
      tenant.markProvisioningStep(currentStep);
      schemaProvisioner.provision(tenant);

      currentStep = "COGNITO";
      tenant.markProvisioningStep(currentStep);
      cognitoProvisioner.provision(tenant);

      currentStep = "S3";
      tenant.markProvisioningStep(currentStep);
      storageProvisioner.provision(tenant);

      tenant.activate();
    } catch (InvalidParameterException ex) {
      recorder.markAndRecordFailure(tenantId, currentStep);
      throw new NonRetryableProvisioningException("Invalid Cognito user pool schema", ex);
    } catch (Exception ex) {
      recorder.markAndRecordFailure(tenantId, currentStep);
      throw ex;
    }
  }
}
