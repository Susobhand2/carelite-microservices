package com.carelite.tenant.api;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OnboardTenantRequest {
  @NotBlank
  @Size(max = 200)
  private String clinicName;

  @NotBlank
  @Pattern(
      regexp = "^[a-z][a-z0-9-]{2,59}$",
      message =
          "must start with a lowercase letter and contain only lowercase letters, numbers, and hyphens")
  private String slug;

  @NotBlank
  @Email
  @Size(max = 200)
  private String adminEmail;

  @NotBlank
  @Size(max = 200)
  private String adminName;

  @NotBlank
  @Size(max = 80)
  private String timezone;
}
