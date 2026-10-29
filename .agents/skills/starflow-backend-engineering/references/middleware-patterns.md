# Middleware Patterns

Choose middleware from business semantics and failure requirements. This document describes roles and mandatory contracts, not a required StarFlow technology list.

## Decision Frame

Before selecting a component, state:

- access pattern and expected cardinality;
- source of truth and write owner;
- consistency and maximum acceptable lag;
- throughput, latency, hot-key or hot-partition risk;
- durability, availability, and recovery requirements;
- operational ownership and rebuild path.

Using fewer well-operated components is usually better than adding a specialized store without a proven access pattern.

## Relational Database And Mapper Layer

Use a relational database for transactional authority, constraints, state machines, and joins within one ownership boundary.

- One service or bounded context owns writes to each business table.
- Enforce invariants with constraints in addition to application checks.
- Use unique keys, conditional updates, and affected-row results for concurrency and idempotency.
- Batch high-frequency paths and select only required fields.
- Keep multi-table invariants in one local transaction.
- Use dedicated projection types for partial selects.
- Manage schema with versioned migrations and test forward/backward deployment compatibility.
- Budget total connections across every replica and job, not one service in isolation.

Cross-domain reads through a shared database can be an explicit small-team or migration compromise. Document the coupling and do not let other services mutate the owner's tables.

## Redis

Use Redis for rebuildable low-latency state, bounded ordered collections, counters with a defined authority, rate limiting, and atomic Redis-local operations.

- Build keys centrally from `system:domain:purpose:identity[:version]`; do not concatenate ad hoc strings in business code.
- Match structures to semantics: String/JSON for snapshots, Hash for independently updated fields, ZSet for bounded ranking or recency, Bitmap for dense exact membership, probabilistic structures only when false positives are acceptable.
- Define cache-aside order, miss behavior, null sentinel, TTL jitter, maximum staleness, refresh, and invalidation.
- Use L1 only when cross-instance invalidation or version checking is defined.
- Use pipelines/bulk operations after deduplicating keys.
- Use string-safe serializers for Lua and exact numeric/member protocols.
- Put multi-step Redis-local decisions in Lua and return explicit status codes. Pass thresholds as arguments.
- Mitigate hot-key rebuild with single-flight, stale-while-revalidate, logical expiry, or versioned swap as scale requires.

Redis is not an authoritative command log merely because it is fast. Do not mutate Redis and then best-effort publish the only durable event.

## Message Broker

Use a broker for delayed side effects, fan-out, peak smoothing, asynchronous projections, and workflows whose client response does not require immediate completion.

Define for every event:

```text
topic/type, schema version, eventId, aggregateId, ordering key,
producer, consumers, idempotency, retry/DLQ, retention, replay, observability
```

- Preserve order only within the smallest aggregate that needs it.
- Use unique constraints, Inbox records, fixed storage IDs, or conditional Upsert for idempotency.
- Emit downstream effects only for a real state transition, not a duplicate delivery.
- Use Outbox or a broker transaction protocol for an event coupled to a local commit, and inspect commit/rollback/unknown outcomes.
- Keep large bodies in object/content storage and put references plus integrity/version data in events.
- Store event contracts centrally or in a schema registry; copied DTOs and topic strings drift.
- Make DLQ and replay operationally usable, not merely configured.

## RPC, Discovery, And Configuration

Use synchronous RPC only when the current result needs a downstream fact or decision. Prefer typed interfaces and one client configuration path.

The RPC foundation should provide:

- service discovery and load balancing;
- connection/read/total deadlines;
- trace, request, tenant, and verified identity propagation;
- per-downstream concurrency isolation;
- circuit breaking and metrics;
- retry only for safe idempotent operations;
- request/response size limits.

Discovery and configuration systems do not authenticate callers. Internal endpoints need workload identity and network policy. Dynamic configuration is for parameters safe to change at runtime; validate ranges and define refresh behavior. Secrets belong in a secret manager or protected environment injection, not general configuration storage.

## Search Engine

Treat search as a versioned, rebuildable projection.

- Events carry entity ID and version; the consumer may reload authoritative state and fully replace or delete the document.
- Reject stale versions so an old retry cannot overwrite a new document.
- Use stable pagination (`search_after`/PIT where applicable) rather than unbounded deep offsets.
- Rebuild into a new index and atomically switch an alias.
- Define mappings, analyzers, replicas, snapshots, timeout, and maximum result window per environment.

Never make search the only copy of business state.

## Wide-Column Or Content Store

Use a wide-column store for high-volume data with known query patterns, such as large content or time-bucketed records.

- Model tables from queries, not relational normalization.
- Generate stable IDs so retries overwrite or deduplicate predictably.
- Estimate partition size and hot-partition risk; add time or hash buckets when needed.
- Define consistency level, tombstone behavior, retention, repair, and orphan cleanup.
- For cross-store writes, state the write order and accepted orphan/missing-data outcome. Prefer immutable versions and pointer switching for updates.

## Object Storage

Hide providers behind a narrow strategy/port and typed configuration, but keep business policy above the provider adapter.

Define content type and size validation, checksum, naming, authorization, upload timeout, lifecycle, malware/media processing, reference tracking, and orphan cleanup. Prefer direct/presigned upload for large objects when it fits the threat model. Do not treat a provider URL as permanent business identity.

## Scheduler And Reconciliation

Use a scheduler for retry, reconciliation, compaction, and operational workflows that are safe to rerun.

- Jobs must be idempotent, partitionable, resumable, observable, and bounded.
- Record cursor/checkpoint and drift metrics.
- Delete a repair ledger entry only after every required target succeeds.
- Rebuild from authoritative data rather than compounding a possibly wrong projection.

Reconciliation is a safety net, not permission to acknowledge work before durability or leave the real-time producer disconnected.

## Distributed IDs

Choose database-generated, UUID-like, or trend-increasing IDs according to write distribution, ordering, exposure, and migration needs. A remote ID service adds a critical synchronous dependency; justify it against an embedded/segment allocator.

Name generators by domain, reserve ranges safely, monitor segment allocation, and validate migration offsets against existing data. Record aggregate metrics rather than logging every generated ID.

## Cross-Cutting Starters

Place reusable runtime integration in focused starters: context, logging, RPC, tracing, serialization, or idempotency. Keep each starter conditional, typed, replaceable, and independently tested. Do not combine unrelated middleware or domain logic into one "all-in-one" starter.

The business module owns provider SDKs and clients. The API module owns only stable contracts. Common foundations must not become a backdoor through which every service acquires every middleware dependency.
