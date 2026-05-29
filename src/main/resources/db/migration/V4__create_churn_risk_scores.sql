CREATE TABLE churn_risk_scores (
                                   id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   tenant_id           UUID           NOT NULL REFERENCES tenants(id),
                                   customer_id         UUID           NOT NULL REFERENCES customers(id),
                                   score               NUMERIC(5,4)   NOT NULL,
                                   time_horizon_days   INTEGER        NOT NULL,
                                   risk_tier           VARCHAR(20)    NOT NULL,
                                   narrative           TEXT           NOT NULL,
                                   recommended_action  TEXT,
                                   scored_at           TIMESTAMPTZ    NOT NULL,
                                   model_version       VARCHAR(50)    NOT NULL,
                                   created_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                                   updated_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_scores_customer_id     ON churn_risk_scores(customer_id);
CREATE INDEX idx_scores_tenant_scored   ON churn_risk_scores(tenant_id, scored_at DESC);
CREATE INDEX idx_scores_risk_tier       ON churn_risk_scores(tenant_id, risk_tier);