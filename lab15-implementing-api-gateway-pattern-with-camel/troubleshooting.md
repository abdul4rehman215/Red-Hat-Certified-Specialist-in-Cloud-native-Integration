# 🛠️ Lab 15 — Troubleshooting Guide (API Gateway Pattern with Apache Camel)

This guide covers common and realistic issues when running a Spring Boot + Camel API Gateway with:
- backend mock services (Jetty)
- gateway mediation routes
- Basic Auth / role-based access
- throttling (rate limiting)
- dashboard aggregation
- performance testing scripts

---

## ✅ Issue 1: Maven build fails (`mvn clean compile`)

### 🔥 Symptoms
- Compilation errors
- Dependency resolution failures
- “Unsupported major.minor version” / Java mismatch

### 🧠 Causes
- Wrong Java version
- Missing Maven dependencies
- Package path mismatch (e.g., `package` not matching folder structure)

### ✅ Fix
Check Java version:
```bash
java -version
````

Check Maven dependencies:

```bash
mvn -version
mvn dependency:tree
```

Clean and rebuild:

```bash
mvn clean install -U
```

Verify `JAVA_HOME`:

```bash
echo $JAVA_HOME
```

---

## ✅ Issue 2: App starts but some routes are not running

### 🔥 Symptoms

* You can’t reach one of the mock services (8081/8082/8083)
* Gateway endpoints return errors
* Camel route count is less than expected

### 🧠 Causes

* A route failed to start due to a syntax error
* Port conflict prevented Jetty consumer binding
* Spring Boot component scan didn’t load a `@Component`

### ✅ Fix

Watch startup logs carefully:

* Camel prints route startup summary:

  * “Routes startup (total: X started: X)”

Confirm ports are listening:

```bash
sudo netstat -tulpn | egrep ':8080|:8081|:8082|:8083'
```

Check for exceptions in logs:

```bash
# If running in terminal, scroll up.
# Or redirect output to a file:
mvn spring-boot:run | tee gateway.log
grep -i error gateway.log
```

---

## ✅ Issue 3: Port already in use (8080 / 8081 / 8082 / 8083)

### 🔥 Symptoms

* Spring Boot fails with “port already in use”
* Jetty consumer fails to bind

### 🧠 Causes

* A previous run of the app is still running
* Another service is already using the port

### ✅ Fix

Find port usage:

```bash
sudo netstat -tulpn | grep :8080
```

Kill the process:

```bash
sudo kill -9 <pid>
```

Re-run:

```bash
mvn spring-boot:run
```

---

## ✅ Issue 4: 401 Unauthorized for valid endpoint calls

### 🔥 Symptoms

* `curl http://localhost:8080/api/v1/users` returns 401
* Even with auth it still returns 401

### 🧠 Causes

* Missing Basic Auth credentials in curl
* Wrong credentials
* SecurityConfig roles mismatch

### ✅ Fix

Use correct credentials:

```bash
curl -u user:password http://localhost:8080/api/v1/users
curl -u admin:admin http://localhost:8080/api/v1/orders
```

Test wrong creds (should fail):

```bash
curl -u wrong:wrong http://localhost:8080/api/v1/users
```

---

## ✅ Issue 5: 403 Forbidden on admin endpoints

### 🔥 Symptoms

* USER credentials work for `/users` but fail for `/orders`
* Returns 403 Forbidden

### 🧠 Cause

This is expected role-based access control:

* `/orders` and `/dashboard` require ADMIN

### ✅ Fix

Use admin credentials:

```bash
curl -u admin:admin http://localhost:8080/api/v1/orders
curl -u admin:admin http://localhost:8080/api/v1/dashboard
```

---

## ✅ Issue 6: Rate limiting “not working” or behaves unexpectedly

### 🔥 Symptoms

* You don’t see 503 after many requests
* Or 503 happens earlier than expected

### 🧠 Causes

* Requests not hitting the throttled route
* Script delay changes actual req/min rate
* Throttle window overlaps between tests
* The throttle returns delayed processing (depends on async vs sync behavior)

### ✅ Fix

Confirm you’re testing the correct endpoint:

```bash
curl -u user:password http://localhost:8080/api/v1/users
```

Run the provided script exactly:

```bash
chmod +x scripts/test-rate-limiting.sh
./scripts/test-rate-limiting.sh
```

Check gateway logs for throttling behavior:

```bash
# Follow logs while running tests
# (If app is in terminal, you’ll see it live)
```

---

## ✅ Issue 7: Dashboard aggregation returns malformed JSON

### 🔥 Symptoms

* `/api/v1/dashboard` returns broken JSON
* Missing fields or incorrect braces

### 🧠 Causes

* Simple string-based merge is fragile
* One backend returns unexpected formatting
* Aggregation order or substring logic fails

### ✅ Fix

Confirm each backend response is valid:

```bash
curl -u user:password http://localhost:8080/api/v1/users
curl -u user:password http://localhost:8080/api/v1/products
curl -u admin:admin http://localhost:8080/api/v1/orders
```

Re-test dashboard:

```bash
curl -u admin:admin http://localhost:8080/api/v1/dashboard
```

Production best practice:

* use Jackson to parse responses into objects and merge safely (instead of substring merging)

---

## ✅ Issue 8: Backend “failure testing” is confusing (all services in one JVM)

### 🔥 Symptoms

* Trying to stop “one service” stops everything
* Killing Java process causes gateway down too

### 🧠 Cause

In this lab design, mock services and gateway routes run in the **same Spring Boot JVM**.

### ✅ Fix

For this lab:

* stopping JVM simulates backend down because gateway becomes unreachable too

For a more realistic design:

* run mock services in separate processes/containers (different JVMs), so gateway can remain running and return fallback responses.

---

## ✅ Issue 9: Actuator endpoints not available

### 🔥 Symptoms

* `/actuator/health` works but `/actuator/metrics` fails
* 404 on actuator endpoints

### 🧠 Causes

* Management endpoints not exposed
* Missing actuator dependency (in other projects)
* Security rules blocking actuator

### ✅ Fix

Confirm configuration:

```properties
management.endpoints.web.exposure.include=health,info,metrics
```

Test:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

---

## ✅ Issue 10: Performance test results show high latency

### 🔥 Symptoms

* Many requests take longer than expected
* Some concurrent calls slow down

### 🧠 Causes

* Throttle limits delaying processing
* Local machine CPU pressure
* JVM warm-up (first calls slower)
* Logging overhead

### ✅ Fix

Re-run after warmup:

```bash
./scripts/performance-test.sh
```

Reduce logging noise (optional in real ops):

* set log levels to WARN for noisy packages

Monitor system:

```bash
top
ps aux | grep java
```

---

## ✅ Operational Checklist (Quick Debug)

Check if app is running:

```bash
sudo netstat -tulpn | egrep ':8080|:8081|:8082|:8083'
```

Confirm gateway endpoints:

```bash
curl -u user:password http://localhost:8080/api/v1/users
curl -u admin:admin http://localhost:8080/api/v1/dashboard
```

Watch logs:

```bash
# run in same terminal or pipe:
mvn spring-boot:run | tee gateway.log
```

---

## 🔐 Security Notes

* No real secrets stored
* Credentials are demo-only (`user:password`, `admin:admin`)
* In production, replace with:

  * JWT/OAuth2
  * external identity provider
  * secrets vault / environment variables

---
