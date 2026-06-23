package com.kehai.api.ingestion;

import com.kehai.api.ingestion.dto.EventRequest;
import com.kehai.api.ingestion.dto.EventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Event Ingestion",
        description = "Push behavioral events from your SaaS application into Kehai. " +
                "Every signal a customer generates (login, feature use, payment, support ticket) " +
                "flows through this endpoint."
)
@RestController
@RequestMapping("/customers/events")
public class EventIngestionController {

    private final EventIngestionService ingestionService;

    public EventIngestionController(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Operation(
            summary = "Ingest a behavioral event",
            description = "Accepts a single behavioral event for a customer. The customer record is " +
                    "auto-created on first sight of an external_customer_id. Events are persisted " +
                    "in their entirety; the properties field accepts any JSON shape."
    )
    @PostMapping
    public ResponseEntity<EventResponse> ingest(@Valid @RequestBody EventRequest request) {
        EventResponse response = ingestionService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
