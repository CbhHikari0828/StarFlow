# Modules And Business Code

Use these rules when defining module boundaries, package responsibilities, domain models, or use-case code.

## Module Shapes

Split a service into `api` and `biz` only when another independently built module or service consumes its contract. Do not create an empty `api` module for symmetry.

### `*-api`

The API module is a lightweight, stable compile-time contract. It may contain:

- HTTP/RPC interfaces;
- request and response DTOs;
- boundary validation annotations;
- service-name and protocol constants;
- explicitly versioned public event schemas when no separate event-contract module is warranted.

It must not contain controllers, service implementations, persistence objects, mappers, cache code, MQ clients, database drivers, provider SDKs, or dependencies on `biz`. Framework types needed only for annotations should be `provided` or their build-system equivalent when practical.

### `*-biz`

The business module owns execution and data. It contains the application entrypoint, controllers, use-case services, domain/internal models, repositories or mappers, RPC adapters, consumers, caches, middleware configuration, and migrations owned by the service.

It may depend on its own `api`, shared foundations, and other services' `api` modules. It must never depend on another service's `biz` module.

Keep internal and external endpoints distinguishable in routing, authorization, and deployment. A Java comment saying "internal" is not a security boundary.

Keep provider implementation aligned with its API contract. When the framework supports it cleanly, let the provider implement or reuse the declared contract rather than copy paths and signatures. Otherwise add provider contract tests that verify routing, method, validation, serialization, and response compatibility.

## Dependency Direction

Use this default direction:

```text
transport adapters -> application/use cases -> domain decisions
                         |                    |
                         v                    v
                 outbound ports       pure policies
                         |
                         v
             persistence/RPC/MQ adapters
```

Spring-style layering can remain pragmatic, but protocol and infrastructure details should not leak into pure business decisions.

## Layer Responsibilities

- **Controller**: decode the protocol, trigger Bean Validation, obtain trusted request context, call one use case, and map the result. Do not query a mapper, manipulate Redis, or orchestrate several downstream clients here.
- **Application/use-case service**: enforce authorization and business invariants, coordinate dependencies, own the local transaction boundary, and return a business result. Prefer capability-focused services over a large entity-wide service.
- **RPC adapter**: translate local intent into a remote DTO, apply the deadline, interpret the remote envelope, classify failures, and implement only an explicitly allowed fallback. Business code should not repeatedly inspect remote `Response<T>` objects.
- **Assembler/converter**: perform deterministic, side-effect-free model conversion, defaults, and derivation. Keep I/O out so edge cases are easy to test.
- **Repository/mapper**: expose narrow persistence operations whose names communicate locking, idempotency, projection shape, and affected-row semantics. Do not return a partially populated general-purpose DO when a dedicated projection type is safer.
- **Consumer/job**: adapt an event or schedule into a use case. It owns acknowledgement and retry semantics, not unrelated domain logic.
- **Configuration**: create typed clients, executors, serializers, and policies. Avoid scattering client construction across services.

## Model Vocabulary

Use boundary-specific models and map explicitly:

| Model | Meaning | Must not become |
|---|---|---|
| `ReqVO` / `RspVO` | External client request and response | Persistence or cross-service contract |
| `ReqDTO` / `RspDTO` | Synchronous cross-service contract | Database record |
| `*Event` / `*MqDTO` | Versioned asynchronous contract | Reused RPC DTO |
| `BO` / command / result | Internal business computation | Wire format by accident |
| `DO` / entity record | Persistence representation | Controller response or shared API type |
| `*CacheDTO` | Versioned cache snapshot | Authoritative domain object |
| `*Document` | Search/read projection | Source of truth |

These suffixes describe roles, not mandatory names. Map them to the target repository's established vocabulary such as `Command`, `Result`, `View`, `Entity`, or `Record`; preserve the boundary and dependency rule instead of performing cosmetic renames.

Avoid an all-purpose DTO that accumulates transport, persistence, cache, and event fields. Avoid exposing every setter when a model has meaningful invariants.

## Business Elegance

- Name operations after business intent and state transitions, not CRUD mechanics: `publish`, `approve`, `reserve`, `settle`, or `revoke` convey more than `update`.
- Represent finite states and operations with enums or sealed types. Centralize parsing and reject unknown values; do not spread magic integers and strings.
- Validate syntax and size at the boundary, then enforce cross-field and state-dependent rules in the use case or a dedicated policy.
- Use guard clauses for invalid identity, ownership, state, and dependency results before mutating state.
- Make state transitions conditional on the expected prior state or version. Use affected-row count to distinguish success, duplicate, conflict, and missing data.
- Separate pure decision/assembly code from I/O orchestration. Pure code should be directly unit-testable.
- Batch remote and persistence operations after deduplicating keys. Avoid N+1 calls and queries hidden inside mapping loops.
- Prefer immutable commands, results, and events. Mutation is acceptable inside a tightly scoped assembler or persistence object when the framework requires it.
- Keep transaction boundaries visible. Do not hold a database transaction open across slow remote calls unless the tradeoff is explicit and bounded.
- Comments should explain invariants, failure choices, ordering, compatibility, and non-obvious reasons. Do not narrate obvious assignments.

## Complexity Signals

Split a service or use case when a class combines several independent capabilities, owns unrelated caches and topics, or cannot be tested without constructing most of the service. A large class is a signal, not an automatic violation; split by business capability and change reason rather than by arbitrary line count.

Do not abstract after the first similarity. Promote code only when the invariant and variation points are understood. Prefer two clear local implementations over a misleading generic framework.

## Tests By Boundary

- Pure policies, assemblers, enum parsing, and merge algorithms: fast unit tests.
- Mapper locking, constraints, upserts, and migrations: real database integration tests.
- RPC adapters and envelopes: consumer/provider contract tests plus timeout and fallback tests.
- Consumers: duplicate, out-of-order, retry, poison message, and transaction tests.
- Controllers: validation, authorization, HTTP status, and stable response-shape tests.
