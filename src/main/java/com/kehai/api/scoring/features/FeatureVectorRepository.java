package com.kehai.api.scoring.features;

import com.kehai.api.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeatureVectorRepository
        extends JpaRepository<FeatureVector, UUID> {
    Optional<FeatureVector> findTopByCustomerOrderByComputedAtDesc(Customer customer);
}
