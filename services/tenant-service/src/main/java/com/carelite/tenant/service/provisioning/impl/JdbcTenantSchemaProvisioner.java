package com.carelite.tenant.service.provisioning.impl;

import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.service.provisioning.TenantSchemaProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JdbcTenantSchemaProvisioner implements TenantSchemaProvisioner {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void provision(Tenant tenant) {
    String schemaName = tenant.getSchemaName();

    if (!schemaName.matches("^[a-z][a-z0-9_]{2,79}$")) {
      throw new IllegalArgumentException("Invalid tenant schema name: " + schemaName);
    }

    jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
  }
}
