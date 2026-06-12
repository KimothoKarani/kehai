package com.kehai.api.scoring;

import com.kehai.api.customer.Customer;
import com.kehai.api.tenant.Tenant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ChurnRiskScoreRepository
    extends JpaRepository<ChurnRiskScore, UUID>
{
    Optional<ChurnRiskScore> findTopByCustomerOrderByScoredAtDesc(Customer customer);

    /**
     * Returns the most recent ChurnRiskScore per customer for a given tenant
     * and risk tier, ordered by score descending.
     *
     * The inner subquery finds the latest score per customer; the outer query
     * filters by tier and orders by score. This pattern handles the common
     * "latest record per group" requirement without window function, so it
     * works on the simplest PostgresSQL setup.
     */
    @Query("""
            SELECT s FROM ChurnRiskScore s
            WHERE s.tenant = :tenant
                AND s.riskTier = :tier
                AND s.scoredAt = (
                    SELECT MAX(s2.scoredAt) FROM ChurnRiskScore s2
                    WHERE s2.customer = s.customer
                )
               ORDER BY s.score DESC
            """)
    List<ChurnRiskScore> findLatestScoresByTenantAndTier(
            @Param("tenant") Tenant tenant,
            @Param("tier") ChurnRiskScore.RiskTier tier,
            Pageable pageable
    );
}
