# 🛠️ Troubleshooting Guide — Lab 07: Error Handling and Dead Letter Queues (DLQ)

> This guide documents common problems encountered while implementing retry logic and DLQ patterns in Apache Camel, along with practical fixes.

---

## ✅ Issue 1: `cd /home/student/camel-labs` fails (path not found)

### **Problem**
```bash
cd /home/student/camel-labs
# -bash: cd: /home/student/camel-labs: No such file or directory
````

### **Likely Cause**

The cloud VM user and home layout is different (`/home/toor` instead of `/home/student`).

### **Fix**

Create and use the correct working directory:

```bash
mkdir -p /home/toor/camel-labs
cd /home/toor/camel-labs
pwd
```

---

## ✅ Issue 2: `mvn exec:java` fails with “No plugin found for prefix 'exec'”

### **Problem**

Running:

```bash
mvn exec:java -Dexec.mainClass="..."
```

fails with plugin errors.

### **Likely Cause**

`exec-maven-plugin` is missing from `pom.xml`.

### **Fix**

Add the plugin:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
</plugin>
```

---

## ✅ Issue 3: JMS queue routes fail (cannot connect / no consumer)

### **Problem**

JMS producer sends messages but route doesn’t process, or you see connection errors.

### **Likely Cause**

* JMS component not added to Camel context
* Broker URL mismatch
* JMS dependencies missing

### **Fix**

Ensure JMS is configured in code before starting routes:

```java
JmsConfig.configureJms(context);
```

And verify the broker URL used:

```java
connectionFactory.setBrokerURL("vm://localhost?broker.persistent=false");
```

---

## ✅ Issue 4: Messages are not appearing in DLQ even after retries

### **Problem**

You expect a message to reach DLQ after retries, but no DLQ artifacts appear.

### **Likely Cause**

* Exception type doesn’t match your `onException(...)` clauses
* `.handled(false)` propagates exception without DLQ routing in that specific route
* Message gets processed successfully before retries exhaust

### **Fix**

* Confirm exception type triggers the correct handler:

  * `RuntimeException` → runtime DLQ
  * `IllegalArgumentException` → validation DLQ
  * `SecurityException` → security DLQ (in comprehensive routes)

* Ensure DLQ is configured in the relevant route builder:

```java
.deadLetterUri("jms:queue:DLQ.RuntimeErrors")
```

---

## ✅ Issue 5: Validation errors retry unexpectedly

### **Problem**

A payload error keeps retrying even though it should stop immediately.

### **Likely Cause**

Validation exceptions thrown as `RuntimeException` instead of `IllegalArgumentException`.

### **Fix**

Throw the correct exception type for validation:

```java
throw new IllegalArgumentException("Invalid message format");
```

And ensure:

```java
.maximumRedeliveries(0)
```

---

## ✅ Issue 6: DLQ monitoring files not created

### **Problem**

DLQ queues receive messages but file outputs don’t appear under `output/dlq/...`.

### **Likely Cause**

* Output directories not created
* File component cannot write (permissions)
* Route not started / consumer not active

### **Fix**

Create directories before starting Camel:

```java
Files.createDirectories(Paths.get("output/dlq/runtime-errors"));
Files.createDirectories(Paths.get("output/dlq/validation-errors"));
Files.createDirectories(Paths.get("output/dlq/security-errors"));
Files.createDirectories(Paths.get("output/dlq/unexpected-errors"));
```

Then confirm routes started in logs.

---

## ✅ Issue 7: `start-broker.sh` doesn't seem to affect JMS behavior

### **Problem**

You run `start-broker.sh` but behavior is unchanged.

### **Likely Cause**

This lab uses:

```text
vm://localhost?broker.persistent=false
```

which is an in-memory broker mode and does not strictly require an external broker process.

### **Fix**

This is expected. Keep the script for completeness, but rely on `vm://` for local lab execution.

---

## ✅ Issue 8: DLQ stats file counts look “too high”

### **Problem**

DLQ statistics show higher counts than expected.

### **Likely Cause**

Counts are cumulative because multiple runs/lab parts generated files inside the same `output/` directory.

### **Fix**

Either:

* treat counts as cumulative (valid for repeated runs), OR
* reset outputs before rerun:

```bash
rm -rf output/
mkdir -p output/success output/dlq/runtime-errors output/dlq/validation-errors output/dlq/security-errors output/dlq/unexpected-errors
```

---

## ✅ Issue 9: Random failures make results inconsistent (RANDOM_ERROR)

### **Problem**

Sometimes messages succeed, sometimes fail.

### **Likely Cause**

Randomized failure simulation (`Random`) intentionally creates non-deterministic behavior.

### **Fix**

For deterministic tests:

* remove random logic, OR
* lower failure probability, OR
* repeat runs and observe statistical behavior (realistic for stress testing)

---

## ✅ Quick Verification Checklist

Run these to confirm your setup is healthy:

```bash
java -version
mvn -version
mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.errorhandling.RetryTestApplication"
mvn exec:java -Dexec.mainClass="com.alnafi.camel.errorhandling.DLQTestApplication"
```

---
