package com.carelite.common;

public final class CareLiteHeaders {

    public static final String TENANT_ID = "X-Tenant-ID";
    public static final String ACTOR_ID = "X-Actor-ID";
    public static final String ROLES = "X-Roles";
    public static final String CORRELATION_ID = "X-Correlation-ID";
    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private CareLiteHeaders() {
    }
}
