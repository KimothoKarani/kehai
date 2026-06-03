package com.kehai.api.scoring.features;

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

    @PostMapping("/compute")
    public ResponseEntity<FeatureVector> compute(@PathVariable String externalId) {
        FeatureVector vector = featureService.computeFor(externalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(vector);
    }

    @GetMapping("/latest")
    public ResponseEntity<FeatureVector> latest(@PathVariable String externalId) {
        return ResponseEntity.ok(featureService.getLatest(externalId));
    }
}
