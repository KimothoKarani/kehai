package com.kehai.api.narrative.explainers;

import com.kehai.api.narrative.FeatureExplainer;
import com.kehai.api.narrative.FeatureExplanation;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

@Component
public class DaysSinceLastLoginExplainer implements FeatureExplainer {

    private static final int RISK_THRESHOLD_DAYS = 14;

    @Override
    public String featureKey() {
        return "days_since_last_login";
    }

    @Override
    public FeatureExplanation explain(FeatureVector vector) {
        Integer days = vector.getDaysSinceLastLogin();

        if (days == null) {
            // No login at all within our 90-day lookback - the strongest possible signal
            return new FeatureExplanation(
                    "no login activity recorded in the past 90 days",
                    2.0
            );
        }

        if (days < RISK_THRESHOLD_DAYS) return null;

        String text;
        if (days < 30) {
            text = String.format("has not logged in for %d days", days);
        } else if (days < 60) {
            text = String.format("has not logged in for over a month (%d days)", days);
        } else {
            text = String.format("has not logged in for over two months (%d days)", days);
        }

        // 30 days = 1.0, saturates at 2.0
        double magnitude = Math.min(days / 30.0, 2.0);
        return new FeatureExplanation(text, magnitude);
    }
}
