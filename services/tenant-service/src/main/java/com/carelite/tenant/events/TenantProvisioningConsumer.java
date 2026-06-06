package com.carelite.tenant.events;

import com.carelite.events.DomainEventEnvelope;
import com.carelite.tenant.service.TenantProvisioningWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantProvisioningConsumer {

  private final TenantProvisioningWorker worker;

  @RetryableTopic(
      attempts = "3",
      backoff = @Backoff(delay = 10000, multiplier = 2.0),
      dltTopicSuffix = ".dlt",
      retryTopicSuffix = ".retry")
  @KafkaListener(topics = "domain.events", groupId = "tenant-service")
  public void consume(DomainEventEnvelope event) {
    if (!"TENANT_PROVISIONING_REQUESTED".equals(event.eventType())) {
      return;
    }

    worker.provision(event.aggregateId());
  }
}
