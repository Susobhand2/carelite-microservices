package com.carelite.tenant.exception;

public class DuplicateTenantSlugException extends RuntimeException {
  public DuplicateTenantSlugException(String slug) {
    super("Tenant slug already exists: " + slug);
  }
}
