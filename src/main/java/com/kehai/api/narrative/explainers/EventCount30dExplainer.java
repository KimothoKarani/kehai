package com.kehai.api.narrative.explainers;

import com.kehai.api.narrative.FeatureExplainer;
import com.kehai.api.narrative.FeatureExplanation;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

@Component
public class EventCount30dExplainer implements FeatureExplainer {

    private static final int RISK_THRESHOLD = 5;

    @Override
    public String featureKey() {
        return "event_count_30d";
    }

    @Override
    public FeatureExplanation explain(FeatureVector vector) {
        Integer count = vector.getEventCount30d();
        if (count == null ||  count >= RISK_THRESHOLD) return null;

        String text = (count == 0) ? "no activity events recorded in the last 30 days"
                : String.format("only %d activity events in the last 30 days", count);

        double magnitude = (RISK_THRESHOLD - count) / (double) RISK_THRESHOLD;
        return new FeatureExplanation(text, magnitude);
    }
}
