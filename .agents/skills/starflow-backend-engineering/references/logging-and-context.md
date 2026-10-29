# Logging And Request Context

Use these rules when adding operation logging, exception handling, audit trails, request context, identity propagation, or trace correlation.

## Separate Log Purposes

- **Operation log**: a structured summary of a business operation and outcome.
- **Diagnostic log**: technical detail for investigation, sampled and level-controlled.
- **Security/audit log**: append-oriented evidence with stricter access, retention, and tamper controls.
- **Metric/trace**: aggregate behavior and cross-boundary latency; do not replace them with high-volume INFO lines.

Do not make one AOP interceptor serve all four purposes.

## Operation Logging AOP

An operation annotation should declare stable business intent, such as an operation code and description. The aspect owns mechanics. Default fields should include:

```text
timestamp, service, operation, traceId/requestId, actor/tenant,
duration, outcome, errorCode, selected business identifiers
```

The advice must:

1. capture start state;
2. call `proceed` inside `try`;
3. classify success or failure without changing the thrown exception;
4. emit the completion event from `finally` or equivalent;
5. tolerate serialization or logging backend failure without failing the business operation.

Assign one stack-trace owner per failure chain. Operation AOP records one structured outcome without duplicating the stack; the global unhandled-exception boundary owns the full stack for unexpected failures; a dependency adapter logs details only when it absorbs, retries, or materially translates the exception.

Default to metadata, not full arguments or results. Request or response body logging must be an explicit, reviewed opt-in with recursive redaction, depth and length limits, and type-specific summaries.

Never log complete passwords, verification codes, tokens, authorization or cookie headers, secrets, private keys, payment data, identity documents, or unrestricted personal data. Treat field-name redaction as a minimum, not sufficient protection. Multipart files should log safe metadata only; servlet objects, streams, and large binary values should never be serialized.

For MQ operations, log topic, tag, message/event ID, business key, attempt, duration, and outcome. Do not print the full body by default. For high-frequency ID generation, cache access, or per-record loops, use metrics or sampled DEBUG rather than INFO per item.

## Exception Logging

- Validation and expected business failures: stable code, safe path/operation, no secret rejected value, usually WARN or INFO according to expected frequency.
- Dependency timeout/unavailability: dependency, operation, deadline, attempt, and circuit state; avoid duplicated stack traces.
- Unknown failure: ERROR with stack trace and correlation identifiers; return a generic client error.
- Retry: log the first failure and terminal result; avoid one full stack trace per attempt unless diagnosing.

Logs should be structured and written to stdout in container environments. Rotation and collection belong to the platform. Local file appenders may remain a development option, not the only production sink.

## Request Context

Use a typed, preferably immutable context for user/workload identity, tenant, trace ID, request ID, locale, and idempotency key. Do not let an unbounded `Map<String,Object>` become a public contract.

At an inbound boundary:

- authenticate or verify the trusted upstream identity;
- remove or reject caller-supplied internal identity headers;
- populate context once;
- clear thread-local state in `finally`, including exceptional exits.

At an outbound boundary:

- propagate only required fields;
- sign or authenticate internal identity using workload identity, mTLS, or a verifiable token;
- propagate trace and deadline information;
- do not turn service discovery into an authorization mechanism.

Thread-local libraries alone do not prove context propagation. Request-scoped child tasks may copy a whitelist of context fields and must restore and clear them using the framework's supported mechanism. Detached or durable work such as retries, scheduled jobs, replays, and delayed events must persist only required metadata, establish a new trace when executed, and use an explicit system or delegated actor; never retain an old request ThreadLocal snapshot or credential. MQ producers should write correlation fields into headers or the event envelope; consumers establish a new scoped context and clear it after processing.

## AOP Boundary

Use AOP for uniform cross-cutting mechanics whose behavior is independent of domain decisions: timing, safe operation metadata, trace correlation, and standardized metrics. Do not hide authorization, transactions spanning remote calls, retries of non-idempotent commands, or business state changes inside a generic aspect.

Order interacting filters and aspects explicitly when correctness depends on it: identity and trace context must exist before operation logging; exception mapping must preserve outcome data; cleanup must run after all downstream work.
