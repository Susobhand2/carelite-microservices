package com.carelite.tenant.exception;

public class TenantNotFoundException extends RuntimeException {
  public TenantNotFoundException(String tenantId) {
    super("Tenant not found: " + tenantId);
  }
}
