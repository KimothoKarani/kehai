package com.kehai.api.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BehavioralEventRepository extends JpaRepository<BehavioralEvent, UUID> {
    // NO custom queries needed yet. The feature engineering layer will add queries
    // here later (rolling window aggregations)
}
