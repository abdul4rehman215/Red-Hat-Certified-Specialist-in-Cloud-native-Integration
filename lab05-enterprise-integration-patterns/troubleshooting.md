# 🛠️ Troubleshooting Guide — Lab 05: Implementing Enterprise Integration Patterns (EIPs)

This guide covers common problems and fixes for Lab 05 where Apache Camel + Spring Boot implements:
- Content-Based Router (CBR)
- Splitter (Batch processing)
- Optional Split + Aggregate flow

---

## ✅ Quick Health Checks (Start Here)

### 1) Confirm Java + Maven
```bash
java -version
mvn -version
````

### 2) Confirm you’re in the project root

```bash
pwd
ls -la
```

You should see:

* `pom.xml`
* `src/`
* `target/` (after build)

---

## 🧩 Issue 1: Port 8080 already in use

### ❌ Symptoms

* App fails to start
* You see errors like:

  * “Port 8080 was already in use”

### ✅ Fix

Check what’s listening:

```bash id="wo8e2s"
sudo netstat -tulpn | grep 8080
```

Stop it (example PID 4123):

```bash id="8g1g1g"
sudo kill -9 4123
```

Then rerun:

```bash id="ylp0v9"
mvn spring-boot:run
```

---

## 🧩 Issue 2: `mvn spring-boot:run` fails because project isn’t a real Spring Boot project

### ❌ Symptoms

* Spring Boot plugin runs but app won’t start properly
* missing Boot parent / dependency issues

### ✅ Fix (Used in This Lab)

Ensure your `pom.xml` includes Spring Boot parent:

```xml id="7e6jqd"
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.7.8</version>
  <relativePath/>
</parent>
```

Then rebuild:

```bash id="knaz9f"
mvn clean compile
mvn spring-boot:run
```

---

## 🧩 Issue 3: Orders endpoint returns 400 / JSON parsing errors

### ❌ Symptoms

* API returns error: “Error processing order…”
* Camel route fails when unmarshalling JSON

### ✅ Fix Checklist

1. Ensure you send valid JSON and correct Content-Type:

```bash id="7fzz6q"
curl -X POST http://localhost:8080/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{"orderId":"X","customerType":"PREMIUM","amount":100,"priority":"NORMAL","productCategory":"BOOKS"}'
```

2. Confirm fields match `CustomerOrder` exactly:

* `orderId`
* `customerType`
* `amount`
* `priority`
* `productCategory`

3. If it still fails, review logs in the Spring Boot terminal (Camel will print why JSON failed).

---

## 🧩 Issue 4: Controller sends Java object but route expects JSON string

### ❌ Symptoms

* Route begins with:

  * `.unmarshal().json(JsonLibrary.Jackson, CustomerOrder.class)`
* But controller sends `CustomerOrder` directly (object), causing mismatch

### ✅ Fix (Applied in This Lab)

Convert object to JSON string in controller before sending into Camel:

```java id="e3shxj"
String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(order);
producerTemplate.sendBody("direct:processOrder", json);
```

Same applies to batch controller:

* convert `BatchOrder` to JSON string before sending to `direct:processBatchOrder`

---

## 🧩 Issue 5: Camel routes not starting / not discovered

### ❌ Symptoms

* app starts but routing doesn’t happen
* no “Started route…” messages

### ✅ Fix Checklist

1. Verify Spring Boot main class exists and is annotated:

```java id="bt1y0c"
@SpringBootApplication
public class EipPatternsApplication { ... }
```

2. Verify Camel routes are Spring components:

```java id="4m22en"
@Component
public class ContentBasedRouterRoute extends RouteBuilder { ... }
```

3. Rebuild and restart:

```bash id="8b89at"
mvn clean compile
mvn spring-boot:run
```

---

## 🧩 Issue 6: Content-Based Router routes “wrong” destination

### ❌ Symptoms

* order goes to a branch you didn’t expect

### ✅ Root Cause

`choice()` checks rules top-to-bottom and takes the first match.

Example: Premium + HIGH might route to premium branch before high-priority branch if premium checks happen first.

### ✅ Fix Options

* reorder conditions
* add combined conditions
* add a more specific rule earlier

---

## 🧩 Issue 7: Splitter works but some orders not routed

### ❌ Symptoms

* batch endpoint returns success
* but some individual orders don’t show expected routing logs

### ✅ Fix Checklist

1. Confirm `orders` array exists and is not empty.
2. Ensure each order contains correct fields.
3. Confirm splitter sends each split order to Content-Based Router:

```java id="3s5m8v"
.marshal().json(JsonLibrary.Jackson)
.to("direct:processOrder")
```

4. Ensure `direct:processOrder` route is started and reachable.

---

## 🧩 Issue 8: Aggregation route produces strange concatenated output

### ❌ Symptoms

* aggregated output looks like `CustomerOrder{...},CustomerOrder{...}`

### ✅ Explanation

The aggregation strategy uses:

```java
getBody(String.class)
```

If the body is still a Java object, Camel converts it using `toString()`. That’s why output looks like concatenated `CustomerOrder{...}`.

### ✅ Fix (If you want cleaner aggregation)

Marshal to JSON before aggregation, or aggregate structured results (e.g., list) instead of string concatenation.

---

## 🧩 Issue 9: Dependencies not downloaded / build fails due to missing artifacts

### ❌ Symptoms

* Maven build fails due to dependency resolution
* network hiccups / local cache problems

### ✅ Fix

Force resolve:

```bash id="t8vndg"
mvn dependency:resolve
```

Clean install:

```bash id="xbtb7r"
mvn clean install
```

---

## 🧩 Issue 10: Performance script output is “messy”

### ❌ Symptoms

* multiple `curl` outputs appear on same line

### ✅ Explanation

The script runs requests in background (`&`). Output interleaves — this is expected and realistic.

### ✅ Optional Improvement

Print a newline after each response, or redirect curl output:

```bash id="twhb7p"
curl ... -s && echo
```

---

## ✅ Recovery Reset (Safe Restart)

1. Stop the app (`Ctrl+C`)

2. Clean and rebuild:

```bash id="gjrx4u"
mvn clean compile
```

3. Restart:

```bash id="mi4pw9"
mvn spring-boot:run
```

4. Retest quickly:

```bash id="5m819m"
curl -X GET http://localhost:8080/api/orders/test
curl -X GET http://localhost:8080/api/batch/sample
```

---
