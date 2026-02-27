# 🛠️ Lab 12 — Troubleshooting Guide (External REST API Consumption with Camel)

> This guide captures realistic issues that can occur when consuming external REST APIs using Apache Camel, and how to fix them.

---

## ✅ Issue 1: Connection Timeouts / No Response from External API

### 🔥 Symptoms
- Camel logs show timeout errors
- Route stops processing or repeatedly fails
- curl requests hang or fail

### 🧠 Cause
- DNS/network issues on the VM
- External API temporarily unavailable
- Firewall/proxy restrictions
- Default connect/socket timeouts too low (or no explicit timeouts set)

### ✅ Fix (Connectivity Check)
```bash
ping -c 3 jsonplaceholder.typicode.com
````

Check HTTP reachability:

```bash
curl -I https://jsonplaceholder.typicode.com/posts/1
```

### ✅ Fix (Add Timeouts in Camel HTTP Endpoint URI)

```java
.to("https://jsonplaceholder.typicode.com/posts?bridgeEndpoint=true&connectTimeout=5000&socketTimeout=10000")
```

---

## ✅ Issue 2: HTTP 429 Rate Limiting from External API

### 🔥 Symptoms

* `CamelHttpResponseCode` shows **429**
* Logs show repeated failures when polling too frequently

### 🧠 Cause

The external API is rate limiting requests.

### ✅ Fix (Backoff / Delay)

In the API error handler route:

```java
.when(header("CamelHttpResponseCode").isEqualTo(429))
.log("Rate limit exceeded - implementing backoff")
.delay(5000)
```

### ✅ Best Practice

* Increase timer interval (`period`)
* Limit data size (`_limit=...`)
* Add retries with exponential backoff

---

## ✅ Issue 3: JSON Parsing Errors (Unmarshal Fails)

### 🔥 Symptoms

* Exceptions during `.unmarshal().json()`
* Stack trace mentions JSON parsing failures

### 🧠 Cause

* API returns non-JSON response (HTML, error string, etc.)
* Response body empty or unexpected format
* Wrong headers (missing Accept)

### ✅ Fix (Ensure Correct Headers)

```java
.setHeader("Accept", constant("application/json"))
```

### ✅ Fix (Validate Content-Type Before Unmarshalling)

```java
.choice()
  .when(header("Content-Type").contains("application/json"))
    .unmarshal().json()
  .otherwise()
    .log("Non-JSON response received: ${body}")
.end()
```

---

## ✅ Issue 4: Script Fails Due to Broken `tee` Line / Formatting

### 🔥 Symptoms

* `test-runner.sh` fails to run
* `tee` logs not created
* Syntax errors in script output

### 🧠 Cause

Multiline copy/paste breaks the command pipeline, especially around:

* `| tee test-output.log`

### ✅ Fix

Ensure this line is **single-line** and valid:

```bash
timeout $2 mvn exec:java -Dexec.mainClass="com.example.camel.RestConsumerApplication" 2>&1 | tee test-output.log
```

---

## ✅ Issue 5: Monitoring Route Shows Incorrect Response Time

### 🔥 Symptoms

* Response time logs show incorrect values
* `RequestStartTime` appears after the request
* Timing values print timestamps instead of milliseconds

### 🧠 Cause

Start time must be recorded **before** HTTP call, and calculated **after**.

### ✅ Fix (Correct Timing Flow)

* Set `RequestStartTime` before `.to("https://...")`
* After call, compute `ResponseTimeMs = now - start`

Example:

```java
.process(exchange -> {
    long startTime = System.currentTimeMillis();
    exchange.setProperty("RequestStartTime", startTime);
})
.to("https://jsonplaceholder.typicode.com/posts/1?bridgeEndpoint=true")
.process(exchange -> {
    long startTime = exchange.getProperty("RequestStartTime", Long.class);
    long responseTime = System.currentTimeMillis() - startTime;
    exchange.setProperty("ResponseTimeMs", responseTime);
})
.log("Response time (ms): ${exchangeProperty.ResponseTimeMs}")
```

---

## ✅ Issue 6: Maven Build Errors / Dependency Conflicts

### 🔥 Symptoms

* `mvn clean compile` fails
* Missing classes or version mismatch errors

### 🧠 Cause

* Dependency conflicts
* Local Maven cache issues
* Incorrect Camel version alignment

### ✅ Fix (Resolve Dependencies)

```bash
mvn dependency:resolve
```

### ✅ Fix (Clean Rebuild)

```bash
mvn clean
mvn compile
```

---

## ✅ Issue 7: Memory Issues During Execution

### 🔥 Symptoms

* Java process crashes
* `OutOfMemoryError`
* Maven exec hangs or fails

### 🧠 Cause

Low memory environment or heavy logging/large payload processing.

### ✅ Fix (Increase Memory for Maven/Java)

```bash
export MAVEN_OPTS="-Xmx512m -Xms256m"
mvn exec:java
```

---

## ✅ Operational Debugging Tips

### 🔍 Confirm API is reachable from the VM

```bash
curl -s -o /dev/null -w "%{http_code}" https://jsonplaceholder.typicode.com/posts/1
echo ""
```

### 🔍 Reduce payload size during testing

Use API limits:

* `?_limit=5`
* `?_limit=3`

### 🔍 Slow down polling intervals

Increase `timer` periods:

* `period=20000` → `period=60000` for less stress

---

## ✅ Next Steps (Production Hardening Ideas)

* Add retry policy with max redeliveries and delay (Camel error handling patterns)
* Add circuit breaker around external calls
* Use secure auth headers for real APIs (API keys, OAuth2)
* Export metrics for monitoring (Prometheus, Grafana)
* Add structured JSON logging
* Persist output into a real data store (DB/Kafka/S3)
