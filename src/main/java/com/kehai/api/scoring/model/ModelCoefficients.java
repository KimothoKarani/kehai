package com.kehai.api.scoring.model;

public record ModelCoefficients(
        double intercept,
        double loginFrequencyRatio,
        double daysSinceLastLogin,
        double eventCount30d,
        double featureUsageBreadth
) {
    /**
     * Phase 1 coefficients - hand-set from statistical priors.
     * Replace with fitted values once labeled churn data is available.
     */
    public static ModelCoefficients defaults() {
        return new ModelCoefficients(
                -0.85,      // intercept (~30% baseline P (churn))
                -1.50,              // login_frequency_ratio
                +0.05,              // days_since_last_login (per day)
                -0.02,              // event_count_30d (per event)
                -0.20               // feature_usage_breadth (per distinct feature)
        );
    }
}
