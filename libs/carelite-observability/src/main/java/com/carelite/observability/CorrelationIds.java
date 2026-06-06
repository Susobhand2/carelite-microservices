package com.carelite.observability;

import java.util.UUID;

public final class CorrelationIds {

  private CorrelationIds() {}

  public static String newCorrelationId() {
    return UUID.randomUUID().toString();
  }
}
