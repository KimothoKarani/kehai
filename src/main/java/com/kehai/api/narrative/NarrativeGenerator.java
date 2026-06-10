package com.kehai.api.narrative;

import com.kehai.api.scoring.ScoringProperties;
import com.kehai.api.scoring.features.FeatureVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NarrativeGenerator {

    private static final Logger log = LoggerFactory.getLogger(NarrativeGenerator.class);

    private static final int MAX_RISK_FACTORS = 3;

    private final List<FeatureExplainer> explainers;
    private final ScoringProperties properties;


    public NarrativeGenerator(List<FeatureExplainer> explainers,
                              ScoringProperties properties) {
        this.explainers = explainers;
        this.properties = properties;

        log.info("NarrativeGenerator initialized with {} explainers: {}",
                explainers.size(),
                explainers.stream()
                        .map(e -> e.getClass().getSimpleName())
                        .toList());
    }

    public NarrativeResult generate(double score, FeatureVector vector) {
        List<FeatureExplanation> riskSignals = explainers.stream()
                .map(e -> e.explain(vector))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(FeatureExplanation::riskMagnitude).reversed())
                .limit(MAX_RISK_FACTORS)
                .toList();

        String narrative = buildNarrative(score, riskSignals);
        String action = buildRecommendedAction(score, riskSignals);

        return new NarrativeResult(narrative, action);
    }

    private String buildNarrative(double score, List<FeatureExplanation> signals) {
        int pct = (int) Math.round(score * 100);
        int horizon = properties.getTimeHorizonDays();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "This customer has a %d%% probability of churning within %d days. ",
                pct, horizon));

        if (signals.isEmpty()) {
            sb.append("No specific risk signals detected at this time; the score reflects baseline model uncertainty.");
            return sb.toString();
        }

        if (signals.size() == 1) {
            sb.append("Primary risk signal: ");
            sb.append(signals.get(0).text());
        } else {
            sb.append("Primary risk signals: ");
            sb.append(signals.stream()
                    .map(FeatureExplanation::text)
                    .collect(Collectors.joining("; ")));
        }
        sb.append(".");

        return sb.toString();
    }

    private String buildRecommendedAction(double score, List<FeatureExplanation> signals) {
        double high = properties.getRiskThresholds().getHigh();
        double medium = properties.getRiskThresholds().getMedium();

        if (score >= high) {
            return "Priority outreach from account manager within 48 hours. " +
                    "Investigate the underlying disengagement and propose concrete next steps before " +
                    "this customer fully detaches.";
        }
        if (score >= medium) {
            return "Schedule a proactive check-in within the next 7 days. " +
                    "A light-touch conversation now is materially cheaper than recovery later.";
        }
        return "No immediate action required. Continue monitoring through standard channels.";
    }


}
