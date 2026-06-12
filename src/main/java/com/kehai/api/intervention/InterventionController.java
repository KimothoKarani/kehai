package com.kehai.api.intervention;

import com.kehai.api.intervention.dto.InterventionListResponse;
import com.kehai.api.scoring.ChurnRiskScore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @GetMapping("/high-risk")
    public ResponseEntity<InterventionListResponse> highRisk(
            @RequestParam(defaultValue = "HIGH") ChurnRiskScore.RiskTier tier,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(interventionService.getInterventionList(tier, limit));
    }
}
