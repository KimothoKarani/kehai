package com.kehai.api.scoring;

import com.kehai.api.scoring.dto.RiskScoreResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers/{externalId}")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/score")
    public ResponseEntity<RiskScoreResponse> score(@PathVariable String externalId) {
        RiskScoreResponse response = scoringService.scoreCustomer(externalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/risk")
    public ResponseEntity<RiskScoreResponse> latestRisk(@PathVariable String externalId) {
        return ResponseEntity.ok(scoringService.getLatestScore(externalId));
    }
}
