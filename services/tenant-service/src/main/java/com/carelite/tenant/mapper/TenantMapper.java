package com.carelite.tenant.mapper;

import com.carelite.tenant.api.OnboardTenantResponse;
import com.carelite.tenant.domain.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TenantMapper {
  @Mapping(source = "tenantId", target = "tenantId")
  @Mapping(source = "slug", target = "slug")
  @Mapping(source = "clinicName", target = "clinicName")
  @Mapping(source = "schemaName", target = "schemaName")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "adminEmail", target = "adminEmail")
  @Mapping(source = "adminName", target = "adminName")
  @Mapping(source = "timezone", target = "timezone")
  @Mapping(source = "s3Prefix", target = "s3Prefix")
  OnboardTenantResponse maptoTenantResponse(Tenant tenant);
}
