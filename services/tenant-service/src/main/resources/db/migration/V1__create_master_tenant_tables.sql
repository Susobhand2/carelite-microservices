CREATE SCHEMA IF NOT EXISTS master;

CREATE TABLE IF NOT EXISTS master.tenants (
    tenant_id VARCHAR(80) PRIMARY KEY,
    slug VARCHAR(60) NOT NULL UNIQUE,
    clinic_name VARCHAR(200) NOT NULL,
    schema_name VARCHAR(80) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    admin_email VARCHAR(200) NOT NULL,
    admin_name VARCHAR(200) NOT NULL,
    timezone VARCHAR(80) NOT NULL,
    cognito_group_prefix VARCHAR(120),
    s3_prefix VARCHAR(300) NOT NULL,
    provisioning_step VARCHAR(80),
    idempotency_key VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS master.tenant_provisioning_steps (
    tenant_id VARCHAR(80) NOT NULL,
    step_name VARCHAR(80) NOT NULL,
    status VARCHAR(30) NOT NULL,
    error_message TEXT,
    updated_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (tenant_id, step_name),
    CONSTRAINT fk_tenant_provisioning_steps_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES master.tenants (tenant_id)
);

