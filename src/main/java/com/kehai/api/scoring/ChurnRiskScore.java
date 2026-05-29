package com.kehai.api.scoring;

import com.kehai.api.common.audit.AuditableEntity;
import com.kehai.api.customer.Customer;
import com.kehai.api.tenant.Tenant;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "churn_risk_scores",
        indexes = {
                @Index(name = "idx_scores_customer_id", columnList = "customer_id"),
                @Index(name = "idx_scores_tenant_scored", columnList = "tenant_id, scored_at"),
                @Index(name = "idx_scores_risk_tier", columnList = "tenant_id, risk_tier")
        }
)
public class ChurnRiskScore extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 0.00 to 1.00 - raw model output
    @Column(name = "score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;

    // Days until predicted churn
    @Column(name = "time_horizon_days", nullable = false)
    private int timeHorizonDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_tier", nullable = false)
    private RiskTier riskTier;

    // The plain English explanation - Kehai's core output
    @Column(name = "narrative", nullable = false, columnDefinition = "TEXT")
    private String narrative;

    // The recommended action for the retention team
    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(name = "scored_at", nullable = false)
    private Instant scoredAt;

    // Which model version produced this score - important for auditing
    @Column(name = "model_version", nullable = false)
    private String modelVersion;

    public enum RiskTier {
        LOW, // 0.00 - 0.39
        MEDIUM, //0.40 - 0.69
        HIGH // 0.70 - 1.00
    }

    // Getters and setters
    public UUID getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public int getTimeHorizonDays() { return timeHorizonDays; }
    public void setTimeHorizonDays(int days) { this.timeHorizonDays = days; }
    public RiskTier getRiskTier() { return riskTier; }
    public void setRiskTier(RiskTier riskTier) { this.riskTier = riskTier; }
    public String getNarrative() { return narrative; }
    public void setNarrative(String narrative) { this.narrative = narrative; }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String action) { this.recommendedAction = action; }
    public Instant getScoredAt() { return scoredAt; }
    public void setScoredAt(Instant scoredAt) { this.scoredAt = scoredAt; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

}
