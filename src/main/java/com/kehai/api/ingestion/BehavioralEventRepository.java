package com.kehai.api.ingestion;

import com.kehai.api.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BehavioralEventRepository extends JpaRepository<BehavioralEvent, UUID> {
    List<BehavioralEvent> findByCustomerAndOccurredAtAfterOrderByOccurredAtDesc(
            Customer customer, Instant since
    );
}
