package com.carelite.tenant.service.provisioning.impl;

import com.carelite.tenant.config.AwsProvisioningProperties;
import com.carelite.tenant.domain.Tenant;
import com.carelite.tenant.service.provisioning.TenantStorageProvisioner;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "carelite.provisioning.mode", havingValue = "aws")
public class S3TenantStorageProvisioner implements TenantStorageProvisioner {

  private final S3Client s3Client;
  private final AwsProvisioningProperties provisioningProperties;

  @Override
  public void provision(Tenant tenant) {
    var bucket = provisioningProperties.s3().bucket();
    if (!StringUtils.hasText(bucket)) {
      throw new IllegalStateException("carelite.aws.s3.bucket is required for AWS provisioning");
    }

    var s3Prefix = tenant.getS3Prefix();
    String markerKey = s3Prefix.endsWith("/") ? s3Prefix + ".keep" : s3Prefix + "/.keep";
    PutObjectRequest request =
        PutObjectRequest.builder().bucket(bucket).key(markerKey).contentType("text/plain").build();

    s3Client.putObject(
        request,
        RequestBody.fromString(
            "CareLite tenant storage marker for " + tenant.getTenantId(), StandardCharsets.UTF_8));
  }
}
