package com.carelite.tenant.exception;

public class NonRetryableProvisioningException extends RuntimeException {
  public NonRetryableProvisioningException(String message, Throwable cause) {
    super(message, cause);
  }
}
