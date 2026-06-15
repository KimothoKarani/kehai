package com.kehai.api.scoring;

import com.kehai.api.scoring.model.ModelPrediction;

public record ScoringResult(
        ChurnRiskScore score,
        ModelPrediction prediction
) {
}
