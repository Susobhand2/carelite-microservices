package com.carelite.clinic.api;

import com.carelite.tenancy.TenantContextHolder;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TenantDebugController {

  @GetMapping("/api/v1/tenant-context")
  public Map<String, String> tenantContext() {
    return Map.of("tenantId", TenantContextHolder.requireTenantId());
  }
}
