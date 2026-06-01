package com.kehai.api.ingestion.dto;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID eventId,
        Instant receivedAt,
        String status
) {
    public static EventResponse accepted(UUID id, Instant receivedAt) {
        return new EventResponse(id, receivedAt, "accepted");
    }
}
