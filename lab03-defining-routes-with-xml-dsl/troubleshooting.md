# 🛠️ Troubleshooting Guide — Lab 03: Defining Routes with XML DSL

> This document lists common issues and fixes when running **Apache Camel XML DSL routes** inside a **Spring Boot** application.

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
* `data/`

### 3) Confirm Camel XML routes exist in the correct folder

```bash
ls -la src/main/resources/camel/
```

### 4) Confirm input/output directories exist

```bash
find data -maxdepth 2 -type d
```

If missing, create them:

```bash
mkdir -p data/input data/output data/processed
mkdir -p data/output/{premium,standard,basic,errors,aggregated,health,success}
```

---

## 🧩 Issue 1: Application starts but routes are not loaded

### ❌ Symptoms

* Spring Boot starts but Camel logs show **0 routes started**
* Your XML routes don’t execute

### ✅ Fix Checklist

1. Verify `application.properties` has XML route discovery enabled:

```properties
camel.springboot.xml-routes=classpath:camel/*.xml
```

2. Confirm XML files are actually inside:

```text
src/main/resources/camel/
```

3. Rebuild and run again:

```bash
mvn clean compile
mvn spring-boot:run
```

---

## 🧩 Issue 2: Startup fails with XML parsing errors

### ❌ Symptoms

* Spring Boot fails with XML exceptions
* Route file fails to load

### ✅ Fix Checklist

1. Validate XML syntax with `xmllint`:

```bash
xmllint --noout src/main/resources/camel/*.xml
```

2. Check common XML mistakes:

* missing closing tags
* invalid namespace
* invalid attributes
* broken quotes
* newlines inside XML attributes (especially `throwException` message)

3. Fix and rerun:

```bash
mvn clean compile
mvn spring-boot:run
```

---

## 🧩 Issue 3: Content-based router fails with JSONPath errors

### ❌ Symptoms

Errors like:

* “No language could be found for: jsonpath”
* “Failed to create route due to unknown language: jsonpath”

### ✅ Root Cause

XML uses:

```xml
<jsonpath>$.customerType</jsonpath>
```

This requires the dependency:

* `camel-jsonpath`

### ✅ Fix

Add dependency in `pom.xml`:

```xml
<dependency>
  <groupId>org.apache.camel</groupId>
  <artifactId>camel-jsonpath</artifactId>
</dependency>
```

Then rebuild:

```bash
mvn clean compile
mvn spring-boot:run
```

---

## 🧩 Issue 4: File route doesn’t process input files

### ❌ Symptoms

* Files exist in `data/input/` but nothing appears in `data/output/`

### ✅ Fix Checklist

1. Confirm file route is started (check logs):
   Look for:

* `Started fileProcessingRoute (file://data/input)`

2. Confirm files exist:

```bash
ls -la data/input/
```

3. Confirm file permissions:

```bash
chmod -R u+rwX data/
chmod 755 data data/input data/output
```

4. Confirm output directory exists:

```bash
mkdir -p data/output
```

---

## 🧩 Issue 5: Output tier directories missing (premium/standard/basic)

### ❌ Symptoms

* Route starts, but file endpoint fails to write tiered files
* You see directory-not-found style write issues

### ✅ Fix

Create expected tier folders:

```bash
mkdir -p data/output/premium data/output/standard data/output/basic
```

---

## 🧩 Issue 6: Error route runs but error files aren’t created

### ❌ Symptoms

* The timer triggers, but you don’t see files in `data/output/errors/`

### ✅ Notes + Fix Checklist

1. The route triggers every 45 seconds, and scenario selection is random.
   So error files may take a minute to appear.

2. Ensure errors directory exists:

```bash
mkdir -p data/output/errors
```

3. Watch directory:

```bash
watch -n 2 "ls -la data/output/errors/"
```

---

## 🧩 Issue 7: Aggregation route fails due to missing strategy bean

### ❌ Symptoms

Camel startup fails with errors about:

* missing bean reference
* aggregation strategy not found
* `strategyRef` not resolved

### ✅ Root Cause

XML references:

```xml
<aggregate strategyRef="myAggregationStrategy" ...>
```

This requires a Spring bean named:

* `myAggregationStrategy`

### ✅ Fix Checklist

1. Ensure the class exists:

```bash
ls -la src/main/java/com/alnafi/camel/xmldsl/MyAggregationStrategy.java
```

2. Ensure it has the correct annotation and name:

```java
@Component("myAggregationStrategy")
```

3. Rebuild:

```bash
mvn clean compile
mvn spring-boot:run
```

---

## 🧩 Issue 8: Unit tests fail unexpectedly

### ❌ Symptoms

* `mvn test` fails
* routes aren’t found or context not started

### ✅ Fix Checklist

1. Confirm test dependencies exist in `pom.xml`:

* `spring-boot-starter-test`
* `camel-test-spring-junit5`

2. Confirm route IDs match exactly:

* `fileProcessingRoute`
* `contentBasedRouter`

3. Rerun tests:

```bash
mvn test
```

4. If tests fail due to stale context, clean and rerun:

```bash
mvn clean test
```

---

## ✅ Operational Monitoring Tips

### Watch route outputs

```bash
watch -n 2 'find data/output -type f | head -20'
```

### Tail Spring Boot logs

Just observe the running terminal, or if logs are file-based:

```bash
tail -f <logfile>
```

---

## ✅ Recovery Reset (Safe Cleanup)

If you want a clean retest run:

1. Stop app (`Ctrl+C`)
2. Clear output files:

```bash
rm -rf data/output/*
mkdir -p data/output/{premium,standard,basic,errors,aggregated,health,success}
```

3. Recreate test files:

```bash
echo "Hello World! This is a test file for XML DSL routing." > data/input/test1.txt
echo "Apache Camel makes integration easy and powerful." > data/input/test2.txt
echo "XML DSL provides declarative route configuration." > data/input/test3.txt
```

4. Restart:

```bash
mvn spring-boot:run
```

---
