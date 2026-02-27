# 🛠️ Troubleshooting Guide — Lab 19: Handling Message Failures with Apache Camel

> This troubleshooting guide focuses on **real issues and fixes** you’re likely to hit while testing retries, DLQ, and fallback behaviors in Camel.

---

## ✅ Issue 1: Application doesn’t start with `mvn exec:java`

### ❗ Symptoms
- Maven fails when running:
```bash
mvn exec:java -Dexec.mainClass="com.example.camel.ErrorHandlingApplication"
````

### ✅ Likely Causes

* Missing `exec-maven-plugin` in `pom.xml`
* Wrong main class name/package
* Build not compiled

### ✅ Fix

1. Ensure plugin exists in `pom.xml`:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
</plugin>
```

2. Build cleanly:

```bash
mvn clean compile
```

3. Confirm main class path:

```bash
mvn -q -Dexec.mainClass="com.example.camel.ErrorHandlingApplication" exec:java
```

---

## ✅ Issue 2: Input files remain in `input/*` and nothing happens

### ❗ Symptoms

* Files stay in input folder
* No logs about processing

### ✅ Likely Causes

* Application not running
* Route not started
* Wrong directory path
* File polling delay (you set delay=5000)

### ✅ Fix

1. Confirm app is running:

```bash
ps aux | grep ErrorHandlingApplication
```

2. Check log file:

```bash
tail -n 50 nohup.out
# or
tail -n 50 application.log
```

3. Wait at least 5 seconds due to:

```text
delay=5000
```

4. Confirm correct folder structure exists:

```bash
ls -la input/retry input/dlq input/fallback input/fallback-alt input/advanced
```

---

## ✅ Issue 3: Retry doesn’t seem to happen

### ❗ Symptoms

* Failure occurs but no retry logs
* Message fails immediately

### ✅ Likely Causes

* Exception is handled somewhere else
* Incorrect error handler configuration
* Route never reached failing processor

### ✅ Fix

1. Confirm `RetryRouteBuilder` has:

```java
errorHandler(defaultErrorHandler()
  .maximumRedeliveries(3)
  .redeliveryDelay(2000)
  .logRetryAttempted(true)
  .logExhausted(true));
```

2. Confirm logs show:

```text
[WARN ] ... Redelivery attempt: 1 due to ...
```

---

## ✅ Issue 4: DLQ messages don’t show headers (FailureReason etc.) in file output

### ❗ Symptoms

* `output/dlq/failed/*.txt` contains only original body
* You expected headers embedded

### ✅ Why This Happens

Camel File component writes **body** to the file by default. Headers are not automatically serialized into file content.

### ✅ Fix Options

* Keep as-is (accurate for file-based demo) and rely on logs for headers ✅
* OR explicitly format body to include headers before writing:

```java
.setBody(simple("Body=${body}\nReason=${header.FailureReason}\nTime=${header.FailureTimestamp}\n"))
```

(You did not implement this in the lab, so repo keeps original behavior.)

---

## ✅ Issue 5: Runtime error output folder stays empty (`output/advanced/runtime-errors/`)

### ❗ Symptoms

* Validation/general routes produce files
* Runtime errors folder often empty

### ✅ Cause

Your runtime exception handler uses:

```java
handled(false)
```

So after retries, the exchange is considered failed and may not complete a write to a file endpoint consistently (depending on where exception propagates).

### ✅ Fix (If You Want Runtime Errors Written Reliably)

Change to:

```java
handled(true)
```

and then route to runtime errors output.

⚠️ But in this lab, leaving `handled(false)` is a valid demonstration of failure propagation.

---

## ✅ Issue 6: Running in background doesn’t create `nohup.out`

### ❗ Symptoms

* You run:

```bash
mvn exec:java ... &
```

but `nohup.out` does not exist

### ✅ Cause

`nohup.out` is created by `nohup`, not by `&`.

### ✅ Fix

Redirect output explicitly (as done in the lab):

```bash
mvn exec:java -Dexec.mainClass="com.example.camel.ErrorHandlingApplication" > nohup.out 2>&1 &
```

---

## ✅ Issue 7: Cannot stop the running app cleanly

### ❗ Symptoms

* App running in background, terminal closed
* Need to stop it

### ✅ Fix

Find PID:

```bash
ps aux | grep ErrorHandlingApplication
```

Kill it:

```bash
kill -9 <PID>
```

---

## ✅ Issue 8: `watch` command output is messy or breaks due to quoting

### ❗ Symptoms

* Watch output not showing what you intended
* Path breaks in command

### ✅ Fix

Use single quotes around watch command:

```bash
watch -n 2 'ls -la output/dlq/success/ && echo "---" && ls -la output/dlq/failed/'
```

---

## ✅ Issue 9: `bc` not installed for performance script

### ❗ Symptoms

* Running `analyze-performance.sh` fails when computing percentages

### ✅ Fix

Install bc:

```bash
sudo apt-get update
sudo apt-get install -y bc
```

Verify:

```bash
which bc
```

---

## ✅ Issue 10: Load test generates messages but results don’t match expected counts

### ❗ Symptoms

* Random failures cause variation
* Counts differ from run to run

### ✅ Cause

`FailureSimulatorProcessor` uses randomness; outcomes will vary per run.

### ✅ Fix / Best Practice

This is expected. If you want deterministic tests:

* Replace randomness with fixed rule-based failures
* Or set a fixed seed in Random:

```java
private final Random random = new Random(42);
```

(You did not do this in the lab; random behavior is realistic for failure simulation.)

---

## ✅ Quick Verification Checklist

### 1) Confirm app is running

```bash
ps aux | grep ErrorHandlingApplication
```

### 2) Confirm processing logs exist

```bash
tail -n 50 nohup.out
tail -n 50 application.log
```

### 3) Confirm outputs exist

```bash
ls -la output/retry/
ls -la output/dlq/success/ output/dlq/failed/
ls -la output/fallback/primary/ output/fallback/fallback/
ls -la output/advanced/success/ output/advanced/validation-errors/ output/advanced/general-errors/
```

---
