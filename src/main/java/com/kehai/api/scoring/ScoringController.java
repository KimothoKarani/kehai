package com.kehai.api.scoring;

import com.kehai.api.scoring.dto.RiskScoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Churn Scoring",
        description = "Score individual customers for churn risk and retrieve their most recent score. " +
                "Each score arrives with a plain English narrative explaining the primary risk signals."
)
@RestController
@RequestMapping("/customers/{externalId}")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @Operation(
            summary = "Score a customer now",
            description = "Runs the full scoring pipeline for a single customer: fresh feature computation, " +
                    "logistic regression prediction, narrative generation. The result is persisted " +
                    "and returned synchronously. Use this when you need an up-to-the-second score " +
                    "rather than the most recent nightly batch result."
    )
    @PostMapping("/score")
    public ResponseEntity<RiskScoreResponse> score(@PathVariable String externalId) {
        RiskScoreResponse response = scoringService.scoreCustomer(externalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get the most recent risk score",
            description = "Returns the most recently persisted score for the customer, with its narrative " +
                    "and recommended action. Typically this is the score produced by last night's " +
                    "batch run, unless an on-demand score has been requested since."
    )
    @GetMapping("/risk")
    public ResponseEntity<RiskScoreResponse> latestRisk(@PathVariable String externalId) {
        return ResponseEntity.ok(scoringService.getLatestScore(externalId));
    }
}
