package com.kehai.api.customer;

import com.kehai.api.common.audit.AuditableEntity;
import com.kehai.api.tenant.Tenant;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "customers",
        indexes = {
                @Index(name = "idx_customers_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_customers_external_id", columnList = "tenant_id, external_id")
        }
)
public class Customer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // The tenant who owns this customer record
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // The customer's ID in the tenant's own system
    // Safaricom sends us "MSISDN_254712345678", we store it here
    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    // When the customer started their subscription with the tenant
    @Column(name = "subscription_started_at")
    private Instant subscriptionStartedAt;

    // Whether this customer has actually churned (ground truth for model training)
    @Column(name = "churned", nullable = false)
    private boolean churned = false;

    @Column(name = "churned_at")
    private Instant churnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public enum Status {
        ACTIVE, AT_RISK, CHURNED
    }

    // Getters and setters
    public UUID getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Instant getSubscriptionStartedAt() { return subscriptionStartedAt; }
    public void setSubscriptionStartedAt(Instant t) { this.subscriptionStartedAt = t; }
    public boolean isChurned() { return churned; }
    public void setChurned(boolean churned) { this.churned = churned; }
    public Instant getChurnedAt() { return churnedAt; }
    public void setChurnedAt(Instant churnedAt) { this.churnedAt = churnedAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
