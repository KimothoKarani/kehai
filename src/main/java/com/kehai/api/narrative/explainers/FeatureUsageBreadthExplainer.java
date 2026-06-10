package com.kehai.api.narrative.explainers;

import com.kehai.api.narrative.FeatureExplainer;
import com.kehai.api.narrative.FeatureExplanation;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

@Component
public class FeatureUsageBreadthExplainer implements FeatureExplainer {

    private static final int RISK_THRESHOLD = 2;

    @Override
    public String featureKey() {
        return "feature_usage_breadth";
    }

    @Override
    public FeatureExplanation explain(FeatureVector vector) {
        Integer breadth = vector.getFeatureUsageBreadth();
        if (breadth == null || breadth >= RISK_THRESHOLD) return null;

        String text = (breadth == 0)
                ? "no distinct features used in the last 30 days"
                : "using only one distinct feature, indicating narrow product engagement";

        double  magnitude = (RISK_THRESHOLD - breadth) / (double) RISK_THRESHOLD;
        return new FeatureExplanation(text, magnitude);
    }
}
