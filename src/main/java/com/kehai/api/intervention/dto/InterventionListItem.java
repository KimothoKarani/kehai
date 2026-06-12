package com.kehai.api.intervention.dto;

import com.kehai.api.scoring.ChurnRiskScore;
import com.kehai.api.scoring.ChurnRiskScoreRepository;

import java.math.BigDecimal;
import java.time.Instant;

public record InterventionListItem(
        String externalCustomerId,
        String customerName,
        String customerEmail,
        BigDecimal score,
        ChurnRiskScore.RiskTier riskTier,
        String narrative,
        String recommendedAction,
        Instant scoredAt
) {

    public static InterventionListItem from(ChurnRiskScore score) {
        return new InterventionListItem(
                score.getCustomer().getExternalId(),
                score.getCustomer().getName(),
                score.getCustomer().getEmail(),
                score.getScore(),
                score.getRiskTier(),
                score.getNarrative(),
                score.getRecommendedAction(),
                score.getScoredAt()
        );
    }
}
