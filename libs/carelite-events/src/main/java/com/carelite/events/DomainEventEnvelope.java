package com.carelite.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DomainEventEnvelope(
    UUID eventId,
    String eventType,
    String aggregateType,
    String aggregateId,
    String tenantId,
    Map<String, Object> payload,
    Map<String, String> metadata,
    Instant occurredAt) {
  public static DomainEventEnvelope now(
      String eventType,
      String aggregateType,
      String aggregateId,
      String tenantId,
      Map<String, Object> payload,
      Map<String, String> metadata) {
    return new DomainEventEnvelope(
        UUID.randomUUID(),
        eventType,
        aggregateType,
        aggregateId,
        tenantId,
        payload,
        metadata,
        Instant.now());
  }
}
