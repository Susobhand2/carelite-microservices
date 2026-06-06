package com.carelite.tenant.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "tenants", schema = "master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Tenant {

  @Id
  @Column(name = "tenant_id", length = 80)
  private String tenantId;

  @Column(nullable = false, unique = true, length = 60)
  private String slug;

  @Column(name = "clinic_name", nullable = false, length = 200)
  private String clinicName;

  @Column(name = "schema_name", nullable = false, unique = true, length = 80)
  private String schemaName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private TenantStatus status;

  @Column(name = "admin_email", nullable = false, length = 200)
  private String adminEmail;

  @Column(name = "admin_name", nullable = false, length = 200)
  private String adminName;

  @Column(nullable = false, length = 80)
  private String timezone;

  @Column(name = "cognito_group_prefix", length = 120)
  private String cognitoGroupPrefix;

  @Column(name = "s3_prefix", nullable = false, length = 300)
  private String s3Prefix;

  @Column(name = "provisioning_step", length = 80)
  private String provisioningStep;

  @Column(name = "idempotency_key", nullable = false, unique = true, length = 120)
  private String idempotencyKey;

  @Column(name = "request_hash", nullable = false, length = 64)
  private String requestHash;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false, length = 120)
  private String createdBy;

  @LastModifiedBy
  @Column(name = "updated_by", nullable = false, length = 120)
  private String updatedBy;

  public Tenant(
      String tenantId,
      String slug,
      String clinicName,
      String schemaName,
      String adminEmail,
      String adminName,
      String timezone,
      String idempotencyKey,
      String requestHash) {
    Instant now = Instant.now();
    this.tenantId = tenantId;
    this.slug = slug;
    this.clinicName = clinicName;
    this.schemaName = schemaName;
    this.status = TenantStatus.PROVISIONING;
    this.adminEmail = adminEmail;
    this.adminName = adminName;
    this.timezone = timezone;
    this.cognitoGroupPrefix = schemaName;
    this.s3Prefix = "tenants/" + schemaName + "/";
    this.provisioningStep = "MASTER_ROW";
    this.idempotencyKey = idempotencyKey;
    this.requestHash = requestHash;
  }

  public void activate() {
    this.status = TenantStatus.ACTIVE;
    this.provisioningStep = "ACTIVATE";
  }

  public void suspend() {
    this.status = TenantStatus.SUSPENDED;
    this.provisioningStep = "SUSPEND";
  }

  public void retryProvisioning() {
    this.status = TenantStatus.PROVISIONING;
    this.provisioningStep = "RETRY";
  }

  public void markProvisioningStep(String step) {
    this.provisioningStep = step;
  }

  public void failProvisioning(String step) {
    this.status = TenantStatus.FAILED;
    this.provisioningStep = step;
  }
}
