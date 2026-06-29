# Kehai

**Behavioral churn prediction for B2B SaaS.**

Kehai detects the quiet signals that a subscription customer is about to leave, weeks before they actually do. It reads behavioral event data from your application, computes a churn risk score with a transparent statistical model, and returns a plain English explanation that a customer success manager can act on the same morning.

The name comes from a Japanese concept meaning the presence you sense before something happens. The faint signal that someone is about to enter the room. That is what Kehai detects in your customer data.

---

## Why Kehai exists

Most churn prediction tools are black boxes. A data scientist trains a model, it outputs a number, and the retention team has no idea why customer X is flagged. They cannot act confidently on a score they do not understand.

Kehai is interpretable by design. Every risk score arrives with a sentence:

> "This customer has a 71% probability of churning within 30 days. Primary risk signals: has not logged in for over two months (65 days); no activity events recorded in the last 30 days; no distinct features used in the last 30 days."

> "Priority outreach from account manager within 48 hours."

That is not a number. That is a decision the retention team can act on without needing to understand survival analysis.

---

## Who Kehai is for

Kehai is built specifically for B2B SaaS companies with 500 to 50,000 subscribers. The features it computes, the language it uses in its narratives, and the integrations it ships with are all chosen for this segment. It is not a general-purpose ML platform. It is not built (yet) for telecoms, gyms, insurance, or media companies.

This is deliberate. We believe the best churn prediction for B2B SaaS will not come from a horizontal ML platform. It will come from a product that understands SaaS retention deeply, integrates natively with the tools SaaS teams already use, and speaks the language of customer success managers.

Other verticals will follow, with industry-specific configurations built on top of the same engine. But not first.

---

## What Kehai expects you to send

Kehai is event-driven. You send Kehai a stream of behavioral events from your SaaS application. The features Kehai computes assume a specific event vocabulary, listed below. If you already track these events for analytics, Segment, Mixpanel, or your own data warehouse, you can pipe the same stream into Kehai with minimal additional work.

### The minimum viable integration

At a minimum, Kehai needs **one** event type to produce a useful score: `login`. With only logins, Kehai can compute three of its four features (login frequency, days since last login, total event count) and generate a meaningful narrative.

A single event sent to `POST /api/v1/customers/events` looks like this:

```json
{
  "external_customer_id": "user_42",
  "event_type": "login",
  "occurred_at": "2026-06-23T08:15:00Z",
  "properties": {
    "session_id": "sess_abc123"
  }
}
```

| Field | Required | Description |
|-------|----------|-------------|
| `external_customer_id` | Yes | Your internal identifier for the user. Kehai never assigns its own IDs to your users. |
| `event_type` | Yes | One of the canonical event types listed below. |
| `occurred_at` | Yes | ISO 8601 timestamp of when the event happened in your system. |
| `properties` | No | Arbitrary JSON. Stored as-is; some properties influence specific features. |

The customer record is created automatically the first time Kehai sees an event for an `external_customer_id` it has not seen before. No separate "create customer" call is required.

### The canonical event vocabulary

These are the events Kehai's feature engineering layer understands today. Send the ones that exist in your product. The more you send, the more accurate Kehai becomes.

#### Engagement events (required for v1)

| Event type | When to send | Why it matters |
|-----------|--------------|----------------|
| `login` | Every successful user login or session start | The single most important churn signal. Drives login frequency, days since last login, and total activity features. |
| `feature_used` | When a user invokes a meaningful product feature | Drives feature usage breadth. The `feature_name` property is recommended. |

#### Value events (recommended)

| Event type | When to send | Why it matters |
|-----------|--------------|----------------|
| `report_exported` | When a user exports, downloads, or shares a report | Strong indicator of getting value out of the product. |
| `record_created` | When a user creates a meaningful business record | Indicates active product use, not just login. |

#### Commercial events (recommended)

| Event type | When to send | Why it matters |
|-----------|--------------|----------------|
| `payment_succeeded` | On successful subscription payment | Establishes payment recency. |
| `payment_failed` | On failed subscription payment | Strong leading indicator of churn. |
| `plan_changed` | When a user upgrades or downgrades | Downgrades signal early disengagement. The `from_plan` and `to_plan` properties are required. |
| `subscription_cancelled` | When a user cancels | Used to label ground truth for future model training. |

#### Friction events (recommended)

| Event type | When to send | Why it matters |
|-----------|--------------|----------------|
| `support_ticket_opened` | When a user opens a support ticket | The `priority` and `category` properties help future features. |
| `support_ticket_resolved` | When a ticket is resolved | Used to compute unresolved ticket counts. |

### Events Kehai does not yet use

If you send events not in the list above (for example `dashboard_viewed`, `invite_sent`, `webhook_configured`), Kehai will accept and store them without complaint. They will sit in the events table waiting for future features that use them. They will not cause errors. Send everything you have. Kehai will use what it can today and grow into the rest.

### What you should not send

Kehai is not an analytics platform. Do not send page views, mouse clicks, or every API call. The system is designed for meaningful business events, not raw telemetry. A SaaS customer typically generates between 10 and 200 Kehai events per month, not 10,000.

---

## What Kehai sends you back

The retention team consumes Kehai through one of three patterns. Pick the one that fits your stack.

### Pattern 1: Daily intervention list (simplest)

Your dashboard or internal tool calls `GET /api/v1/customers/high-risk` once per day. Kehai returns a sorted list of customers who need attention this morning, with the narrative and recommended action for each.

```bash
curl https://api.kehai.io/api/v1/customers/high-risk?tier=HIGH&limit=50 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Response:

```json
{
  "generated_at": "2026-06-23T08:00:00Z",
  "total_customers": 12,
  "tier_filter": "HIGH",
  "customers": [
    {
      "external_customer_id": "user_42",
      "score": 0.74,
      "risk_tier": "HIGH",
      "narrative": "This customer has a 74% probability of churning within 30 days. Primary risk signals: has not logged in for over two months; no activity events recorded in the last 30 days.",
      "recommended_action": "Priority outreach from account manager within 48 hours. Investigate the underlying disengagement and propose concrete next steps before this customer fully detaches.",
      "scored_at": "2026-06-23T02:15:00Z"
    }
  ]
}
```

This is the easiest pattern to implement and the one most early customers should start with.

### Pattern 2: Per-customer score lookup

When a CSM opens a customer record in your internal CRM, you call `GET /api/v1/customers/{externalId}/risk` for that one customer. Useful for embedding the Kehai narrative into existing tools without redesigning the workflow.

### Pattern 3: Webhooks (planned, not yet implemented)

You give Kehai a URL. When a customer crosses into HIGH risk, Kehai POSTs to your URL with the narrative and recommended action. This integrates naturally with Slack notifications, HubSpot updates, or Intercom workflows. Coming in v1.1.

---

## How scoring happens

Kehai scores every active customer every night at 02:00 UTC, automatically. The intervention list is populated and ready before your retention team logs in. You do not need to trigger scoring yourself.

If you need to score a customer on demand, for example immediately after a meaningful event, you can call `POST /api/v1/customers/{externalId}/score`. The fresh score is returned synchronously.

---

## What it does today

- Accept behavioral events via a single HTTP endpoint
- Auto-create customer records on first sight of an event
- Compute statistical features over rolling time windows
- Score each customer with a logistic regression model that produces a calibrated 0 to 1 probability
- Generate a plain English narrative explaining the score
- Return a recommended action sized to the risk tier
- Serve a prioritised intervention list endpoint
- Run a nightly batch scoring job autonomously

All of this is enforced by JWT authentication and scoped to the calling tenant. Multi-tenant isolation is enforced at the database layer on every query.

---

## What it will do

The full vision for Kehai includes capabilities that are not built yet:

- A web dashboard for retention teams who do not want to consume the API directly
- SDKs in Python, JavaScript, Ruby, and Go for easier integration
- Pre-built integrations with Segment, Stripe, HubSpot, and Intercom
- Webhook notifications when customers cross risk tier boundaries
- Cox proportional hazards model for time-to-churn predictions
- Gradient boosting model with SHAP value explanations for tenants with enough labeled data
- Customer-defined features for tenants with non-standard event streams
- Stripe subscription billing, plan enforcement, and a customer billing portal

These are not commitments with dates. They are the shape of where Kehai is going. Each one ships when the data and the customers say it should.

---

## Architecture

```
Your SaaS application
        |
        |  HTTP POST per behavioral event
        v
Kehai REST API (Spring Boot 3.5, Java 21, PostgreSQL 16)
        |
        |  Event Ingestion
        |    persists raw events in jsonb
        |
        |  Feature Engineering (strategy pattern, one class per feature)
        |    computes engagement signals over rolling windows
        |
        |  Scoring (logistic regression with hand set coefficients)
        |    produces calibrated probability and per feature contributions
        |
        |  Narrative Generation (rule based, one explainer per feature)
        |    writes plain English explanation and recommended action
        |
        v
Your retention team (via API, dashboard, or webhook)
```

The pipeline runs synchronously for ad hoc scoring requests and autonomously via Quartz + Spring Batch for nightly scoring of every active customer in every tenant.

---

## Technology

- **Spring Boot 3.5 on Java 21** for the API runtime. Virtual threads are available for high concurrency I/O.
- **PostgreSQL 16** as the only data store. Behavioral events are stored as jsonb to accommodate any SaaS company's event schema without code changes.
- **Flyway** for versioned schema migrations. All schema changes are SQL scripts in version order.
- **Spring Security with OAuth2 Resource Server** for stateless JWT authentication. No sessions, ever.
- **Spring Batch with Quartz** for autonomous nightly scoring.
- **Caffeine** for in-process caching of recent scores and feature vectors.
- **Stripe** (planned) for subscription billing.

---

## Running Kehai locally

You will need Java 21, Maven, and PostgreSQL 16 installed.

**1. Create the database and user:**

```bash
sudo -u postgres psql
CREATE DATABASE kehai;
CREATE USER kehai_user WITH PASSWORD 'kehai_pass';
GRANT ALL PRIVILEGES ON DATABASE kehai TO kehai_user;
\c kehai
GRANT ALL ON SCHEMA public TO kehai_user;
```

**2. Copy the example configuration:**

```bash
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

**3. Run the application:**

```bash
./mvnw spring-boot:run
```

**4. Seed a test tenant:**

```bash
psql -U kehai_user -d kehai -h localhost \
  -c "INSERT INTO tenants (name, slug, email, plan, status) VALUES ('Test Co', 'test-tenant', 'admin@test.com', 'STARTER', 'ACTIVE');"
```

**5. Generate a development JWT:**

The class `com.kehai.api.devtools.DevTokenGenerator` in the test sources prints a valid token to standard output when you run its `main` method. Use that token in the `Authorization: Bearer ...` header for all API requests.

---

## API at a glance

All endpoints are prefixed with `/api/v1` and require a valid JWT.

```
POST /customers/events                          Ingest a behavioral event
POST /customers/{externalId}/features/compute   Compute a fresh feature vector
GET  /customers/{externalId}/features/latest    Get the most recent feature vector
POST /customers/{externalId}/score              Score a customer now
GET  /customers/{externalId}/risk               Get the most recent score for a customer
GET  /customers/high-risk?tier=HIGH&limit=50    Get the prioritised intervention list

POST /admin/jobs/nightly-scoring/run            Manually trigger nightly scoring (admin only)

GET  /actuator/health                           Health check (public)
```

---

## Multi-tenant isolation

Kehai is multi-tenant from the first commit. Every database table has a `tenant_id` column. Every query filters on it. The tenant is extracted from the JWT's `tenant_id` claim at the security filter layer and made available to every downstream service through a thread local context.

This means one tenant cannot read another tenant's data, even by accident, even from a bug. The isolation is enforced at the query layer, not at the application layer.

---

## Project status

Kehai is in active early development. The core product loop (events in, scores and narratives out, intervention list available) is working end to end. The Stripe billing integration and the dashboard frontend are the next planned milestones. The product is not yet generally available, but it is being tested with early design partners in the East African SaaS ecosystem.

If you are a B2B SaaS founder or a customer success leader interested in being one of the first Kehai users, get in touch. The first integrations will receive direct support from the founder.

---

## License

Proprietary. All rights reserved.

---