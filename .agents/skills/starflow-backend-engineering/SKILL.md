---
name: starflow-backend-engineering
description: Design, implement, or review Java/Spring backends using StarFlow-derived conventions for api/biz boundaries, common foundations, contracts, logging AOP, concurrency, messaging, and middleware reliability. Use for StarFlow-style work or reusable backend standards; not for unrelated local fixes.
---

# StarFlow Backend Engineering

Apply a reusable backend engineering language derived from StarFlow. Preserve its useful separation of contracts, business implementation, cross-cutting starters, asynchronous projections, and repair mechanisms without copying its product domain or known reliability gaps.

## Operating Principles

- Start from the target repository. Treat its build files, contracts, configuration, migrations, and implementation as facts; treat documentation as supporting evidence that may drift.
- Separate current behavior, proposed standard, and deliberate exception. Never present an existing compromise as a universal rule.
- Prefer explicit business states, ownership, failure semantics, and dependency direction over framework ceremony.
- Keep contracts narrow and models boundary-specific. Do not let persistence objects or one universal DTO cross every layer.
- Give every authoritative write one owner and one explicit commit point. Treat caches, search documents, counters, and read models as projections unless the design explicitly proves otherwise.
- Design remote calls and messages as failure-prone. Define timeout, idempotency, ordering, retry, compensation, observability, and replay where relevant.
- Keep `common` small and domain-neutral. Put opt-in cross-cutting behavior in focused starters or libraries rather than a miscellaneous utility module.
- Reuse the target project's stack and conventions when they are sound. Add middleware only when an access pattern, scale constraint, or failure model justifies it.
- Scale the analysis and artifacts to the request. A local endpoint does not need a platform redesign; a cross-service write does need a failure model.

## Workflow

1. Discover the affected modules, dependency direction, public and internal contracts, data owners, middleware, and execution model.
2. Classify the work: local use case, shared contract, cross-service workflow, asynchronous projection, common infrastructure, or production hardening.
3. Identify invariants before implementation: valid states, authorization, commit point, duplicate behavior, concurrency behavior, and client-visible result.
4. Select the smallest compatible pattern. Explain material tradeoffs when multiple patterns are defensible.
5. Implement within the owning module. Keep protocol adapters, business orchestration, pure transformation, persistence, and middleware concerns distinct.
6. Verify happy paths and the failures introduced by the design: validation, duplicate delivery, timeout, partial dependency failure, rollback, retry, and shutdown as applicable.

## Reference Routing

- For module splitting, `api`/`biz`, layer ownership, model roles, and elegant business code, read [references/modules-and-business-code.md](references/modules-and-business-code.md).
- For `common`, input and response models, validation, pagination, error codes, exceptions, and RPC contracts, read [references/contracts-and-common.md](references/contracts-and-common.md).
- For operation logging AOP, security redaction, exception logging, request context, and trace propagation, read [references/logging-and-context.md](references/logging-and-context.md).
- For executors, virtual threads, futures, batch consumers, rate limiting, backpressure, transactions, and reliable async work, read [references/concurrency-and-reliability.md](references/concurrency-and-reliability.md).
- For Redis, messaging, RPC/discovery, relational and specialized stores, object storage, scheduling, distributed IDs, and starter boundaries, read [references/middleware-patterns.md](references/middleware-patterns.md).
- For an architecture or code-health review, read [references/review-checklist.md](references/review-checklist.md) plus only the domain references implicated by the request.

## Cross-Boundary Contract

For each RPC, event, scheduled job, or cross-store write, make the relevant fields explicit:

- owner and caller/producer/consumer;
- request, response, or event schema and compatibility policy;
- authoritative state and derived state;
- authentication and authorization source;
- deadline, capacity limit, and degradation behavior;
- transaction or durability point;
- idempotency key and duplicate semantics;
- ordering key and stale-event behavior;
- retry limit, backoff, DLQ or failure ledger, and replay path;
- metrics, tracing, alerting, and reconciliation.

Do not force irrelevant fields into a simple local call. Do not omit them from a workflow whose correctness depends on them.

## Output Expectations

When designing, provide the minimum useful combination of module boundaries, dependency direction, model ownership, data ownership, a call/event flow, and a failure table. When implementing, make the code and focused tests the primary deliverable. When reviewing, lead with correctness, security, reliability, and contract findings before style observations.

Do not reproduce these known anti-patterns as StarFlow conventions: externally reachable internal RPC endpoints, trust in an unsigned identity header, full request/response logging, Redis-first best-effort MQ dual writes, acknowledging messages before durable acceptance, unbounded in-process buffers, copied unversioned event DTOs, or scheduled reconciliation used to hide a broken online commit path.
