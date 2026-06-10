package com.kehai.api.narrative;

/**
 * A single human-readable explanation of one feature's contribution
 * to a customer's churn risk.
 *
 * @param text              the sentence to show the retention manager
 * @param riskMagnitude     relative importance (0.0 = trivial, 1.0+ = severe);
 *                          used to rank multiple risk signals against each other
 */

public record FeatureExplanation(String text, double riskMagnitude) {
}
