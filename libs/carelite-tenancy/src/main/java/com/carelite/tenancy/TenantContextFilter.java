package com.carelite.tenancy;

import com.carelite.common.CareLiteHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantContextFilter extends OncePerRequestFilter {

  private static final Set<String> EXCLUDED_PREFIXES =
      Set.of("/actuator", "/swagger-ui", "/v3/api-docs");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (isExcluded(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String tenantId = request.getHeader(CareLiteHeaders.TENANT_ID);
    if (tenantId == null || tenantId.isBlank()) {
      response.sendError(
          HttpStatus.BAD_REQUEST.value(), CareLiteHeaders.TENANT_ID + " is required");
      return;
    }

    try {
      TenantContextHolder.setTenantId(tenantId.trim().toLowerCase());
      filterChain.doFilter(request, response);
    } finally {
      TenantContextHolder.clear();
    }
  }

  private boolean isExcluded(HttpServletRequest request) {
    String path = request.getRequestURI();
    return EXCLUDED_PREFIXES.stream().anyMatch(path::startsWith);
  }
}
