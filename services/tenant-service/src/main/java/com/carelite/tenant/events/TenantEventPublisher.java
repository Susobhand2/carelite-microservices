package com.carelite.tenant.events;

import com.carelite.events.DomainEventEnvelope;
import com.carelite.tenant.domain.Tenant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantEventPublisher {
  private static final String DOMAIN_EVENTS_TOPIC = "domain.events";

  private final KafkaTemplate<String, DomainEventEnvelope> kafkaTemplate;

  public void publishProvisioningRequested(Tenant tenant) {
    DomainEventEnvelope event =
        DomainEventEnvelope.now(
            "TENANT_PROVISIONING_REQUESTED",
            "TENANT",
            tenant.getTenantId(),
            tenant.getTenantId(),
            Map.of(
                "tenantId", tenant.getTenantId(),
                "schemaName", tenant.getSchemaName(),
                "cognitoGroupPrefix", tenant.getCognitoGroupPrefix(),
                "s3Prefix", tenant.getS3Prefix()),
            Map.of("source", "tenant-service"));

    kafkaTemplate.send(DOMAIN_EVENTS_TOPIC, tenant.getTenantId(), event);
  }
}
