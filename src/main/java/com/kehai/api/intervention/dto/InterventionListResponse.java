package com.kehai.api.intervention.dto;

import com.kehai.api.scoring.ChurnRiskScore;

import java.time.Instant;
import java.util.List;

public record InterventionListResponse(
        Instant generatedAt,
        int totalCustomers,
        ChurnRiskScore.RiskTier tierFilter,
        List<InterventionListItem> customers
) {
}
