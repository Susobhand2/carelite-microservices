package com.carelite.tenant.service.impl;

import com.carelite.tenant.api.OnboardTenantRequest;
import com.carelite.tenant.service.RequestHashingService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.stereotype.Service;

@Service
public class RequestHashingServiceImpl implements RequestHashingService {
  @Override
  public String hash(OnboardTenantRequest request) {
    String canonical =
        String.join(
            "|",
            normalize(request.getClinicName()),
            normalize(request.getSlug()).toLowerCase(),
            normalize(request.getAdminEmail()).toLowerCase(),
            normalize(request.getAdminName()),
            normalize(request.getTimezone()));

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(canonical.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("SHA-256 is not available", ex);
    }
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim();
  }
}
