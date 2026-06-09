package com.kehai.api.scoring.dto;

import com.kehai.api.scoring.ChurnRiskScore;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record RiskScoreResponse(
        String externalCustomerId,
        BigDecimal score,
        ChurnRiskScore.RiskTier riskTier,
        int timeHorizonDays,
        String narrative,
        String recommendedAction,
        Instant scoredAt,
        String modelVersion,
        Map<String, Double> contributions
) {
    public static RiskScoreResponse from(ChurnRiskScore score,
                                         Map<String, Double> contributions) {
        return new RiskScoreResponse(
                score.getCustomer().getExternalId(),
                score.getScore(),
                score.getRiskTier(),
                score.getTimeHorizonDays(),
                score.getNarrative(),
                score.getRecommendedAction(),
                score.getScoredAt(),
                score.getModelVersion(),
                contributions
        );
    }

    public static RiskScoreResponse from(ChurnRiskScore score) {
        return from(score, null);
    }
}
