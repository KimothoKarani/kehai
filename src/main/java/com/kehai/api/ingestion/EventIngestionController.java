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

@RestController
@RequestMapping("/customers/events")
public class EventIngestionController {

    private final EventIngestionService ingestionService;

    public EventIngestionController(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> ingest(@Valid @RequestBody EventRequest request) {
        EventResponse response = ingestionService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
