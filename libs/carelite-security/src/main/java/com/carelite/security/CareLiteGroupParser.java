package com.carelite.security;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class CareLiteGroupParser {

  private CareLiteGroupParser() {}

  public static boolean isSuperAdmin(Collection<String> groups) {
    if (groups == null) {
      return false;
    }

    return groups.stream().filter(Objects::nonNull).anyMatch(CareLiteRoles.SUPER_ADMIN::equals);
  }

  public static List<TenantRoleMembership> tenantMemberships(Collection<String> groups) {
    if (groups == null) {
      return List.of();
    }

    return groups.stream()
        .filter(Objects::nonNull)
        .map(CareLiteGroupParser::parseTenantMembership)
        .filter(Objects::nonNull)
        .toList();
  }

  public static boolean hasTenantAccess(Collection<String> groups, String tenantId) {
    if (isSuperAdmin(groups)) {
      return true;
    }

    if (tenantId == null || tenantId.isBlank()) {
      return false;
    }

    String normalizedTenantId = tenantId.trim().toLowerCase(Locale.ROOT);

    return tenantMemberships(groups).stream()
        .anyMatch(membership -> membership.tenantId().equals(normalizedTenantId));
  }

  private static TenantRoleMembership parseTenantMembership(String group) {
    String normalizedGroup = group.trim();

    if (normalizedGroup.isBlank() || CareLiteRoles.SUPER_ADMIN.equals(normalizedGroup)) {
      return null;
    }

    String role = extractRole(normalizedGroup);

    if (role == null) {
      return null;
    }

    String suffix = "_" + role;
    String tenantId = normalizedGroup.substring(0, normalizedGroup.length() - suffix.length());

    if (tenantId.isBlank()) {
      return null;
    }

    return new TenantRoleMembership(tenantId.toLowerCase(Locale.ROOT), role);
  }

  private static String extractRole(String group) {
    return switch (group) {
      case String s when s.endsWith("_" + CareLiteRoles.CLINIC_ADMIN) -> CareLiteRoles.CLINIC_ADMIN;
      case String s when s.endsWith("_" + CareLiteRoles.RECEPTIONIST) -> CareLiteRoles.RECEPTIONIST;
      case String s when s.endsWith("_" + CareLiteRoles.DOCTOR) -> CareLiteRoles.DOCTOR;
      case String s when s.endsWith("_" + CareLiteRoles.PATIENT) -> CareLiteRoles.PATIENT;
      default -> null;
    };
  }
}
