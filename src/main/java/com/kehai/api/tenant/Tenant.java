package com.kehai.api.tenant;

import com.kehai.api.common.audit.AuditableEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class Tenant extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    // The unique slug used in API keys and logs: "safaricom", "twiga-foods"
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    public enum Plan {
        STARTER, GROWTH, SCALE, ENTERPRISE
    }

    public enum Status {
        ACTIVE, SUSPENDED, CANCELLED
    }

    // Getter and setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String id) { this.stripeCustomerId = id; }

}
