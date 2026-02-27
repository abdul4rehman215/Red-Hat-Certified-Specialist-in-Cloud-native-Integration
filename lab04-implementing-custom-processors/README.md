# 🧪 Lab 04: Implementing Custom Processors in Camel Routes

---

## 🎯 Objectives

By the end of this lab, I was able to:

- ✅ Understand the role of **custom processors** in Apache Camel integration patterns
- ✅ Implement processors by creating Java classes that implement the `Processor` interface
- ✅ Insert custom processors into Camel routes to modify **message body + headers**
- ✅ Test routes with different message types and formats (user messages, system messages, null cases)
- ✅ Debug and troubleshoot processor logic, route flow, and build/runtime issues
- ✅ Apply best practices such as structured logging, modular route design, and automated tests

---

## 🧠 What I Built (High-Level Summary)

This lab is focused on **custom processor development** and how processors plug into Camel routes.

### ✅ Custom Processors Implemented

1. **MessageTransformProcessor**
   - Reads message body as `String`
   - Converts content to uppercase
   - Appends timestamp (`PROCESSED_AT_<epoch>`)
   - Adds headers:
     - `ProcessedBy`
     - `ProcessingTimestamp`
     - `MessageLength`
   - Handles null body safely (creates a fallback message)

2. **MessageEnrichmentProcessor**
   - Enriches messages using a simulated in-memory lookup (Map)
   - Detects USER-style messages (prefix like `USER001`)
   - Adds metadata headers:
     - `EnrichedBy`, `EnrichmentTimestamp`, `Priority`, `EnrichmentApplied`
   - Applies simple priority logic using `MessageType` header:
     - `USER_REQUEST` → HIGH
     - `SYSTEM_MESSAGE` → MEDIUM
     - else → LOW

---

## 📌 Prerequisites

- Java basics (classes, interfaces, methods, exception handling)
- Apache Camel fundamentals (routes, endpoints, exchange)
- Maven basics (dependencies, build lifecycle)
- Basic Linux CLI usage

---

## 🧰 Technical Requirements

- Java 11+
- Maven 3.6+
- Apache Camel 3.x dependencies (via `pom.xml`)
- Text editor (nano/vim)

---

## 🖥️ Lab Environment

This lab was executed in a **cloud-based Linux training environment**.

| Component | Details |
|----------|---------|
| Java | OpenJDK 11+ |
| Maven | 3.6+ |
| Camel | 3.20.0 (as configured in `pom.xml`) |
| Execution Style | Standalone Camel using `camel-main` and `exec:java` |

> ⚠️ Note: Host/session identifiers shown in outputs are preserved as-is in `output.txt` for authenticity.

---

## ✅ Tasks Overview (No Commands Here)

### ✅ Task 1: Create Custom Processors
- Created a Maven project and configured Camel dependencies
- Built `MessageTransformProcessor` for transformation + header enrichment
- Built `MessageEnrichmentProcessor` for message enrichment + metadata headers + priority logic

### ✅ Task 2: Insert Processors into Routes
Created a `RouteBuilder` containing multiple routes using processors:

- `direct:transform` → uses `MessageTransformProcessor`
- `direct:enrich` → uses `MessageEnrichmentProcessor`
- `direct:combined` → runs **transform then enrichment**
- `timer:autoTest` → generates messages and routes them into `direct:transform`
- `direct:output` → logs final body + headers (acts like a sink)

### ✅ Task 3: Testing Strategy (Manual + Automated)
- Built and compiled using Maven
- Ran a standalone app (`CustomProcessorApplication`) that:
  - starts Camel context
  - sends test messages to routes using `ProducerTemplate`
  - waits to observe timer route executions
- Created unit tests for processor logic (JUnit4 + Camel Exchange)
- Created integration tests for route behavior (CamelTestSupport)
- Added a reusable manual testing script (`test-custom-processors.sh`)
- Completed an advanced test app (`AdvancedTestApplication`) to test:
  - different message sizes
  - special characters / unicode
  - null and missing header cases
  - basic performance batch (50 messages)

---

## ✅ Verification Checklist

- ✅ `mvn clean compile` completes successfully (`BUILD SUCCESS`)
- ✅ Standalone application runs and routes execute (`mvn exec:java`)
- ✅ Transform processor:
  - body becomes uppercase
  - timestamp marker is appended
  - headers are present (`ProcessedBy`, `MessageLength`, etc.)
- ✅ Enrichment processor:
  - user IDs enrich correctly (USER001/002/003)
  - unknown user handled gracefully (USER999)
  - priority changes based on `MessageType`
- ✅ Unit tests pass (`MessageTransformProcessorTest`)
- ✅ Integration tests pass (`CustomProcessorRouteTest`)
- ✅ Manual script completes successfully
- ✅ Advanced test application compiles and runs successfully

---

## 📂 Repository Structure (Lab Folder)

```text
lab04-implementing-custom-processors/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    ├── pom.xml
    ├── src/
    │   ├── main/java/com/alnafi/camel/lab4/
    │   │   ├── CustomProcessorApplication.java
    │   │   ├── AdvancedTestApplication.java
    │   │   ├── processors/
    │   │   │   ├── MessageTransformProcessor.java
    │   │   │   └── MessageEnrichmentProcessor.java
    │   │   └── routes/
    │   │       └── CustomProcessorRouteBuilder.java
    │   └── test/java/com/alnafi/camel/lab4/
    │       ├── processors/
    │       │   └── MessageTransformProcessorTest.java
    │       └── routes/
    │           └── CustomProcessorRouteTest.java
    └── test-custom-processors.sh
````

---

## 🧾 Result

✅ Implemented and integrated custom Camel processors into multiple routes
✅ Demonstrated transformation + enrichment patterns using processors
✅ Validated behavior using:

* real application execution
* unit tests (processor-level)
* integration tests (route-level)
* automation script for repeatable testing
* advanced scenario testing (formats, edge cases, performance)

---

## 🌍 Why This Matters (Real-World Relevance)

Custom processors are one of the most common building blocks in enterprise integration because they enable:

* message normalization (format changes, cleanup, standardization)
* enrichment (lookup user/product/account metadata)
* header-based routing decisions (priority, flags, tags)
* validation, filtering, and business rules enforcement
* reliable observability via structured logs + headers

This is directly applicable to real integration engineering tasks such as:

* building middleware services
* preparing payloads for downstream systems
* routing by metadata (priority tiers, customer tiers, system events)
* making integrations testable and production-ready

---

## ✅ Conclusion

In this lab, I implemented multiple custom processors and integrated them into Camel routes to transform and enrich message traffic. I validated functionality using both manual execution and automated test suites, and extended testing via a reusable script and an advanced test application.

✅ Lab 04 completed successfully. 🚀

---

