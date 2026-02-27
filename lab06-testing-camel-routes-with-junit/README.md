# 🧪 Lab 06: Testing Camel Routes with JUnit

> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** `toor`  
> **Focus:** Unit + integration testing for Apache Camel routes using **JUnit 5** and **camel-test-junit5**

---

## 🎯 Objectives

By the end of this lab, I was able to:

- Set up unit tests for Apache Camel routes using the **JUnit** framework
- Create and configure **mock endpoints** to simulate external systems
- Write comprehensive test cases validating route behavior with multiple inputs
- Use Camel test assertions to verify routing logic, headers, and transformations
- Apply best practices for testing enterprise integration routes (error handling, concurrency, reporting)

---

## 📌 Prerequisites

Before performing this lab, the following knowledge was required:

- Basic understanding of Apache Camel (routes, endpoints, processors)
- Familiarity with Java
- Maven basics (dependencies, build lifecycle)
- Unit testing concepts
- JUnit 5 familiarity
- Completed prior Camel labs (basic route creation)

---

## 🧰 Lab Environment

**Ready-to-Use Cloud Lab** (pre-configured for Camel development/testing):

- OpenJDK **17** (used in this lab)
- Apache Maven **3.6+**
- Apache Camel **3.x**
- JUnit 5
- IDE (VS Code / IntelliJ Community)

✅ **Important Fixes Applied (to run successfully)**  
- **Fix #1:** Java text blocks (`""" ... """`) require Java 15+. Compiler set to **Java 17**.  
- **Fix #2:** `xpath()` language requires **camel-xpath** dependency in Camel 3.x.

---

## 🗂️ Repository Structure

```text
lab06-testing-camel-routes-with-junit/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    ├── pom.xml
    ├── run-tests.sh
    └── src/
        ├── main/
        │   └── java/
        │       └── com/example/camel/
        │           ├── OrderProcessingRoute.java
        │           └── OrderProcessor.java
        └── test/
            └── java/
                └── com/example/camel/
                    ├── OrderProcessingRouteTest.java
                    └── OrderProcessingIntegrationTest.java
```

> ✅ In this lab, the “scripts/” folder is used to store all created project files (Maven + Java source + test files) in one place for GitHub organization.

---

## ✅ Lab Tasks Overview (What I Did)

### ✅ Task 1: Set Up Unit Tests for Camel Routes Using JUnit

* Created a Maven project structure with main/test Java directories
* Added a working `pom.xml` with Camel + JUnit 5 dependencies
* Built sample Camel routes:

  * **Order Processing** route (choice-based routing using XPath)
  * **Order Validation** route (throws exceptions on invalid input)
  * **Order Transformation** route (extracts customerId + adds headers)
* Created a processor class (`OrderProcessor`) for message processing (used as reference component)

### ✅ Task 2: Mock Endpoints and Simulate Different Inputs

* Replaced real endpoints using `isMockEndpoints()` to intercept messages
* Created multiple XML order test payloads (HIGH/MEDIUM/LOW + invalid scenarios)
* Verified correct route behavior with `MockEndpoint` expectations and message counts

### ✅ Task 3: Advanced Assertions for Route Correctness

* Verified transformations:

  * body extraction from XML (`customerId`)
  * headers like `CustomerId` and `ProcessedTimestamp`
* Implemented:

  * content-based assertions
  * custom predicates
  * timing/performance check with a timeout
* Built integration tests including:

  * centralized error handling using `onException()`
  * full flow: validate → transform → result
  * error-path routing to `mock:error`
  * concurrent message processing with async sends

### ✅ Task 4: Run, Verify, and Generate Reports

* Ran complete Maven test suite (`13` tests total)
* Ran tests per class
* Generated Surefire HTML report
* Created an executable helper script `run-tests.sh` to run everything quickly

---

## ✅ Verification & Validation

Validation was confirmed through:

* `mvn clean compile test` → **BUILD SUCCESS**
* Total tests: **13**

  * Route tests: **10**
  * Integration tests: **3**
* Surefire report generated under:

  * `target/site/surefire-report.html`
  * `target/surefire-reports/`

---

## 📌 What I Learned

* How to test Camel routes using **CamelTestSupport** (JUnit 5)
* How to mock endpoints safely using **isMockEndpoints()**
* How to validate routing, headers, transformations, and timing
* How to design integration tests with:

  * `onException()` flows
  * result vs error channels
  * concurrency validation
* How to generate test reports with Maven Surefire

---

## 🌍 Why This Matters

Camel routes typically integrate multiple systems (queues, files, HTTP services).
Testing them properly ensures:

* predictable behavior across environments
* safe refactoring
* faster incident response in production integrations
* confidence in routing + transformations without real external dependencies

---

## 🧩 Real-World Applications

These skills apply to:

* Integration testing in microservices
* Enterprise messaging pipelines (Kafka/AMQ/JMS/Camel)
* CI/CD validation of integration routes
* Regression testing of routing logic
* Error handling + recovery verification

---

## ✅ Result

* ✅ Camel routes created successfully (3 routes)
* ✅ Mock endpoints validated routing logic for HIGH/MEDIUM/LOW
* ✅ Invalid orders correctly triggered exception handling
* ✅ Transformation route extracted `customerId` and set headers
* ✅ Integration flows validated with error handling and concurrency
* ✅ All tests passed (13 total)
* ✅ Surefire HTML report generated successfully

---

## 🏁 Conclusion

This lab demonstrated how to create **JUnit 5** test cases for **Apache Camel routes** using `camel-test-junit5`.
I configured mock endpoints to validate routing logic without external dependencies, tested valid/invalid scenarios, verified transformations and headers, and validated full integration flows with error handling and concurrency testing.

✅ Lab completed successfully on a cloud lab environment
✅ All tests passing (13 total)

---
