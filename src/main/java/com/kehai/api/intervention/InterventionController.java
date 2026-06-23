package com.kehai.api.intervention;

import com.kehai.api.intervention.dto.InterventionListResponse;
import com.kehai.api.scoring.ChurnRiskScore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Intervention List",
        description = "The prioritised list of customers your retention team should work through today. " +
                "Returns the most recent score per customer, filtered by risk tier, sorted by " +
                "score descending. This is the endpoint your dashboard calls each morning."
)
@RestController
@RequestMapping("/customers")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @Operation(
            summary = "Get the prioritised intervention list",
            description = "Returns at most one row per customer (their most recent score), filtered to the " +
                    "specified risk tier. Tier defaults to HIGH and limit defaults to 50. The hard " +
                    "limit cap is 500 to protect the database from runaway requests."
    )
    @GetMapping("/high-risk")
    public ResponseEntity<InterventionListResponse> highRisk(
            @RequestParam(defaultValue = "HIGH") ChurnRiskScore.RiskTier tier,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(interventionService.getInterventionList(tier, limit));
    }
}
