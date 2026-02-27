# 🧪 Lab 05: Implementing Enterprise Integration Patterns (EIPs)

---

## 🎯 Objectives

By the end of this lab, I was able to:

- ✅ Understand core **Enterprise Integration Patterns (EIPs)** and why they matter in distributed systems
- ✅ Implement the **Content-Based Router** pattern to route orders based on message content
- ✅ Implement the **Splitter** pattern to break batch payloads into individual orders
- ✅ Use **Apache Camel** (Java DSL) as an integration framework within **Spring Boot**
- ✅ Configure routing rules and message transformation logic using JSON + Camel expressions
- ✅ Test and validate EIP implementations using REST endpoints (`curl`) and console logs
- ✅ Perform basic concurrent/performance-style testing with a shell script

---

## 🧠 What I Built (High-Level Summary)

This lab is a Spring Boot application that demonstrates two key EIPs:

### ✅ 1) Content-Based Router (EIP)
- Receives an **order JSON**
- Deserializes it into a `CustomerOrder`
- Uses `choice()` + `when()` to route based on:
  - `customerType`
  - `amount`
  - `priority`
  - `productCategory`
- Sends to specialized internal routes:
  - Premium high-value
  - Premium regular
  - High priority
  - Electronics
  - Standard fallback

Each destination route logs what it is doing and prints a processing summary.

---

### ✅ 2) Splitter (EIP)
- Receives a **batch JSON** (`BatchOrder`)
- Splits the list of `orders` into individual `CustomerOrder` messages
- Processes each order and forwards each one to the **Content-Based Router**
- Uses `.streaming()` to process items one-by-one (memory-friendly)

Also includes an optional route that demonstrates **Split + Aggregate** behavior after processing.

---

## 📌 Prerequisites

- Java basics
- Familiarity with JSON/XML formats
- Maven fundamentals
- Understanding of REST basics
- Basic Linux command-line skills

---

## 🧰 Technical Requirements

- Java 11+
- Maven 3.6+
- Spring Boot 2.7.x
- Apache Camel 3.20.x
- Internet access (dependencies)

---

## 🖥️ Lab Environment

This lab was executed in a **cloud-based Linux training environment**.

| Component | Details |
|----------|---------|
| OS | Ubuntu 20.04 LTS |
| Java | OpenJDK 11 |
| Maven | 3.8 |
| Spring Boot | 2.7.8 |
| Camel | 3.20.1 |
| App Port | 8080 |

> ⚠️ Note: The exact terminal hosts and runtime logs are preserved in `output.txt` for authenticity.

---

## ✅ Tasks Overview (No Commands Here)

### ✅ Task 1: Implement Content-Based Router
- Created Maven Spring Boot project and added Camel dependencies
- Built a `CustomerOrder` model
- Implemented a Camel route that routes orders based on message fields
- Added a REST endpoint to send orders for processing

✅ **Realistic Fix Applied**
The REST controller originally sent a Java object, but the route starts with:
- `.unmarshal().json(..., CustomerOrder.class)` which expects JSON.
So the controller was updated to send a JSON string via Jackson ObjectMapper.

---

### ✅ Task 2: Implement Splitter Pattern
- Built a `BatchOrder` model containing a list of orders
- Implemented a Camel Splitter route using:
  - `.split(simple("${body.orders}")).streaming()`
- Sent each split order back into the Content-Based Router
- Added REST endpoints for batch processing (with and without aggregation)

---

### ✅ Task 3: Test and Validate
- Built and ran the Spring Boot app using `mvn spring-boot:run`
- Tested routing behavior using `curl`:
  - premium high value
  - high priority
  - electronics
  - standard
- Tested batch processing:
  - used `/api/batch/sample`
  - posted batch JSON to `/api/batch/process`
  - verified split + routing logs
- Added a basic performance-style script to send multiple orders concurrently

---

## ✅ Verification Checklist

- ✅ App starts on port `8080`
- ✅ Camel context starts and routes show as started
- ✅ `/api/orders/test` responds successfully
- ✅ Orders route correctly based on conditions:
  - Premium + amount > 1000 → premium high value
  - Premium → premium regular
  - Priority HIGH → high priority route
  - Category ELECTRONICS → electronics route
  - Otherwise → standard route
- ✅ Batch splitter splits and processes individual orders
- ✅ Batch with aggregation route runs successfully
- ✅ Performance script sends multiple orders concurrently

---

## 📂 Repository Structure (Lab Folder)

```text
lab05-enterprise-integration-patterns/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    ├── pom.xml
    ├── src/
    │   ├── main/java/com/alnafi/eip/
    │   │   ├── EipPatternsApplication.java
    │   │   ├── controller/
    │   │   │   ├── OrderController.java
    │   │   │   └── BatchOrderController.java
    │   │   ├── model/
    │   │   │   ├── CustomerOrder.java
    │   │   │   └── BatchOrder.java
    │   │   └── routes/
    │   │       ├── ContentBasedRouterRoute.java
    │   │       └── SplitterRoute.java
    │   └── main/resources/
    │       └── application.yml
    ├── test-performance.sh
````

---

## 🧾 Result

✅ Implemented Content-Based Router and Splitter EIPs using Apache Camel + Spring Boot
✅ Built REST endpoints to drive integration patterns in a realistic way
✅ Verified routing decisions via logs + console output
✅ Demonstrated batch splitting with streaming processing
✅ Added a concurrency test script for practical validation

---

## 🌍 Why This Matters (Real-World Relevance)

EIPs are widely used in real systems where many services exchange messages with different formats and rules.

This lab directly matches real-world integration work such as:

* **Order routing** and fulfillment workflows
* **Priority routing** for urgent events or incidents
* **Batch processing pipelines** for bulk imports (finance, inventory, logs)
* **Middleware integration layers** between microservices
* Standard patterns used in enterprise integration tools and iPaaS platforms

---

## ✅ Conclusion

In this lab, I implemented two foundational EIPs—**Content-Based Router** and **Splitter**—using Apache Camel inside a Spring Boot application. I tested the routes through REST calls and verified that messages were routed and processed correctly under multiple scenarios, including batch splitting and concurrent request testing.

---
