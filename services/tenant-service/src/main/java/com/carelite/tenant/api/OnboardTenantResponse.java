package com.carelite.tenant.api;

import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.domain.TenantStatus;

public record OnboardTenantResponse(
    String tenantId,
    String slug,
    String clinicName,
    String schemaName,
    TenantStatus status,
    String adminEmail,
    String adminName,
    String timezone,
    String s3Prefix) {
  public static OnboardTenantResponse from(Tenant tenant) {
    return new OnboardTenantResponse(
        tenant.getTenantId(),
        tenant.getSlug(),
        tenant.getClinicName(),
        tenant.getSchemaName(),
        tenant.getStatus(),
        tenant.getAdminEmail(),
        tenant.getAdminName(),
        tenant.getTimezone(),
        tenant.getS3Prefix());
  }
}
