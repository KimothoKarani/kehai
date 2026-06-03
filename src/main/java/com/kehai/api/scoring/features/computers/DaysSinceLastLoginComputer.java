package com.kehai.api.scoring.features.computers;

import com.kehai.api.ingestion.BehavioralEvent;
import com.kehai.api.scoring.features.FeatureComputer;
import com.kehai.api.scoring.features.FeatureComputer;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class DaysSinceLastLoginComputer implements FeatureComputer {


    @Override
    public void compute(FeatureVector vector, List<BehavioralEvent> events, Instant now) {
        Instant lastLogin = events.stream()
                .filter(e -> "login".equals(e.getEventType()))
                .map(BehavioralEvent::getOccurredAt)
                .findFirst()        // events are pre-sorted DESC
                .orElse(null);

        if (lastLogin == null) {
            vector.setDaysSinceLastLogin(null);
            return;
        }

        long days = ChronoUnit.DAYS.between(lastLogin, now);
        vector.setDaysSinceLastLogin((int) days);
    }
}
