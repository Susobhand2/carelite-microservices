package com.carelite.tenant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "carelite.aws")
public record AwsProvisioningProperties(String region, S3Properties s3, CognitoProperties cognito) {
  public record S3Properties(String bucket) {}

  public record CognitoProperties(String userPoolId) {}
}
