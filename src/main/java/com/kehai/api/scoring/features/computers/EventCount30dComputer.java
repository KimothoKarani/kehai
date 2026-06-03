package com.kehai.api.scoring.features.computers;

import com.kehai.api.ingestion.BehavioralEvent;
import com.kehai.api.scoring.features.FeatureComputer;
import com.kehai.api.scoring.features.FeatureVector;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EventCount30dComputer implements FeatureComputer {

    @Override
    public void compute(FeatureVector vector, List<BehavioralEvent> events, Instant now) {
        Instant cutoff = now.minus(30, ChronoUnit.DAYS);

        long count = events.stream()
                .filter(e -> e.getOccurredAt().isAfter(cutoff))
                .count();

        vector.setEventCount30d((int) count);
    }
}
