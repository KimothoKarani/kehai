package com.kehai.api.scoring.model;

import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LogisticRegressionModel {

    private static final String VERSION = "logreg-v1-fixed";

    private final ModelCoefficients coefficients = ModelCoefficients.defaults();

    public ModelPrediction predict(FeatureVector v) {
        Map<String, Double> contributions = new LinkedHashMap<>();
        double z = coefficients.intercept();

        // login_frequency_ratio (impute 1.0 if null)
        double x1 = imputeRatio(v.getLoginFrequencyRatio(), 1.0);
        double c1 = coefficients.loginFrequencyRatio() * x1;
        contributions.put("login_frequency_ratio", c1);
        z += c1;

        // days_since_last_login (impute 90 if null)
        double x2 = imputeInt(v.getDaysSinceLastLogin(), 90);
        double c2 = coefficients.daysSinceLastLogin() * x2;
        contributions.put("days_since_last_login", c2);
        z += c2;

        // event_count_30d (impute 0 if null)
        double x3 = imputeInt(v.getEventCount30d(), 0);
        double c3 = coefficients.eventCount30d() * x3;
        contributions.put("event_count_30d", c3);
        z += c3;

        // feature_usage_breadth (impute 0 if null)
        double x4 = imputeInt(v.getFeatureUsageBreadth(), 0);
        double c4 = coefficients.featureUsageBreadth() * x4;
        contributions.put("feature_usage_breadth", c4);
        z += c4;

        double probability = sigmoid(z);
        return new ModelPrediction(probability, z, contributions, VERSION);
    }

    private static double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    private static double imputeRatio(BigDecimal value, double fallback) {
        return value != null ? value.doubleValue() : fallback;
    }

    private static double imputeInt(Integer value, double fallback) {
        return value != null ? value.doubleValue() : fallback;
    }

    public String getVersion() { return VERSION; }

}
