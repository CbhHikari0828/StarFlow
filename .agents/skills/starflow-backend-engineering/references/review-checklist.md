# Review Checklist

Use this checklist for system design, module design, or code-health reviews. Report evidence-backed findings in severity order. Do not fail a design merely for choosing a different technology when its invariants are sound.

## Boundaries

- Is each module's reason to change clear?
- Does every business table and authoritative command have one owner?
- Are dependency directions acyclic, with no service depending on another service's implementation module?
- Are external, internal, and administrative endpoints separated and authenticated?
- Is `common` domain-neutral and small? Are optional runtime concerns in focused starters?

## Contracts And Models

- Are VO, RPC DTO, event, BO, DO, cache, and search models distinct where their evolution differs?
- Are request size, nested values, batch limits, enums, and cross-field conditions validated?
- Are response states, empty collections, HTTP statuses, pagination, and error codes consistent?
- Are RPC and event schemas versioned and contract-tested?
- Can callers distinguish empty, rejected, timed out, unavailable, and internal failure?

## Business Correctness

- Are authorization and ownership checked at the authoritative boundary?
- Are state transitions conditional on expected state/version and verified by affected rows?
- Are duplicates, retries, concurrent updates, and stale events defined?
- Are transaction boundaries visible, and are remote calls kept out of long transactions?
- Is pure decision/transformation logic separated from I/O and tested?

## Logging And Security

- Does operation logging finish on both success and exception?
- Are bodies opt-in, recursively redacted, and length-limited?
- Are secrets, tokens, credentials, verification codes, sensitive identifiers, and large payloads excluded?
- Is identity verified rather than trusted from a raw header?
- Are logs structured and correlated across HTTP, async tasks, and MQ?

## Concurrency

- Is each asynchronous task classified as required, authoritative, retryable, or best-effort?
- Are executors named, bounded by scarce downstream capacity, context-aware, observable, and closed?
- Do futures have deadlines and explicit partial-result policy?
- Are buffers and queues bounded, with a defined rejection/backpressure strategy?
- Are rate limits calculated across replicas and charged by actual work?

## Messaging And Reliability

- Is the event durably tied to the authoritative commit when correctness requires it?
- Does each event have ID, version, aggregate/order key, idempotency, retry, DLQ, and replay semantics?
- Is broker acknowledgement after the durable acceptance point?
- Do duplicate deliveries avoid duplicate downstream effects?
- Are reconciliation jobs connected to real source events and rebuilding from authority?

## Data And Middleware

- Is each datastore chosen for an explicit access pattern?
- Are Redis, search, counters, and cache treated as authority only when the design proves durability?
- Are hot keys/partitions, connection budgets, message sizes, and deep pagination bounded?
- Are schema/index changes migratable without coordinated downtime?
- Are timeout, circuit, bulkhead, saturation metrics, backup, and recovery defined for critical dependencies?

## Tests And Operations

- Are constraints and SQL semantics tested against the real database?
- Are provider/consumer contracts, duplicate delivery, retry, DLQ, and replay tested?
- Are readiness, graceful shutdown, dependency degradation, and restart recovery verified?
- Does CI run tests and migrations before publishing an artifact?
- Are metrics, traces, alerts, dashboards, and runbooks aligned with failure modes?

## Review Output

For each material finding, state:

```text
severity | violated invariant | evidence | concrete failure | recommended direction | verification
```

Then summarize what should be preserved. Distinguish immediate correctness/security work from scalability improvements and optional architectural evolution.
