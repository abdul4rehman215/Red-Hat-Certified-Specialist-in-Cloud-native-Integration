# 🧪 Lab 16: Dynamic Routing with Apache Camel

---

## 🧱 Repository Structure

```text
lab16-dynamic-routing-with-camel/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── pom.xml
├── test-dynamic-routing.sh
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── routing/
│       │               ├── DynamicRoutingExample.java
│       │               ├── ConfigurationService.java
│       │               ├── AdvancedDynamicRouting.java
│       │               └── PerformanceTest.java
│       └── resources/
│           └── routing-config.properties
└── test-data/
    ├── high-priority-order.json
    ├── medium-priority-order.json
    ├── low-priority-order.json
    └── high-value-order.json
````

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Understand the concept of **dynamic routing** in Apache Camel
* Implement dynamic routing using **`choice()` + predicates**
* Configure **external configuration** to drive routing decisions
* Create conditional logic for routing based on message content
* Test dynamic routes using multiple JSON datasets
* Troubleshoot common dynamic routing issues (ports, routes, JsonPath, exec plugin)

---

## 📌 Prerequisites

Before starting this lab, I already had:

* Basic understanding of Apache Camel concepts and routing
* Familiarity with Java
* Knowledge of Maven build tool
* Understanding of REST APIs and HTTP
* Basic knowledge of JSON
* Linux command line experience

---

## 🧰 Lab Environment

* **Platform:** Cloud-based lab environment (Ubuntu Linux)
* **Java:** 11+
* **Maven:** 3.6+
* **Apache Camel:** 3.x (used: 3.20.0)
* **Tools:** nano/vim, curl

---

## ✅ What I Built

This lab implements **dynamic routing patterns** in Camel using:

### 1) 🔀 Content-based routing using `choice()` + `jsonpath()`

* `/orders` routes requests to different processors based on `priority`
* `/customers` routes requests based on `customerType`

### 2) 🧩 External configuration-driven routing

* Created a Java `ConfigurationService` that loads routing rules from:

  * `routing-config.properties` (if present)
  * otherwise falls back to defaults
* Uses configuration to drive:

  * category routes (`ELECTRONICS`, `CLOTHING`, etc.)
  * priority weights (`URGENT`, `HIGH`, etc.)
  * high-value threshold
  * region queues
  * business/evening/off-hours routing

### 3) 📬 Runtime destination selection using `recipientList()`

* `/products` routes based on **category → endpoint mapping**
* `/complex-orders` routes using multiple criteria
* `/time-sensitive` routes by current hour

### 4) 🧪 Test automation + validation

* JSON datasets stored inside `test-data/`
* Bash test runner script validates every endpoint

### 5) ⚡ Load / performance validation

* A Java `PerformanceTest` sends concurrent requests using:

  * `HttpClient`
  * `ExecutorService`
  * 100 concurrent request loops

---

## 🧪 Task Overview (High-Level)

### ✅ Task 1 — Basic Dynamic Routing

* Maven project created
* Implemented `DynamicRoutingExample.java`
* Exposed REST endpoints using Jetty:

  * `POST /orders`
  * `POST /customers`
* Routed based on JSON content:

  * priority-based routing
  * customerType routing

### ✅ Task 2 — External Data Driven Routing

* Implemented `ConfigurationService.java` for routing rules
* Added `routing-config.properties` for external configuration
* Implemented `AdvancedDynamicRouting.java`:

  * category routing (`recipientList`)
  * multi-criteria routing
  * time-based routing

### ✅ Task 3 — Testing, Fixing, and Load Validation

* Created reusable JSON test datasets
* Created full test runner (`test-dynamic-routing.sh`)
* Encountered real debugging issue:

  * “No consumer available…” due to missing routes in the advanced class
* Fixed by adding `/orders` and `/customers` routes into `AdvancedDynamicRouting`
* Ran full test suite successfully
* Completed load test with concurrent calls

---

## ✅ Result

* ✅ All endpoints routed dynamically based on message content and external rules
* ✅ External configuration successfully drove category/priority/region/time routing
* ✅ Automated test script executed end-to-end without errors after patch
* ✅ Load test completed with 100 concurrent request iterations successfully
* ✅ Verified dynamic routing decisions via Camel startup logs and request results

---

## 💡 Why This Matters

Dynamic routing is essential for building:

* enterprise integration patterns (EIPs)
* event-driven workflows
* API gateways and routing layers
* adaptive systems that react to message content and metadata

This directly maps to real-world integration use cases like:

* multi-tenant routing
* SLA-based processing (priority tiers)
* geo-based queue routing
* time-based workload shifting

---

## 🌍 Real-World Applications

* API message routing based on customer tier (Premium/Standard)
* Order processing workflows with SLA priority routing
* Queue selection based on region or compliance requirements
* Shifting processing between shifts (business hours vs automated)

---

## ✅ Conclusion

In this lab, I implemented dynamic routing in Apache Camel using:

* **`choice()` + JsonPath predicates** for content-based routing
* **`recipientList()`** for runtime destination selection
* **External configuration (`routing-config.properties`)** to drive routing decisions
* Automated validation using `bash + curl` test runner
* Performance validation using concurrent requests via Java `HttpClient`

✅ Lab completed successfully on a cloud Linux environment and validated with multiple datasets + load testing.
