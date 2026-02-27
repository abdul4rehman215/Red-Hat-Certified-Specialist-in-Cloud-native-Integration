# 🧠 Lab 12 — Interview Q&A (Consuming External REST APIs with Camel)

> These questions reinforce the most important ideas behind using Apache Camel as an API consumer: HTTP integration, JSON handling, resilience, and observability.

---

## 1) How does Apache Camel consume external REST APIs in this lab?
Camel consumes external REST APIs using the **HTTP component** (`camel-http`) by sending requests to external URLs via `.to("https://...")` endpoints, while controlling request behavior through headers like HTTP method and Accept.

---

## 2) Why is `Exchange.HTTP_METHOD` set in the route?
Because Camel needs to know which HTTP verb to use (GET/POST/etc.).  
Setting `Exchange.HTTP_METHOD` ensures the outgoing request matches the intended method (in this lab: mostly **GET**).

---

## 3) What does `bridgeEndpoint=true` do in Camel HTTP calls?
It tells Camel to treat the external URL as the endpoint without rewriting host headers or applying internal endpoint behavior.  
This is often used to avoid issues like unexpected host header mismatches or redirect problems when proxying/bridging.

---

## 4) Why are headers like `User-Agent` and `Accept` important for external API consumption?
Many APIs require or behave differently based on headers:
- `Accept: application/json` ensures JSON responses
- `User-Agent` helps identify the client and can avoid blocks or default restrictions
Headers also improve debugging and traceability.

---

## 5) What is the purpose of using `timer:` endpoints?
`timer:` triggers routes periodically.  
This is useful for:
- polling APIs
- scheduled ingestion pipelines
- recurring integrations (common in enterprise systems)

---

## 6) How was JSON processed in the routes?
The routes used:
- `.unmarshal().json()` to convert JSON into objects/maps
- `.split().jsonpath("$[*]")` to iterate over arrays
Additionally, a custom Processor handled more advanced transformations.

---

## 7) What role does `DataTransformProcessor` play?
It enriches and transforms the JSON:
- adds metadata like `processedAt` and `source`
- uppercases post titles
- computes word count for the body
- derives a category (PRIORITY / STANDARD)
- extracts email domain + full address for user objects  
This simulates real-world normalization/enrichment before storing data.

---

## 8) How did the lab simulate persistence/database operations?
It used `.process(...)` blocks that printed:
- `DATABASE INSERT: ...`
- `DATABASE INSERT USER (...) ...`  
This represents the point where real systems would call a database, message broker, or downstream service.

---

## 9) How did the lab implement error handling for external APIs?
It used:
- `onException(Exception.class)` for global error handling
- API error routes (`direct:handleApiError`) that inspect HTTP response codes
This is a standard integration pattern: detect error → route to a handler.

---

## 10) Why is handling HTTP codes like 404 and 429 important?
External APIs can fail or throttle:
- 404: resource missing (safe to skip/log)
- 429: rate limiting (should backoff/retry)
Ignoring these cases can break integrations or cause repeated failures.

---

## 11) What is the benefit of a test runner script like `test-runner.sh`?
It provides repeatable validation:
- runs the app for a controlled time using `timeout`
- captures output using `tee`
- verifies API availability using curl  
This is useful for CI/CD pipelines, troubleshooting, and audit evidence.

---

## 12) Why did the script use `tee test-output.log`?
`tee` writes output to:
- the terminal (live visibility)
- a log file (evidence + debugging)  
This creates a permanent trace of test execution.

---

## 13) What monitoring capability was added in this lab?
A monitoring route performed:
- periodic health checks
- response time measurement (ms)
- “HEALTHY/UNHEALTHY” logging based on HTTP status code
It also collected basic route/context statistics.

---

## 14) What was the realistic issue fixed in `MonitoringRoute`?
The timing logic must measure from **before** the HTTP call to **after** the response.  
The fix:
- store start time as an exchange property before `.to(...)`
- compute `ResponseTimeMs` after the call completes

---

## 15) What production improvements would you add to this integration?
Examples:
- retries with backoff for transient failures
- circuit breaker pattern (resilience4j / Camel EIP)
- proper authentication (API keys, OAuth2)
- structured logging (JSON logs)
- persistent storage (DB, Kafka, S3)
- metrics export (Prometheus/Grafana)
- centralized error queue / dead letter channel
