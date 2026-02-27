# 🎤 Interview Q&A — Lab 07: Error Handling and Dead Letter Queues (DLQ)

## 1) Why is error handling important in Apache Camel routes?
Because Camel routes often integrate multiple external systems (APIs, queues, databases). Failures are expected in production, so error handling ensures resilience, prevents message loss, and supports troubleshooting and recovery.

## 2) What does `onException()` do in Camel?
`onException()` defines how Camel should handle specific exception types. It can configure retries, delays, backoff strategies, routing to error channels (DLQ), logging, and whether the exception is considered handled.

## 3) What is the difference between `handled(true)` and `handled(false)`?
- `handled(true)` means Camel treats the exception as handled, stops propagating it, and continues the route error handling flow.
- `handled(false)` means Camel will rethrow the exception after retry attempts (or immediately) and propagate failure to the caller.

## 4) What is a Dead Letter Queue (DLQ)?
A DLQ is a queue where messages are routed when they cannot be processed successfully after retries. It preserves failed messages for later analysis, replay, or manual remediation.

## 5) Why did you separate DLQs by error type (Runtime vs Validation vs Security)?
Because different failures require different operational responses:
- **Runtime/transient errors**: retry first, then DLQ
- **Validation errors**: no retry, immediate DLQ (payload issue)
- **Security/auth errors**: no retry, immediate DLQ (policy/credentials)

This improves triage speed and reduces noise.

## 6) What is exponential backoff and why is it useful?
Exponential backoff increases delay between retries after each failure. It reduces load on unstable systems and avoids retry storms (hammering a failing service).

## 7) What is the purpose of `maximumRedeliveries()`?
It controls how many retry attempts Camel makes before giving up. After the retry count is exhausted, Camel can route to a DLQ or propagate the exception depending on configuration.

## 8) Why should validation errors typically not be retried?
Because validation failures are usually permanent (bad format, missing required fields). Retrying the same invalid payload wastes resources and delays other processing.

## 9) How did you simulate real failures in this lab?
Using message keywords that trigger exceptions in service beans:
- `NETWORK_ERROR`, `TIMEOUT_ERROR` → RuntimeException
- `VALIDATION_ERROR`, `INVALID_FORMAT` → IllegalArgumentException
- `AUTH_ERROR` → SecurityException
- `PERMANENT_FAILURE` → RuntimeException (permanent)

## 10) Why did you configure JMS using `vm://localhost`?
`vm://localhost?broker.persistent=false` provides an in-memory broker useful for lab/testing scenarios. It avoids external broker setup while still demonstrating JMS queue behavior.

## 11) What is the benefit of adding headers like `ErrorCategory`, `RetryCount`, and `FailureTime`?
They provide context for investigations and automation:
- what type of failure occurred
- how many retries happened
- when the failure occurred
This supports operational reporting and faster root cause analysis.

## 12) How did you monitor DLQ messages in this lab?
By creating consumers/routes that read from DLQ queues and persist messages to files under `output/dlq/*`. This creates a clear audit trail of failed messages.

## 13) What is the main difference between the basic retry lab and the DLQ lab part?
- Basic retry part focuses on `direct:` routes and local file outputs.
- DLQ part uses JMS queues, routes failures into DLQ queues, and adds monitoring routes that persist DLQ artifacts.

## 14) Why is it useful to generate periodic DLQ statistics?
It provides visibility into system health and failure trends over time (how many runtime/validation/security failures exist). In production, this can trigger alerts or dashboards.

## 15) What enterprise best practices for error handling did you apply here?
- classify exceptions by category
- retry only transient failures
- use exponential backoff
- send unrecoverable messages to DLQ
- preserve failed payloads for investigation
- attach diagnostic metadata
- implement monitoring and periodic reporting
