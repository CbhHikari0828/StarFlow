# Contracts And Common Foundations

Use these rules for shared libraries, input and response models, validation, exceptions, pagination, and synchronous service contracts.

## Common Is A Foundation, Not A Drawer

Code belongs in `common` only when it is stable, domain-neutral, dependency-light, and already useful across independent domains. Good candidates include:

- response and paging primitives;
- the base error contract;
- serialization configuration hooks without global mutable state;
- generic validation infrastructure;
- stable technical constants;
- stateless algorithms whose semantics are genuinely domain-independent.

Keep product rules, localized presentation, domain identifiers, business enums, provider SDKs, and feature-specific helpers in their owning module. Repeated code is evidence to investigate, not sufficient proof that it belongs in `common`.

Use focused starters for optional cross-cutting runtime behavior such as request context, operation logging, RPC client configuration, tracing, or idempotency. A starter should:

- expose typed properties;
- use conditional auto-configuration;
- create replaceable beans;
- avoid scanning broad application packages;
- have no domain behavior;
- document ordering and failure behavior;
- include a minimal auto-configuration test.

## Input Contracts

Boundary validation should reject invalid work before business I/O:

- required/blank/null semantics;
- string and binary size;
- numeric ranges and pagination limits;
- collection size and nested element validation;
- enum values and supported combinations;
- URL, identifier, date, and locale format;
- batch size, upload size, and message-size compatibility.

Use a class-level validator or business policy for cross-field rules. Keep semantic checks that require current state, permissions, or a repository in the use-case layer. Custom validators should do one job; let `@NotNull` handle null if the format validator is meant only to validate non-null values.

Never bind persistence objects directly from untrusted input. Treat validation messages as client-facing API text only if they are stable and safe; do not echo secrets or internal diagnostics.

## Response Contract

If the system uses a response envelope, keep its states internally consistent. A practical shape is:

```text
success | code | message | data | requestId/traceId (optional)
```

Rules:

- construct envelopes through factories or immutable variants so success and failure fields cannot contradict each other;
- successful collections return `[]`, not `null`; absence of one optional resource should have a deliberate representation;
- a command with no result may return success with no data; do not invent placeholder booleans;
- error codes and messages are stable API contracts; stack traces, SQL text, class names, and provider errors remain internal;
- use meaningful HTTP statuses for validation, authentication, authorization, conflict, throttling, dependency failure, and server failure instead of encoding every outcome as HTTP 200;
- do not wrap streaming responses, downloads, SSE, protocol handshakes, or health endpoints when an envelope breaks the protocol;
- include a request or trace identifier when support workflows can use it, but avoid decorative fields with no consumer.

For an asynchronous command, return ordinary success only after the command or event intent reaches its declared durable acceptance point. If processing is still pending, use the protocol's accepted/pending semantics, such as HTTP `202 Accepted`, and return a stable `operationId` or `eventId` with a status-query or callback contract. A best-effort async send is not successful command acceptance.

## Pagination

Use offset/page pagination for bounded administrative datasets where exact totals matter. Use a stable cursor for large, hot, or continuously changing datasets.

A cursor must use a deterministic sort with a unique tie-breaker, commonly `(business_sort, id)`. Define whether the cursor is inclusive, how filters are bound to it, and what happens when records change between pages. Limit `pageSize` at the boundary.

For page responses, keep `items`, page/cursor metadata, and optional totals unambiguous. Avoid inheriting an envelope in ways that allow impossible states if composition is clearer.

## Errors And Exceptions

Give each service or bounded context a stable error-code namespace. Distinguish at least:

- invalid request;
- unauthenticated or forbidden;
- not found;
- conflict or illegal state transition;
- rate or capacity limit;
- downstream unavailable or timeout;
- internal system failure.

Business exceptions should be immutable, call `super(message, cause)` as appropriate, and retain the stable code. Unknown exceptions are logged with a stack trace and mapped to a generic client message. Expected business failures normally do not need a stack trace at WARN on every layer.

Keep the framework-neutral error contract and exception base in `common`. Put `@RestControllerAdvice`, HTTP status mapping, and Web-framework parameter adapters in a focused web-error starter when services share the same stack, while allowing each service to contribute its code catalog and specialized handlers. Avoid copied `GlobalExceptionHandler` classes that drift.

Log an exception with its stack trace once at the layer that has enough context to act. Lower adapters may enrich and rethrow; higher layers should not repeatedly log the same failure.

## RPC Contracts

Synchronous RPC is for information or decisions required before the current response can be correct. Its API contract should define:

- endpoint visibility: external, authenticated external, or internal only;
- authentication and workload identity;
- request and response limits, including batch maximum;
- connection, read, and total deadline;
- idempotency and retry eligibility;
- error mapping and which failures may degrade;
- compatibility and deprecation policy.

Keep service discovery names with the public contract when consumers need them. Register typed clients centrally; do not concatenate URLs in business code.

An RPC adapter must distinguish a legitimate empty result, a business rejection, a timeout, and an unavailable dependency. Returning `null` for all four cases destroys the caller's ability to make a correct decision. Decoration data may degrade to an explicit default or partial result; correctness-critical data should fail fast with a typed error.

Shared API jars provide compilation reuse, not runtime compatibility by themselves. Add provider/consumer contract tests and an explicit evolution policy. Avoid breaking field changes merely because all code currently lives in one repository.

## Serialization

Use one configured serializer per application context and inject it. Avoid mutable global serializers initialized in unspecified order. Make time, enum, unknown-field, numeric precision, and null behavior explicit for external contracts and events. Cache serialization may evolve independently and should carry a format version when long-lived data must survive deployments.
