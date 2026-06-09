package com.kehai.api.scoring;

import com.kehai.api.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChurnRiskScoreRepository
    extends JpaRepository<ChurnRiskScore, UUID>
{
    Optional<ChurnRiskScore> findTopByCustomerOrderByScoredAtDesc(Customer customer);
}
