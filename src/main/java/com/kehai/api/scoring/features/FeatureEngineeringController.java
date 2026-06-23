package com.kehai.api.scoring.features;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers/{externalId}/features")
public class FeatureEngineeringController {

    private final FeatureEngineeringService featureService;

    public FeatureEngineeringController(FeatureEngineeringService featureService) {
        this.featureService = featureService;
    }

    @Operation(
            summary = "Compute a fresh feature vector",
            description = "Reads the customer's behavioral events over the last 90 days, runs every " +
                    "configured FeatureComputer, and persists the resulting vector. Returns the " +
                    "saved vector so callers can verify the computed values."
    )
    @PostMapping("/compute")
    public ResponseEntity<FeatureVector> compute(@PathVariable String externalId) {
        FeatureVector vector = featureService.computeFor(externalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(vector);
    }

    @Operation(
            summary = "Get the most recent feature vector",
            description = "Returns the latest persisted feature vector for the given customer, without " +
                    "recomputing. Useful for inspecting what the scoring model saw on its last run."
    )
    @GetMapping("/latest")
    public ResponseEntity<FeatureVector> latest(@PathVariable String externalId) {
        return ResponseEntity.ok(featureService.getLatest(externalId));
    }
}
