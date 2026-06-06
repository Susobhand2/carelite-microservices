package com.carelite.tenant.service.provisioning.impl;

import com.carelite.tenant.config.AwsProvisioningProperties;
import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.exception.NonRetryableProvisioningException;
import com.carelite.tenant.service.provisioning.TenantCognitoProvisioner;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "carelite.provisioning.mode", havingValue = "aws")
public class AwsTenantCognitoProvisioner implements TenantCognitoProvisioner {

  private static final List<String> TENANT_ROLES =
      List.of("CLINIC_ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT");

  private final CognitoIdentityProviderClient client;
  private final AwsProvisioningProperties properties;

  @Override
  public void provision(Tenant tenant) {
    String userPoolId = properties.cognito().userPoolId();
    if (!StringUtils.hasText(userPoolId)) {
      throw new IllegalStateException(
          "carelite.aws.cognito.user-pool-id is required for AWS provisioning");
    }

    for (String role : TENANT_ROLES) {
      createGroupIfMissing(userPoolId, tenant.getTenantId() + "_" + role);
    }

    createAdminIfMissing(userPoolId, tenant);
    addAdminToGroup(userPoolId, tenant);
  }

  private void addAdminToGroup(String userPoolId, Tenant tenant) {
    try {
      client.adminAddUserToGroup(
          AdminAddUserToGroupRequest.builder()
              .userPoolId(userPoolId)
              .username(tenant.getAdminEmail())
              .groupName(tenant.getTenantId() + "_CLINIC_ADMIN")
              .build());
    } catch (InvalidParameterException | ResourceNotFoundException ex) {
      throw new NonRetryableProvisioningException(
          "Invalid Cognito admin group assignment for " + tenant.getAdminEmail(), ex);
    }
  }

  private void createAdminIfMissing(String userPoolId, Tenant tenant) {
    try {
      client.adminCreateUser(
          AdminCreateUserRequest.builder()
              .userPoolId(userPoolId)
              .username(tenant.getAdminEmail())
              .userAttributes(
                  AttributeType.builder().name("email").value(tenant.getAdminEmail()).build(),
                  AttributeType.builder().name("email_verified").value("true").build(),
                  AttributeType.builder().name("name").value(tenant.getAdminName()).build())
              .build());
    } catch (UsernameExistsException ignored) {
      log.info(
          "Cognito admin user already exists, skipping user creation: {}", tenant.getAdminEmail());
    } catch (InvalidParameterException | ResourceNotFoundException ex) {
      throw new NonRetryableProvisioningException(
          "Invalid Cognito admin user provisioning configuration for " + tenant.getAdminEmail(),
          ex);
    }
  }

  private void createGroupIfMissing(String userPoolId, String group) {
    try {
      client.createGroup(
          CreateGroupRequest.builder().userPoolId(userPoolId).groupName(group).build());
    } catch (GroupExistsException ignored) {
      log.info("Cognito group already exists, skipping group creation: {}", group);
    } catch (InvalidParameterException | ResourceNotFoundException ex) {
      throw new NonRetryableProvisioningException(
          "Invalid Cognito group provisioning configuration for group " + group, ex);
    }
  }
}
