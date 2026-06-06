package com.carelite.tenant;

import com.carelite.tenant.config.AwsProvisioningProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableConfigurationProperties(AwsProvisioningProperties.class)
public class TenantServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TenantServiceApplication.class, args);
  }
}
