# Concurrency And Reliability

Use these rules for asynchronous work, virtual threads, futures, batch processing, consumers, rate limiting, and concurrent state changes.

## Classify Work Before Choosing An Executor

| Work class | Examples | Loss policy |
|---|---|---|
| Request-critical | required RPC aggregation, authorization | fail or explicitly degrade before response |
| Authoritative command | state transition, payment, event intent | must reach durable commit; never fire-and-forget |
| Derived best-effort | cache warming, optional decoration | may fail if source can rebuild it |
| Retry/replay | delayed provider or MQ retry | bounded attempts plus durable terminal record |
| Consumer batch | event-to-database projection | acknowledge only after durable acceptance |

Do not submit work to an executor until its loss policy is known.

## Executor Rules

- Create named executors per workload when latency, capacity, shutdown, or context behavior differs. Avoid a single ambiguous global executor.
- Declare the bean using the interface consumers inject, and qualify it when multiple executors exist.
- Configure or enforce concurrency limit, queue/buffer bound, rejection behavior, context propagation, metrics, and shutdown. Carry a deadline on call chains and tasks that require one; do not assume an executor alone enforces it.
- Close executors on application shutdown. For consumers, stop intake, drain within a bounded grace period, then surface unfinished durable work for retry.
- Test observable behavior that matters: virtual-thread execution, concurrency limit, rejection, context cleanup, and shutdown.
- Observe every submitted task: handle rejection, surface task exceptions, and record completion/failure. A discarded `Future` is not an error strategy. Required work must be awaited or converted into a durable job; best-effort work still needs a failure sink or metric.

## Virtual Threads

Use virtual threads for blocking I/O with libraries that are safe to run on them. They reduce thread cost; they do not increase database connections, sockets, provider quotas, heap, or downstream QPS.

Place a semaphore, bulkhead, connection-pool budget, or executor concurrency limit before each scarce dependency. Set total deadlines. Do not use an unbounded per-task virtual-thread executor as evidence of backpressure.

CPU-heavy work still needs a bounded executor sized for CPU. Pinning or long synchronized/native sections should be measured rather than assumed harmless.

## Futures And Parallel Aggregation

- Deduplicate keys and prefer a batch API before launching one future per item.
- Give the whole request a deadline budget, then allocate smaller deadlines to downstream calls.
- Cancel or abandon outstanding work after the deadline where the client supports it.
- Use partial results only for explicitly decorative data; mark or default them consistently.
- A required dependency failure must not be silently converted to an empty object.
- Keep `join/get` inside the failure boundary. Handle timeout, cancellation, completion failure, and rejected submission.

## Concurrent State Changes

Prefer atomic state transitions at the authoritative store:

- unique constraints for identity;
- compare-and-set using expected status or version;
- conditional update and affected-row inspection;
- `ON CONFLICT` with a condition for idempotent state projection;
- row or advisory locks only when optimistic techniques cannot preserve the invariant;
- Redis Lua for an atomic Redis-local check and mutation.

A Redis Lua script cannot make Redis and a database or MQ atomic. For cross-system correctness, persist a command/event intent with the authoritative transaction or use an equivalent durable protocol.

## Messaging And Acknowledgement

Classify messages before applying reliability machinery:

- **Domain fact or command**: correctness-bearing; use a stable `eventId`, schema version, aggregate key, occurred time, producer, durable publication, idempotent acceptance, retry/DLQ, and replay.
- **Reliable projection event**: rebuildable destination but required freshness; retain identity/version, durable broker acceptance, retry, stale-event protection, and a rebuild path.
- **Loss-tolerant hint or telemetry**: cache invalidation hint, sampling, or metrics may use lighter delivery only when loss and maximum staleness are explicit and a source-driven recovery exists.

Assume at-least-once delivery for correctness-bearing messages. Their envelope carries the business payload or a stable source reference.

Consumer order:

1. validate the envelope and compatibility;
2. establish trace and business context;
3. deduplicate or apply an idempotent state transition;
4. commit authoritative state and Inbox marker together where required;
5. create downstream event intent in the same transaction when chaining reliability matters;
6. acknowledge only after the defined durable point;
7. update rebuildable caches/projections after commit.

Throw or return the framework's retry result for transient failures. Route poison or exhausted messages to DLQ/failure storage with alerting and a controlled replay path. Catch-and-log followed by success is an acknowledgement, not error handling.

## Batch And Window Processing

Batch by count or time when the target store benefits, but bound both buffer and wait time. Rate limits charge actual rows/items, not one permit per batch. Merge events only when their semantics permit it; for absolute-state operations, preserving the last ordered operation per `(subject, target)` is often safer than parity cancellation.

In-memory aggregation after the broker callback returns is safe only for data explicitly allowed to be lost and rebuilt. For authoritative or economically important data, durably stage the event or aggregate before acknowledgement.

Use stable per-aggregate ordering keys rather than global ordering. Define stale-version handling because retries and parallel producers can still produce old events.

## Retry And Recovery

A robust retry chain has:

- retry eligibility based on operation idempotency and failure class;
- finite exponential backoff with jitter;
- a concurrency cap and retry budget;
- a stable idempotency/event key;
- durable failure storage or DLQ after exhaustion;
- alerting and an audited replay tool;
- a terminal state rather than infinite retry.

An effective pattern for non-transactional message publication is bounded immediate retry, then a durable failure ledger, then a scheduled replay job. Prefer transactional Outbox when the event must correspond exactly to an authoritative database commit.

Multiple replay workers must claim records atomically. Use a conditional state transition or `SKIP LOCKED`-style claim with owner, lease expiry, attempt count, and an `eventId` uniqueness guard. Expired leases are reclaimable; successful completion is conditional on the current owner. Consumer idempotency remains required but is not a substitute for bounded claiming.

## Transactions And Async Boundaries

Keep all same-database authoritative mutations for one invariant in one local transaction. Publish or schedule derived work after commit. Registering an after-commit callback avoids consumers seeing uncommitted state but does not make publication reliable; use Outbox when loss is unacceptable.

Never assume a transaction context crosses an executor, RPC, MQ, or another datastore. Write the partial-failure matrix and choose an explicit order, compensation, immutable version switch, or eventual repair strategy.

## Capacity And Rate Limits

Derive per-instance limits from the shared downstream budget and expected replica count. A process-local rate limiter multiplies total rate as replicas scale. Externalize safe dynamic thresholds, validate their range, and observe actual saturation, queue depth, rejection, timeout, and downstream latency.
