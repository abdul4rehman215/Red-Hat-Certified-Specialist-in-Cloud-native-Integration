# 🛠️ Troubleshooting Guide — Lab 17: Using EIPs for Scalable Integrations (Apache Camel)

> This troubleshooting guide covers **real issues encountered during the EIP lab** and how they were resolved.

---

## ✅ Issue 1: `code` command not found

### ❗ Problem
When trying to open files with VS Code CLI:
```text
bash: code: command not found
````

### ✅ Cause

The cloud terminal image did not include the VS Code CLI (`code`) binary.

### ✅ Fix

Use a terminal editor instead (same result):

```bash
nano pom.xml
```

---

## ✅ Issue 2: `mvn exec:java` fails to run the main class

### ❗ Problem

Maven cannot execute:

```bash
mvn exec:java -Dexec.mainClass="..."
```

### ✅ Cause

`exec-maven-plugin` was missing from `pom.xml`.

### ✅ Fix

Add exec plugin under `<build><plugins>`:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
</plugin>
```

Then rebuild:

```bash
mvn clean compile
```

---

## ✅ Issue 3: Compilation error — `log` is not defined (`log.info(...)`)

### ❗ Problem

Route uses `log.info(...)` inside lambdas, but compilation fails because there is no `log` variable.

### ✅ Cause

Camel’s `.log("...")` is not the same as a Java SLF4J logger.

### ✅ Fix

Add SLF4J logger to the RouteBuilder:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger(SplitterRoute.class);
```

Repeat the same approach for any class using `log.info(...)` (SplitterRoute, AggregatorRoute, RecipientListRoute).

---

## ✅ Issue 4: Aggregator never completes (messages stuck)

### ❗ Problem

Aggregation continues waiting and never triggers completion.

### ✅ Cause

* Missing completion conditions
* Incorrect correlation key (items grouped wrong)
* Expected item count does not match actual split count

### ✅ Fix

Use both completion size and timeout:

```java
.aggregate(header("correlationId"), new OrderAggregationStrategy())
.completionTimeout(5000)
.completionSize(4)
```

Also verify correlation header is always set in the splitter:

```java
exchange.getIn().setHeader("correlationId", originalOrderId);
```

---

## ✅ Issue 5: Incorrect or missing correlation headers

### ❗ Problem

Items do not aggregate into a single order, or aggregation groups are wrong.

### ✅ Cause

Correlation header not set or set inconsistently.

### ✅ Fix

Always propagate the original order ID:

```java
exchange.getIn().setHeader("originalOrderId", order.getOrderId());
exchange.getIn().setHeader("correlationId", originalOrderId);
```

---

## ✅ Issue 6: Recipient List only partially delivers (or hangs)

### ❗ Problem

* One endpoint failure causes inconsistent routing
* Routing hangs due to slow downstream endpoints

### ✅ Fix

Use reliability controls:

```java
.recipientList(method(OrderRecipientListResolver.class, "resolveRecipients"))
.parallelProcessing()
.stopOnException()
.timeout(10000)
```

This ensures:

* parallel dispatch for throughput
* stop-on-failure to avoid partial updates
* bounded waiting time

---

## ✅ Issue 7: REST route does not start / REST DSL errors

### ❗ Problem

REST DSL route fails to compile or start.

### ✅ Cause

Missing dependency for REST DSL support.

### ✅ Fix

Add Camel REST dependency:

```xml
<dependency>
  <groupId>org.apache.camel</groupId>
  <artifactId>camel-rest</artifactId>
  <version>${camel.version}</version>
</dependency>
```

Then rebuild:

```bash
mvn clean compile
```

---

## ✅ Issue 8: REST endpoint works, but order doesn’t process through EIPs

### ❗ Problem

`POST /api/eip/order` returns success but you don’t see split/aggregate/dispatch behavior.

### ✅ Cause

REST route is not running alongside EIP routes.

### ✅ Fix

Use a combined application that registers **all routes**:

* SplitterRoute
* AggregatorRoute
* RecipientListRoute
* EIPRestRoute

Run:

```bash
mvn exec:java -Dexec.mainClass="com.alnafi.camel.eip.CompleteEIPRestApplication"
```

---

## ✅ Quick Verification Checklist

### 1) Confirm app is running on port 8080

```bash
sudo lsof -i :8080
```

### 2) Test sample endpoint

```bash
curl -s http://localhost:8080/api/eip/sample
```

Expected output example:

```text
ORD-REST-1709047922011
```

### 3) Test order submission

```bash
curl -s -X POST http://localhost:8080/api/eip/order \
  -H "Content-Type: application/json" \
  -d @rest-order.json
```

Expected output example:

```text
Order submitted for processing
```

### 4) Verify EIP flow in logs

Look for:

* `Generating sample order...`
* `Splitting order into individual items...`
* `Aggregation completed for order: ...`
* `Recipients for order ...: direct:...`

---
