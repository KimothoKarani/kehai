package com.kehai.api.ingestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

public record EventRequest(
    @NotBlank(message = "external_customer_id is required")
    String externalCustomerId,

    @NotBlank(message = "event_type is required")
    String eventType,

    @NotNull(message = "occurred_at is required")
    Instant occurredAt,

    Map<String, Object> properties

    ) {}
