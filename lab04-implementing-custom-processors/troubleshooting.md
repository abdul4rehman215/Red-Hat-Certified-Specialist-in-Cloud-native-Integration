# 🛠️ Troubleshooting — Lab 04: Implementing Custom Processors in Camel Routes

> This document covers common issues while implementing and testing **custom Apache Camel processors** in a Maven-based Java project.

---

## ✅ Quick Health Checks (Run These First)

### 1) Confirm Java + Maven
```bash
java -version
mvn -version
````

### 2) Confirm project structure exists

```bash id="r041j9"
ls -la
find src -maxdepth 4 -type d | head -50
```

You should see:

* `pom.xml`
* `src/main/java/...`
* `src/test/java/...`

---

## 🧩 Issue 1: Maven compile fails (package path mismatch)

### ❌ Symptoms

* Compilation errors like:

  * `package com.alnafi.camel.lab4... does not exist`
  * `cannot find symbol`
  * classes not found

### ✅ Fix Checklist

1. Confirm package lines match folder layout exactly.

Example package:

```java id="l6b6z0"
package com.alnafi.camel.lab4.processors;
```

Must be stored under:

```text id="lh22tq"
src/main/java/com/alnafi/camel/lab4/processors/
```

2. List Java files:

```bash id="0bq6ms"
find src/main/java -type f -name "*.java" -print
```

3. Rebuild:

```bash id="haep96"
mvn clean compile
```

---

## 🧩 Issue 2: `mvn exec:java` fails to run the app

### ❌ Symptoms

* “No plugin found for prefix ‘exec’”
* “Failed to execute goal exec-maven-plugin”

### ✅ Fix Options

#### ✅ Option A: Run with explicit command (same main)

```bash id="tja84e"
mvn exec:java -Dexec.mainClass="com.alnafi.camel.lab4.CustomProcessorApplication"
```

#### ✅ Option B: Add exec-maven-plugin to `pom.xml` (if needed)

Add this under `<plugins>`:

```xml id="3xuyv3"
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
</plugin>
```

Then:

```bash id="wij077"
mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.lab4.CustomProcessorApplication"
```

---

## 🧩 Issue 3: Application runs but logs are too quiet

### ❌ Symptoms

* You don’t see enough processor logs
* It looks like nothing happens

### ✅ Fix Checklist

1. Confirm `slf4j-simple` is included in `pom.xml`.
2. Ensure processors log using SLF4J:

```java id="rvqxzl"
logger.info("Processing message in custom processor");
```

3. Ensure routes log outputs via:

* `.log("Body: ${body}")`
* `.log("All Headers: ${headers}")`

---

## 🧩 Issue 4: Enrichment doesn’t happen for USER messages

### ❌ Symptoms

* Output shows `UNKNOWN_USER` for known users
* Expected enrichment did not apply

### ✅ Root Causes

* The enrichment logic expects messages starting with `USER`
* It extracts userId using:

```java id="dbbw2n"
String userId = originalMessage.split(" ")[0];
```

If your body is like `USER001` with no spaces, it still works.
But if your message doesn’t start with `USER###`, it will treat it as standard.

### ✅ Fix

Ensure your message begins with a userId pattern:

```text id="58u6ih"
USER001 requesting account balance
```

---

## 🧩 Issue 5: Priority header not set correctly

### ❌ Symptoms

* `Priority` always LOW
* Priority doesn’t match expected

### ✅ Root Cause

Priority depends on `MessageType` header.

### ✅ Fix

Send with the header:

```bash id="95c4qg"
# Example in app logic:
producer.sendBodyAndHeader("direct:enrich", "USER001 account inquiry", "MessageType", "USER_REQUEST");
```

Header mapping used:

* `USER_REQUEST` → HIGH
* `SYSTEM_MESSAGE` → MEDIUM
* else → LOW

---

## 🧩 Issue 6: Unit tests failing due to Exchange setup

### ❌ Symptoms

* Processor tests fail due to missing context/exchange issues

### ✅ Fix

Use a DefaultExchange with DefaultCamelContext:

```java id="08l3bg"
exchange = new DefaultExchange(new DefaultCamelContext());
```

Then set the body before calling `process()`:

```java id="lvh2p0"
exchange.getIn().setBody("hello world");
processor.process(exchange);
```

---

## 🧩 Issue 7: Integration tests failing (direct endpoints / output route confusion)

### ❌ Symptoms

* Tests hang or fail unexpectedly
* Mock endpoints conflict with existing route consumers

### ✅ Fix Strategy Used in This Lab

Instead of mocking `direct:output`, we validated behavior using:

* `template.requestBody(...)`
* and validating returned exchange/body/headers

This approach works well because:

* `direct:` endpoints remain in-memory
* the current exchange body can be asserted after route processing

---

## 🧩 Issue 8: Manual test script fails (`timeout` command missing)

### ❌ Symptoms

* Running script returns:

  * `timeout: command not found`

### ✅ Fix

Install coreutils:

Ubuntu:

```bash id="l6fd72"
sudo apt update
sudo apt install -y coreutils
```

RHEL/CentOS:

```bash id="oz1uiq"
sudo dnf install -y coreutils
```

Or remove the timeout wrapper and run normally.

---

## ✅ Safe Reset Steps (Clean Re-run)

If you want a clean run:

1. Clean and rebuild:

```bash id="k4gsdx"
mvn clean compile
```

2. Run the main application:

```bash id="6msd8m"
mvn exec:java -Dexec.mainClass="com.alnafi.camel.lab4.CustomProcessorApplication"
```

3. Run tests:

```bash id="nd7y7n"
mvn test
```

---

## ✅ Notes

* Keep processor logic **null-safe**
* Always log both body and key headers during debugging
* Ensure message header `MessageType` is used consistently to drive priority decisions
* Use unit tests for processor logic and integration tests for full route validation

✅ Troubleshooting guide complete.

```

---
