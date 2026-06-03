package com.kehai.api.scoring.features.computers;

import com.kehai.api.ingestion.BehavioralEvent;
import com.kehai.api.scoring.features.FeatureComputer;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FeatureUsageBreadthComputer implements FeatureComputer {

    @Override
    public void compute(FeatureVector vector, List<BehavioralEvent> events, Instant now) {
        Instant cutoff = now.minus(30, ChronoUnit.DAYS);

        Set<String> distinctEventTypes = new HashSet<>();
        for (BehavioralEvent event : events) {
            if (event.getOccurredAt().isAfter(cutoff)) {
                distinctEventTypes.add(event.getEventType());
            }
        }

        vector.setFeatureUsageBreadth(distinctEventTypes.size());
    }
}
