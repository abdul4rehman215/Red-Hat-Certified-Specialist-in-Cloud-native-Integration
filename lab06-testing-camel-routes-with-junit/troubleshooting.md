# 🛠️ Troubleshooting Guide — Lab 06: Testing Camel Routes with JUnit

> This file captures common issues faced while testing Apache Camel routes with JUnit 5 and how to resolve them.

---

## ✅ Issue 1: Mock endpoints not receiving expected messages

### **Problem**
`MockEndpoint` expectations fail, or `expectedMessageCount()` does not match.

### **Likely Cause**
- `isMockEndpoints()` pattern does not match the actual endpoint URIs
- Mock endpoint names differ from route endpoint names
- Tests send messages to the wrong entry endpoint (`direct:`)

### **Fix**
- Confirm your mock replacement patterns match exactly:

```java
@Override
protected String isMockEndpoints() {
    return "direct:highPriorityQueue|direct:mediumPriorityQueue|direct:lowPriorityQueue|direct:orderTransformed";
}
````

* Ensure your test sends to the correct endpoint:

```java
template.sendBody("direct:processOrder", highPriorityOrder);
```

* Confirm the route uses those same endpoint URIs.

---

## ✅ Issue 2: XPath expressions failing during tests

### **Problem**

Routes do not match the correct `when(xpath(...))` conditions, or throw XPath-related errors.

### **Likely Cause**

* Missing Camel XPath dependency in `pom.xml`
* XML structure does not match XPath expression
* The XML contains namespaces not accounted for in XPath

### **Fix**

* Ensure `camel-xpath` dependency exists:

```xml
<dependency>
  <groupId>org.apache.camel</groupId>
  <artifactId>camel-xpath</artifactId>
  <version>${camel.version}</version>
</dependency>
```

* Confirm XML structure matches exactly:

```xml
<order>
  <priority>HIGH</priority>
</order>
```

* Use simple XPath during testing unless namespace support is configured:
  `/order/priority[text()='HIGH']`

---

## ✅ Issue 3: Tests fail to compile due to Java text blocks

### **Problem**

Compilation fails when using:

```java
String xml = """
<order>...</order>
""";
```

### **Likely Cause**

Java version too low (text blocks require Java 15+).

### **Fix**

* Use Java 17 in Maven compiler settings:

```xml
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

* Verify Java version:

```bash
java -version
```

---

## ✅ Issue 4: `assertMockEndpointsSatisfied()` timing out

### **Problem**

Tests hang or fail due to timeout when validating expectations.

### **Likely Cause**

* Expected messages were never produced
* Wrong route entry point used
* Infinite routing loop or missing `.end()`

### **Fix**

* Add an explicit timeout:

```java
assertMockEndpointsSatisfied(5000);
```

* Re-check routing logic and route end blocks:

```java
.choice()
  .when(...)
  .to(...)
.otherwise()
  .to(...)
.end();
```

* Ensure the test sends to the correct direct endpoint.

---

## ✅ Issue 5: Unexpected exception type in validation tests

### **Problem**

`assertThrows(Exception.class, ...)` passes, but you want more accuracy, or test fails with unexpected exception types.

### **Likely Cause**

Camel wraps exceptions depending on how routes are executed.

### **Fix**

Use broader exception for reliability, or assert root cause:

```java
Exception ex = assertThrows(Exception.class, () -> {
    template.sendBody("direct:validateOrder", invalidOrder);
});
```

If needed, inspect root cause:

```java
assertNotNull(ex.getCause());
```

---

## ✅ Issue 6: Surefire report not generated / missing HTML

### **Problem**

`target/site/surefire-report.html` is missing.

### **Likely Cause**

* Surefire report plugin not run
* Maven build incomplete
* Output differs by plugin version (file vs directory behavior)

### **Fix**

Run:

```bash
mvn surefire-report:report
```

Then verify:

```bash
ls -la target/site
```

Note: Some environments show `surefire-report.html` as a file, not a directory — both can be normal.

---

## ✅ Issue 7: Concurrency test intermittently fails

### **Problem**

Concurrent test passes sometimes and fails other times.

### **Likely Cause**

* Insufficient timeout window
* System load in cloud environment
* async sends not completed before assertion window

### **Fix**

* Increase timeout:

```java
assertMockEndpointsSatisfied(15, TimeUnit.SECONDS);
```

* Reduce concurrent orders if environment is slow
* Ensure expectations match exactly the number of async messages sent

---

## ✅ Quick Health Checks (Before Running Tests)

Run these to confirm environment is correct:

```bash
java -version
mvn -version
mvn clean test
```

---
