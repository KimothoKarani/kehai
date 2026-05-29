CREATE TABLE feature_vectors (
                                 id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 tenant_id                   UUID           NOT NULL REFERENCES tenants(id),
                                 customer_id                 UUID           NOT NULL REFERENCES customers(id),
                                 computed_at                 TIMESTAMPTZ    NOT NULL,
                                 login_frequency_ratio       NUMERIC(8,4),
                                 feature_usage_breadth       INTEGER,
                                 days_since_unresolved_ticket INTEGER,
                                 unresolved_ticket_count     INTEGER,
                                 last_payment_days_late      INTEGER,
                                 payment_failure_count       INTEGER,
                                 engagement_trend_slope      NUMERIC(8,4),
                                 days_since_last_login       INTEGER,
                                 event_count_30d             INTEGER
);

CREATE INDEX idx_features_customer_id  ON feature_vectors(customer_id);
CREATE INDEX idx_features_computed_at  ON feature_vectors(tenant_id, computed_at DESC);