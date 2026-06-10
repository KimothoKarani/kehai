package com.kehai.api.narrative.explainers;

import com.kehai.api.narrative.FeatureExplainer;
import com.kehai.api.narrative.FeatureExplanation;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoginFrequencyRatioExplainer implements FeatureExplainer {

    /** A ratio at or above this is normal-to-improved engagement. */
    private static final double RISK_THRESHOLD = 0.7;

    @Override
    public String featureKey() { return "login_frequency_ratio"; }

    @Override
    public FeatureExplanation explain(FeatureVector vector) {
        BigDecimal ratioBd = vector.getLoginFrequencyRatio();
        if (ratioBd == null) return null;

        double ratio = ratioBd.doubleValue();
        if (ratio >= RISK_THRESHOLD) return null;

        int dropPct = (int) Math.round((1.0 - ratio) * 100);
        String text = String.format(
                "login frequency has dropped %d%% versus the baseline period",
                dropPct
        );

        // Magnitude: a 100% drop is "severe" (1.0), a 30% drop is "moderate" (0.3)
        double magnitude = 1.0 - ratio;
        return new FeatureExplanation(text, magnitude);
    }
}
