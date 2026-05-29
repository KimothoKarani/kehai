CREATE TABLE tenants (
                         id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name                VARCHAR(255) NOT NULL,
                         slug                VARCHAR(100) NOT NULL UNIQUE,
                         email               VARCHAR(255) NOT NULL UNIQUE,
                         plan                VARCHAR(50)  NOT NULL,
                         status              VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
                         stripe_customer_id  VARCHAR(255),
                         created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                         updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tenants_slug ON tenants(slug);