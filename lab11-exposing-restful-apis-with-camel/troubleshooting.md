# 🛠️ Lab 11 — Troubleshooting Guide (Camel REST API)

> This document captures realistic issues that can occur when building and testing a Camel REST API on a Linux VM, along with fixes that were used (or recommended) during the lab.

---

## ✅ Issue 1: Port 8080 Already in Use

### 🔥 Symptoms
- Application fails to start
- Error mentions something like:
  - `Address already in use`
  - `BindException`
  - Jetty cannot bind to `0.0.0.0:8080`

### 🧠 Cause
Another process is already listening on port **8080** (could be another Java app, a previous run of this same service, or another web server).

### ✅ Fix
Check what is using port 8080:
```bash
sudo netstat -tulpn | grep :8080
````

Kill the process (replace `<process_id>`):

```bash
sudo kill -9 <process_id>
```

Re-run the application:

```bash
mvn exec:java -Dexec.mainClass="com.example.camel.CamelRestApplication"
```

---

## ✅ Issue 2: `jq: command not found` when running test script

### 🔥 Symptoms

When running `./test-api.sh`:

* `jq: command not found`

### 🧠 Cause

The test script uses `jq` to pretty-print JSON, but it is not installed by default on every Linux image.

### ✅ Fix

Install jq:

```bash
sudo apt-get update
sudo apt-get install -y jq
```

Re-run:

```bash
./test-api.sh
```

---

## ✅ Issue 3: JSON Parsing / Formatting Errors in curl

### 🔥 Symptoms

* API responds with `400`
* Camel may fail to bind request body into the User POJO
* curl command behaves unexpectedly due to malformed JSON

### 🧠 Cause

Invalid JSON formatting, missing commas, incorrect quotes, or multiline quoting issues.

### ✅ Fix (Validate JSON with jq)

```bash
echo '{"name":"test"}' | jq '.'
```

### ✅ Fix (Safer curl formatting)

Use single quotes around JSON body:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","age":28}'
```

---

## ✅ Issue 4: Application Won’t Compile (Java compilation errors)

### 🔥 Symptoms

* `mvn clean compile` fails
* Java errors in `UserRestRoute.java`

### 🧠 Cause

A common real-world issue:

* broken string literals (newlines inside quotes)
* missing imports
* syntax errors when copy-pasting code

### ✅ Fix

Re-open the file and confirm string literals are single-line:

```bash
nano src/main/java/com/example/camel/UserRestRoute.java
```

Re-build:

```bash
mvn clean compile
```

---

## ✅ Issue 5: Camel Application Starts But API Requests Fail

### 🔥 Symptoms

* API starts but curl gets:

  * connection refused
  * no response
  * timeouts

### 🧠 Possible Causes

* Application not running anymore (terminal closed)
* Wrong host/port
* Jetty not started successfully
* API path typo

### ✅ Fix Checklist

Confirm the app is still running:

* The `mvn exec:java ...` terminal must remain open.

Confirm endpoint:

```bash
curl -i http://localhost:8080/api/users
```

Confirm port open:

```bash
netstat -an | grep :8080
```

---

## ✅ Issue 6: curl not installed

### 🔥 Symptoms

* `curl: command not found`

### 🧠 Cause

curl is missing on some minimal VM images.

### ✅ Fix

```bash
sudo apt-get update
sudo apt-get install -y curl
```

### ✅ Alternative: Use wget (POST example)

```bash
wget -qO- --post-data='{"name":"test"}' --header='Content-Type:application/json' \
http://localhost:8080/api/users
```

---

## ✅ Issue 7: Checking performance and runtime health

### 🔎 Check running Java processes

```bash
ps aux | grep java
```

### 🔎 Monitor connections to port 8080

```bash
netstat -an | grep :8080
```

### 🔎 Log monitoring (if log file exists)

```bash
tail -f camel-rest-api.log
```

---

## ✅ Practical Notes from This Lab (Realistic Observations)

### 📌 DELETE returning 404 can be normal

If a user was already deleted earlier, running DELETE again correctly returns:

* HTTP 404

This confirms:

* API state is consistent
* deletion worked previously
* app logic handles missing resources properly

---
