CREATE TABLE behavioral_events (
                                   id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   tenant_id   UUID         NOT NULL REFERENCES tenants(id),
                                   customer_id UUID         NOT NULL REFERENCES customers(id),
                                   event_type  VARCHAR(100) NOT NULL,
                                   occurred_at TIMESTAMPTZ  NOT NULL,
                                   received_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                   properties  JSONB
);

CREATE INDEX idx_events_customer_id     ON behavioral_events(customer_id);
CREATE INDEX idx_events_tenant_occurred ON behavioral_events(tenant_id, occurred_at DESC);
CREATE INDEX idx_events_type            ON behavioral_events(tenant_id, event_type, occurred_at DESC);

-- GIN index for querying inside the JSONB properties column
CREATE INDEX idx_events_properties      ON behavioral_events USING GIN(properties);