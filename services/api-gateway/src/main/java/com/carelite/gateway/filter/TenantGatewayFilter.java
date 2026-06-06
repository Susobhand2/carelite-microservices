package com.carelite.gateway.filter;

import com.carelite.common.CareLiteHeaders;
import com.carelite.security.CareLiteGroupParser;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TenantGatewayFilter implements GlobalFilter, Ordered {

  private static final String COGNITO_GROUPS_CLAIM = "cognito:groups";
  private static final String TENANT_MANAGEMENT_PATH = "/api/v1/tenants";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (isTenantManagementRoute(exchange)) {
      return forwardTenantManagementRequest(exchange, chain);
    }

    return exchange
        .getPrincipal()
        .cast(Authentication.class)
        .flatMap(authentication -> authorizeAndForward(exchange, chain, authentication))
        .switchIfEmpty(reject(exchange, HttpStatus.UNAUTHORIZED));
  }

  private boolean isTenantManagementRoute(ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().pathWithinApplication().value();
    return path.equals(TENANT_MANAGEMENT_PATH) || path.startsWith(TENANT_MANAGEMENT_PATH + "/");
  }

  private Mono<Void> forwardTenantManagementRequest(
      ServerWebExchange exchange, GatewayFilterChain chain) {
    String correlationId = correlationId(exchange);

    ServerWebExchange trustedExchange =
        exchange
            .mutate()
            .request(
                request ->
                    request.headers(
                        headers -> {
                          headers.remove(CareLiteHeaders.TENANT_ID);
                          headers.remove(CareLiteHeaders.CORRELATION_ID);
                          headers.add(CareLiteHeaders.CORRELATION_ID, correlationId);
                        }))
            .build();

    return chain.filter(trustedExchange);
  }

  private Mono<Void> authorizeAndForward(
      ServerWebExchange exchange, GatewayFilterChain chain, Authentication authentication) {

    if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
      return reject(exchange, HttpStatus.UNAUTHORIZED);
    }

    Jwt jwt = jwtAuthentication.getToken();

    String tenantId = exchange.getRequest().getHeaders().getFirst(CareLiteHeaders.TENANT_ID);

    if (tenantId == null || tenantId.isBlank()) {
      return reject(exchange, HttpStatus.BAD_REQUEST);
    }

    List<String> groups = jwt.getClaimAsStringList(COGNITO_GROUPS_CLAIM);

    if (!CareLiteGroupParser.hasTenantAccess(groups, tenantId)) {
      return reject(exchange, HttpStatus.FORBIDDEN);
    }

    String actorId = jwt.getSubject();
    String roles = rolesForTenant(groups, tenantId);
    String correlationId = correlationId(exchange);

    ServerWebExchange trustedExchange =
        exchange
            .mutate()
            .request(
                request ->
                    request.headers(
                        headers -> {
                          headers.remove(CareLiteHeaders.TENANT_ID);
                          headers.remove(CareLiteHeaders.ACTOR_ID);
                          headers.remove(CareLiteHeaders.ROLES);
                          headers.remove(CareLiteHeaders.CORRELATION_ID);

                          headers.add(CareLiteHeaders.TENANT_ID, tenantId.trim().toLowerCase());
                          headers.add(CareLiteHeaders.ACTOR_ID, actorId);
                          headers.add(CareLiteHeaders.ROLES, roles);
                          headers.add(CareLiteHeaders.CORRELATION_ID, correlationId);
                        }))
            .build();

    return chain.filter(trustedExchange);
  }

  private String rolesForTenant(Collection<String> groups, String tenantId) {
    if (CareLiteGroupParser.isSuperAdmin(groups)) {
      return "SUPER_ADMIN";
    }

    return CareLiteGroupParser.tenantMemberships(groups).stream()
        .filter(membership -> membership.tenantId().equals(tenantId.trim().toLowerCase()))
        .map(membership -> membership.role())
        .distinct()
        .reduce((left, right) -> left + "," + right)
        .orElse("");
  }

  private String correlationId(ServerWebExchange exchange) {
    String existing = exchange.getRequest().getHeaders().getFirst(CareLiteHeaders.CORRELATION_ID);

    if (existing != null && !existing.isBlank()) {
      return existing.trim();
    }

    return UUID.randomUUID().toString();
  }

  private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
    exchange.getResponse().setStatusCode(status);
    return exchange.getResponse().setComplete();
  }

  @Override
  public int getOrder() {
    return -100;
  }
}
