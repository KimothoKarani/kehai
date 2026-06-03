package com.kehai.api.scoring.features;

import com.kehai.api.ingestion.BehavioralEvent;

import java.time.Instant;
import java.util.List;

public interface FeatureComputer {
    /**
     * Mutates the given FeatureVector with this computer's contribution.
     *
     * @param vector the vector being built up
     * @param events the customer's events within the lookback window,
     *               ordered by occurred_at DESC
     * @param now   references timestamp for "right now" - passed in
     *              so the entire computation uses a consistent clock
     * */
    void compute(FeatureVector vector, List<BehavioralEvent> events, Instant now);
}
