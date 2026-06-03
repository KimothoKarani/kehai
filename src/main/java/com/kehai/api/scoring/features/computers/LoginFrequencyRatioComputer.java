package com.kehai.api.scoring.features.computers;

import com.kehai.api.ingestion.BehavioralEvent;
import com.kehai.api.scoring.features.FeatureComputer;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class LoginFrequencyRatioComputer implements FeatureComputer {

    @Override
    public void compute(FeatureVector vector, List<BehavioralEvent> events, Instant now) {
        Instant recentCutoff = now.minus(7, ChronoUnit.DAYS);
        Instant baselineStart = now.minus(37, ChronoUnit.DAYS);

        long recentLogins = 0;
        long baselineLogins = 0;

        for (BehavioralEvent event : events) {
            if (!"login".equals(event.getEventType())) continue;

            Instant t = event.getOccurredAt();
            if (t.isAfter(recentCutoff)) {
                recentLogins++;
            } else if (t.isAfter(baselineStart)) {
                baselineLogins++;
            }
        }

        if (baselineLogins == 0) {
            // Can't compute a ratio without a baseline - leave null
            vector.setLoginFrequencyRatio(null);
            return;
        }

        // Normalize baseline (30 days) to a 7-day equivalent rate
        BigDecimal baselineWeekly = BigDecimal.valueOf(baselineLogins)
                .multiply(BigDecimal.valueOf(7))
                .divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);

        BigDecimal ratio = BigDecimal.valueOf(recentLogins)
                .divide(baselineWeekly, 4, RoundingMode.HALF_UP);

        vector.setLoginFrequencyRatio(ratio);
    }
}
