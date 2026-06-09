package com.kehai.api.scoring.model;

import java.util.Map;

public record ModelPrediction(
        double score,
        double logOdds,
        Map<String, Double> contributions,
        String modelVersion
) {
}
