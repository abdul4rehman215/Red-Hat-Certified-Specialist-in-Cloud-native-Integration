# 🧪 Lab 03: Defining Routes with XML DSL

---

## 🎯 Objectives

By the end of this lab, I was able to:

- ✅ Understand the fundamentals of **Apache Camel XML DSL**
- ✅ Create and configure Camel routes using **XML configuration**
- ✅ Implement message transformation and routing logic declaratively
- ✅ Deploy and run XML DSL routes inside a **Spring Boot** application
- ✅ Validate routes via file-based testing + generated outputs
- ✅ Debug and troubleshoot XML-based integration patterns (dependencies, XML parsing, route loading)
- ✅ Add advanced patterns: **content-based routing**, **dead letter handling**, **aggregation**, and **health checks**

---

## 🧠 What I Built (High-Level Summary)

In this lab, I created a **Spring Boot + Apache Camel** project that loads Camel routes from XML files automatically.

### ✅ Route Set Implemented (XML DSL)

1. **File Processing Route (Uppercase Transform)**
   - Watches `data/input/`
   - Logs filename
   - Adds timestamp header
   - Transforms content to uppercase
   - Writes to `data/output/`

2. **Content-Based Router (Customer Tier Routing)**
   - Generates sample order JSON on a timer
   - Extracts `customerType` using `jsonpath`
   - Routes to:
     - Premium processing → `data/output/premium/`
     - Standard processing → `data/output/standard/`
     - Basic processing → `data/output/basic/`

3. **Error Handling Route (Dead Letter Channel)**
   - Triggers scenarios randomly using a timer
   - Throws exceptions (validation/I/O/runtime)
   - Captures errors via **Dead Letter Channel** and writes error reports to:
     - `data/output/errors/`

4. **Aggregation Route (Batch Processing)**
   - Splits a sequence into multiple messages
   - Aggregates into batches of 5 using a custom aggregation strategy bean
   - Writes aggregated JSON batches to:
     - `data/output/aggregated/`

5. **Health Check Route**
   - Runs periodic health checks
   - Writes JSON health files to:
     - `data/output/health/`
   - Enables monitoring-friendly output and JMX settings

---

## 📌 Prerequisites

- Basic XML syntax/structure
- Apache Camel fundamentals (from previous labs)
- Java development basics
- Maven fundamentals
- CLI basics
- Completion of Lab 01 and Lab 02 (or equivalent experience)

---

## 🧰 Technical Requirements

- Java 11+
- Maven 3.6+
- Apache Camel 3.x
- Spring Boot 2.7+
- Text editor (nano/vim/VS Code)

---

## 🖥️ Lab Environment

This lab was executed in a **cloud-based Linux training environment**.

| Component | Details |
|----------|---------|
| OS | Ubuntu 20.04 LTS |
| Java | OpenJDK 11 |
| Maven | 3.8.6 |
| Spring Boot | 2.7.8 |
| Camel | 3.20.2 |
| Route Style | XML DSL loaded via Spring Boot auto-discovery |

> ⚠️ Note: Terminal host/session identifiers appear in `output.txt` exactly as observed during execution.

---

## ✅ Tasks Overview (No Commands Here)

### ✅ Task 1: Project Setup (Maven + Dependencies)
- Generated Maven project structure
- Replaced `pom.xml` with Spring Boot + Camel BOM configuration
- Verified successful compile (`BUILD SUCCESS`)
- Created directory layout for routes, tests, and data I/O folders

### ✅ Task 2: XML DSL Routes + Spring Boot Bootstrap
- Created Spring Boot main class
- Created first XML route for file processing
- Enabled Camel XML route auto-discovery via:
  - `camel.springboot.xml-routes=classpath:camel/*.xml`

### ✅ Task 3: Advanced XML DSL Patterns
- Built a content-based router using `<choice>` + `<when>` + `<otherwise>`
- Implemented message transformation and file outputs per customer tier
- Implemented error handling with Dead Letter Channel + retry policy

✅ **Dependency Fix Applied (Important):**
- The XML used `<jsonpath>...</jsonpath>` for routing decisions.
- To ensure routes load correctly, `camel-jsonpath` dependency was added to the POM and the project rebuilt.

### ✅ Task 4: Testing & Validation
- Created real input test files in `data/input/`
- Ran the Spring Boot application and confirmed routes started
- Verified output files in `data/output/` and tiered folders
- Confirmed uppercase transform results
- Confirmed error files generated in `data/output/errors/`
- Executed unit tests successfully (`Tests run: 3, Failures: 0`)

### ✅ Task 5–6: Monitoring + Operational Readiness
- Added aggregation route and created aggregation strategy bean
- Enabled JMX + management endpoints for monitoring
- Built a health-check route that writes periodic health JSON outputs
- Validated XML parsing using `xmllint`

---

## ✅ Verification Checklist

- ✅ Spring Boot app starts successfully
- ✅ Camel context starts and shows **routes started**
- ✅ XML routes are auto-discovered from `classpath:camel/*.xml`
- ✅ Files placed in `data/input/` are transformed and written to `data/output/`
- ✅ Content router generates tiered JSON files (premium/standard/basic)
- ✅ Error handler produces error reports under `data/output/errors/`
- ✅ Aggregation outputs appear under `data/output/aggregated/`
- ✅ Health check files appear under `data/output/health/`
- ✅ `mvn test` passes successfully

---

## 📂 Repository Structure (Lab Folder)

```text
lab03-defining-routes-with-xml-dsl/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    ├── pom.xml
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/alnafi/camel/xmldsl/
    │   │   │       ├── CamelXmlDslApplication.java
    │   │   │       └── MyAggregationStrategy.java
    │   │   └── resources/
    │   │       ├── application.properties
    │   │       └── camel/
    │   │           ├── file-processing-route.xml
    │   │           ├── content-based-router.xml
    │   │           ├── error-handling-route.xml
    │   │           ├── aggregation-route.xml
    │   │           └── health-check-route.xml
    │   └── test/
    │       └── java/com/alnafi/camel/xmldsl/
    │           └── XmlDslRouteTest.java
    └── sample-data/
        ├── data/input/test1.txt
        ├── data/input/test2.txt
        └── data/input/test3.txt
````

---

## 🧾 Result

✅ Successfully ran Camel routes using **XML DSL** inside Spring Boot
✅ Validated file transformation and tiered routing outputs
✅ Implemented error handling + generated error reports to filesystem
✅ Added aggregation + health monitoring patterns
✅ All unit tests executed successfully and produced reports

---

## 🌍 Why This Matters (Real-World Relevance)

XML DSL is often used where teams prefer **declarative route definitions** over code-heavy implementations. It’s useful for:

* Integration middleware environments
* Ops-friendly configuration-driven routing
* Standardization of integration patterns across teams
* Faster route changes with reduced code changes
* Building and documenting EIPs (choice, error handling, aggregation)

This lab mirrors real enterprise integration work:

* file processing pipelines
* tier-based routing decisions
* error capture and operational reliability
* monitoring/health practices for production routes

---

## ✅ Conclusion

In this lab, I built a Spring Boot application that auto-loads Apache Camel routes defined in XML DSL. I implemented both basic and advanced integration patterns, validated route outputs through filesystem testing, and confirmed stability via unit tests.

This prepares the foundation for upcoming labs involving more advanced route design, custom processors, EIPs, and production-grade integration workflows.

---
