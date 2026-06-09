package com.kehai.api.scoring.features;

import com.kehai.api.customer.Customer;
import com.kehai.api.tenant.Tenant;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(
        name = "feature_vectors",
        indexes = {
                @Index(name = "idx_features_customer_id", columnList = "customer_id"),
                @Index(name = "idx_features_computed_at", columnList = "tenant_id, computed_at")
        }
)
public class FeatureVector {

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

    @Column(name = "computed_at", nullable = false)
    private Instant computedAt;

    // Login frequency: last 7 days vs previous 30 days
    @Column(name = "login_frequency_ratio", precision = 8, scale = 4)
    private BigDecimal loginFrequencyRatio;

    // Number of distinct features used in last 30 days
    @Column(name = "feature_usage_breadth")
    private Integer featureUsageBreadth;

    // Days since last unresolved support ticket (null = no open tickets)
    @Column(name = "days_since_unresolved_ticket")
    private Integer daysSinceUnresolvedTicket;

    // Number of unresolved support tickets
    @Column(name = "unresolved_ticket_count")
    private Integer unresolvedTicketCount;

    // Days late on last payment ( 0 = on time)
    @Column(name = "last_payment_days_late")
    private Integer lastPaymentDaysLate;

    // Number of payment failures in last 90 days
    @Column(name = "payment_failure_count")
    private Integer paymentFailureCount;

    // Slope of weekly active days over last 90 days (negative = declining)
    @Column(name = "engagement_trend_slope", precision = 8, scale = 4)
    private BigDecimal engagementTrendSlope;

    // Days since last login
    @Column(name = "days_since_last_login")
    private Integer daysSinceLastLogin;

    // Total events in last 30 days
    @JsonProperty("event_count_30d")
    @Column(name = "event_count_30d")
    private Integer eventCount30d;

    // Getters and setters
    public UUID getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Instant getComputedAt() { return computedAt; }
    public void setComputedAt(Instant computedAt) { this.computedAt = computedAt; }
    public BigDecimal getLoginFrequencyRatio() { return loginFrequencyRatio; }
    public void setLoginFrequencyRatio(BigDecimal v) { this.loginFrequencyRatio = v; }
    public Integer getFeatureUsageBreadth() { return featureUsageBreadth; }
    public void setFeatureUsageBreadth(Integer v) { this.featureUsageBreadth = v; }
    public Integer getDaysSinceUnresolvedTicket() { return daysSinceUnresolvedTicket; }
    public void setDaysSinceUnresolvedTicket(Integer v) { this.daysSinceUnresolvedTicket = v; }
    public Integer getUnresolvedTicketCount() { return unresolvedTicketCount; }
    public void setUnresolvedTicketCount(Integer v) { this.unresolvedTicketCount = v; }
    public Integer getLastPaymentDaysLate() { return lastPaymentDaysLate; }
    public void setLastPaymentDaysLate(Integer v) { this.lastPaymentDaysLate = v; }
    public Integer getPaymentFailureCount() { return paymentFailureCount; }
    public void setPaymentFailureCount(Integer v) { this.paymentFailureCount = v; }
    public BigDecimal getEngagementTrendSlope() { return engagementTrendSlope; }
    public void setEngagementTrendSlope(BigDecimal v) { this.engagementTrendSlope = v; }
    public Integer getDaysSinceLastLogin() { return daysSinceLastLogin; }
    public void setDaysSinceLastLogin(Integer v) { this.daysSinceLastLogin = v; }
    public Integer getEventCount30d() { return eventCount30d; }
    public void setEventCount30d(Integer v) { this.eventCount30d = v; }

}
