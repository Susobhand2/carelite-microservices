package com.carelite.tenant.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(name = "carelite.provisioning.mode", havingValue = "aws")
public class AwsProvisioningConfig {

  @Bean
  public S3Client s3Client(AwsProvisioningProperties properties) {
    return S3Client.builder().region(Region.of(properties.region())).build();
  }

  @Bean
  public CognitoIdentityProviderClient cognitoIdentityProviderClient(
      AwsProvisioningProperties properties) {
    return CognitoIdentityProviderClient.builder().region(Region.of(properties.region())).build();
  }
}
