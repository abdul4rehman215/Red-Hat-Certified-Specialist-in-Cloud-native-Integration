# 🛠️ Troubleshooting Guide — Lab 16: Dynamic Routing with Apache Camel

> This file captures the **common issues encountered during dynamic routing labs** and the exact fixes used.

---

## ✅ Issue 1: JSON Parsing / JsonPath Predicate Not Working

### ❗ Problem
- `jsonpath()` expressions don’t match as expected
- Route falls into `.otherwise()` unexpectedly
- Logs show routing didn’t follow the intended branch

### ✅ Likely Cause
- JSON body is malformed (missing quotes, wrong key name)
- JsonPath syntax is incorrect
- Dependency `camel-jsonpath` missing (or wrong version)

### ✅ Fix
1) Ensure request JSON is valid and matches the fields used in routing:
```json
{"orderId":"ORD001","priority":"HIGH","amount":1000}
````

2. Confirm JsonPath matches your JSON structure:

```java
.when(jsonpath("$.priority[?(@ == 'HIGH')]"))
```

3. If you have nested objects, adjust accordingly:

```java
.when(jsonpath("$.order.priority[?(@ == 'HIGH')]"))
```

4. Alternative approach using simple (works only when body is mapped appropriately):

```java
.when(simple("${body[priority]} == 'HIGH'"))
```

---

## ✅ Issue 2: Route Not Found (Dynamic Destination Missing)

### ❗ Problem

* Errors suggesting a route/endpoint doesn’t exist
* Dynamic `recipientList()` tries routing to an endpoint that isn’t registered

### ✅ Likely Cause

* Config returns an endpoint that is not defined in routes
* A typo exists in properties (example: `direct:electronicsProcesor`)

### ✅ Fix

1. Validate the config routes match the actual route endpoints:

```properties
routing.electronics.endpoint=direct:electronicsProcessor
```

2. Add fallback handling:

```java
.choice()
 .when(header("targetRoute").isNotNull())
 .recipientList(header("targetRoute"))
 .otherwise()
 .log("No valid route found, using default")
 .to("direct:defaultProcessor")
.end()
```

3. Ensure default route exists:

```java
from("direct:defaultCategoryProcessor")
 .log("Processing general order");
```

---

## ✅ Issue 3: “No consumer available on endpoint …”

### ❗ Problem

Output like:

```text
No consumer available on endpoint: jetty://http://localhost:8080/orders. Exchange[].
```

### ✅ Likely Cause

This happens when the test script calls endpoints that are **not started** in the currently running RouteBuilder.

In this lab, the advanced class originally started only:

* `/products`
* `/complex-orders`
* `/time-sensitive`

But the test script also called:

* `/orders`
* `/customers`

### ✅ Fix

✅ Two correct options:

#### Option A — Run the correct class

If you want to test `/orders` and `/customers`:

```bash
mvn exec:java -Dexec.mainClass="com.example.routing.DynamicRoutingExample"
```

#### Option B — Add missing endpoints to the advanced class (What we did)

We added `/orders` and `/customers` routes to `AdvancedDynamicRouting` so the script works end-to-end in a single app run.

---

## ✅ Issue 4: Port 8080 Already in Use

### ❗ Problem

* App fails to start
* Jetty fails binding to port 8080

### ✅ Likely Cause

A previous Camel app is still running on `8080`.

### ✅ Fix

1. Stop the previous running app:

* Press:

```text
Ctrl + C
```

2. If you don’t have the terminal, find the process:

```bash
sudo lsof -i :8080
```

3. Kill the PID:

```bash
sudo kill -9 <PID>
```

4. Start your app again:

```bash
mvn exec:java -Dexec.mainClass="com.example.routing.AdvancedDynamicRouting"
```

---

## ✅ Issue 5: `mvn exec:java` Fails

### ❗ Problem

* Maven shows errors when running:

```bash
mvn exec:java -Dexec.mainClass="..."
```

### ✅ Likely Cause

* `exec-maven-plugin` missing in `pom.xml`
* Incorrect main class name
* Compilation has failed previously

### ✅ Fix

1. Confirm plugin exists in `pom.xml`:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
</plugin>
```

2. Verify main class spelling:

```bash
mvn -q -Dexec.mainClass="com.example.routing.AdvancedDynamicRouting" exec:java
```

3. Recompile cleanly if needed:

```bash
mvn clean compile
```

---

## ✅ Issue 6: Advanced Routing Behaves Like Defaults (Config Not Loaded)

### ❗ Problem

* Routes behave like hardcoded defaults
* Changing `routing-config.properties` has no visible effect

### ✅ Likely Cause

* File not placed in correct resource path
* Maven didn’t copy resources to classpath
* File name mismatch

### ✅ Fix

1. Ensure file is inside:

```text
src/main/resources/routing-config.properties
```

2. Rebuild:

```bash
mvn clean compile
```

3. Watch build logs for:

```text
Copying 1 resource
```

---

## ✅ Issue 7: Performance Test Runs Slowly / Timeouts

### ❗ Problem

* Requests appear slow
* App logs show delayed processing
* Test might take longer than expected

### ✅ Likely Cause

* Delays are intentionally added in routes:

```java
.delay(1000)
.delay(3000)
.delay(5000)
```

### ✅ Fix

* Reduce artificial delay for stress tests
* Increase executor threads or timeout if required

Example quick adjustment:

```java
.delay(100)
```

---

## ✅ Quick Verification Checklist

Run these to validate state quickly:

### Check if Java/Camel process is running

```bash
ps aux | grep java
```

### Confirm which routes are started (from logs)

Look for:

```text
Started routeX (jetty:http://localhost:8080/<endpoint>)
```

### Quick endpoint test

```bash
curl -X POST http://localhost:8080/time-sensitive \
  -H "Content-Type: application/json" \
  -d '{"requestId":"REQ001","type":"URGENT"}'
```

---
