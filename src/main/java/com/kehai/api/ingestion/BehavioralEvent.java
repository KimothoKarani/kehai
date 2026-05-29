package com.kehai.api.ingestion;

import com.kehai.api.customer.Customer;
import com.kehai.api.tenant.Tenant;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "behavioral_events",
        indexes = {
                @Index(name = "idx_events_customer_id", columnList = "customer_id"),
                @Index(name = "idx_events_tenant_occurred", columnList = "tenant_id, occurred_at"),
                @Index(name = "idx_events_type", columnList = "tenant_id, event_type, occurred_at")
        }
)
public class BehavioralEvent {

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

    // "login", "feature_used", "payment_made", "support_ticket_opened"
    @Column(name = "event_type", nullable = false)
    private String eventType;

    // When this event actually happened in the tenant's system
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    // When Kehai received it
    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    // Flexible JSON payload: session_duration, features_used, amount, etc.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "properties", columnDefinition = "jsonb")
    private Map<String, Object> properties;

    @PrePersist
    protected void onReceive() {
        receivedAt = Instant.now();
    }

    // Getters and setters
    public UUID getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}
