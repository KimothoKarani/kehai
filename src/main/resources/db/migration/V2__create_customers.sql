CREATE TABLE customers (
                           id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           tenant_id               UUID        NOT NULL REFERENCES tenants(id),
                           external_id             VARCHAR(255) NOT NULL,
                           email                   VARCHAR(255),
                           name                    VARCHAR(255),
                           subscription_started_at TIMESTAMPTZ,
                           churned                 BOOLEAN     NOT NULL DEFAULT FALSE,
                           churned_at              TIMESTAMPTZ,
                           status                  VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                           created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customers_tenant_id    ON customers(tenant_id);
CREATE INDEX idx_customers_external_id  ON customers(tenant_id, external_id);
CREATE INDEX idx_customers_status       ON customers(tenant_id, status);

-- A tenant's customer IDs must be unique within that tenant
ALTER TABLE customers
    ADD CONSTRAINT uq_customers_tenant_external
        UNIQUE (tenant_id, external_id);