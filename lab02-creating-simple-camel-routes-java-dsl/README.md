# 🧪 Lab 02: Creating Simple Camel Routes Using Java DSL

---

## 🎯 Objectives

By the end of this lab, I was able to:

- ✅ Understand the fundamentals of **Apache Camel Java DSL**
- ✅ Create simple routes using Java DSL with `from()`, `to()`, and `process()`
- ✅ Implement custom message processors to transform data
- ✅ Test Camel routes by sending multiple sample message types (TXT/JSON/CSV)
- ✅ Debug and troubleshoot basic routing issues
- ✅ Apply basic best practices used in enterprise integration scenarios

---

## 🧠 What I Built (High-Level Summary)

In this lab, I created a **standalone Camel application** using Maven and Camel Main. I implemented **three Camel routes**:

1. **Timer → Processor → File**
   - Generates a message on a schedule
   - Uses a custom processor to enrich the message
   - Writes output to `output/`

2. **File → Processor → File**
   - Watches `input/` for files
   - Uses a processor to generate a “processing report”
   - Writes transformed files to `processed/`

3. **Timer → Inline Processor (Lambda) → File**
   - Creates a simple message and transforms to uppercase
   - Writes to `simple-output/`

I also created optional debugging configuration (`simplelogger.properties`) and an optional enhanced route (`EnhancedRouteBuilder.java`) demonstrating basic error-handling patterns.

---

## 📌 Prerequisites

- Basic Java programming concepts
- Familiarity with Maven
- Basic messaging concepts
- Completion of Lab 01 (Camel environment knowledge) or equivalent

---

## 🧰 Technical Requirements

- Java 11+
- Maven 3.6+
- A terminal + text editor (nano/vim) or IDE

---

## 🖥️ Lab Environment

This lab was executed in a **cloud-based Linux training environment**.

| Component | Details |
|----------|---------|
| Java | OpenJDK 11 |
| Maven | 3.8+ |
| Camel | 3.20.x (as configured in Maven `pom.xml`) |
| Execution Style | Standalone app using Camel Main (`mvn exec:java`) |

> ⚠️ Note: Some package/group names and host/session values appear in outputs exactly as they occurred during execution. These are kept in `output.txt` for authenticity.

---

## ✅ Tasks Overview (No Commands Here)

### ✅ Task 1: Create a Route Using Java DSL

- Created a new Maven project using `maven-archetype-quickstart`
- Updated `pom.xml` to include Apache Camel dependencies:
  - `camel-core`, `camel-main`, `camel-file`, `camel-timer`
  - `slf4j-simple` for logging
- Implemented Camel routes using Java DSL in a RouteBuilder class:
  - Used `from()` for endpoints (timer/file)
  - Used `process()` for custom transformations
  - Used `to()` for output endpoints (file)
- Created a `main()` entry point to start the routes

### ✅ Task 2: Test the Route by Sending Sample Messages

- Built the project with Maven (`clean compile`)
- Ran the Camel app using Maven exec plugin
- Tested File-to-File route using multiple message types:
  - Text file
  - JSON file
  - CSV file
- Verified outputs were created and transformed properly in:
  - `output/`, `processed/`, `simple-output/`
- Practiced stop/restart workflow + clearing output dirs for clean re-testing

### ✅ Debugging / Enhancements (Included in this lab)

- Created a logging configuration file:
  - `src/main/resources/simplelogger.properties`
- Created an optional enhanced builder showing basic error handling:
  - Dead Letter Channel + retries
  - `onException(...)` handling
  - Writes failures to an `error/` directory
  - (Not wired into the main app yet — documented as a realistic next step)

---

## ✅ Verification Checklist

- ✅ Maven project generated successfully
- ✅ `pom.xml` updated and verified
- ✅ Java sources compiled successfully (`BUILD SUCCESS`)
- ✅ Application started with `mvn exec:java`
- ✅ Timer routes generated files into `output/` and `simple-output/`
- ✅ File route processed real files from `input/` → `processed/`
- ✅ Content transformation verified using `cat`
- ✅ Stop/restart tested (Ctrl+C, clear outputs, rerun)

---

## 📂 Repository Structure (Lab Folder)

```text
lab02-creating-simple-camel-routes-java-dsl/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    ├── pom.xml
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/
    │       │       └── alnafi/
    │       │           └── camel/
    │       │               ├── CamelApplication.java
    │       │               ├── SimpleRouteBuilder.java
    │       │               └── EnhancedRouteBuilder.java
    │       └── resources/
    │           └── simplelogger.properties
    └── sample-inputs/
        ├── test-message.txt
        ├── order-data.txt
        ├── json-message.json
        └── customer-data.csv
````

> ✅ Note: The `scripts/` directory contains the exact code/config content created for this lab for easy reuse and review.

---

## 🧾 Result

✅ Built and executed a working Camel Java DSL application
✅ Implemented processors + transformations using RouteBuilder
✅ Validated routing with real file inputs and multiple message formats
✅ Confirmed outputs with file verification + console logs
✅ Created optional debugging + error-handling scaffolding for extension

---

## 🌍 Why This Matters (Real-World Relevance)

Apache Camel Java DSL is widely used for building integration services because:

* It is **type-safe** and developer-friendly
* It integrates easily with build pipelines (Maven/CI)
* It supports enterprise patterns (EIPs) and scalable routing logic
* It’s commonly used for:

  * microservice communication and async workflows
  * data routing and transformation pipelines
  * scheduled/batch processing tasks
  * integration with message brokers, APIs, filesystems, and databases

---

## ✅ Conclusion

In this lab, I successfully created a Maven-based Apache Camel project and implemented multiple Camel routes using Java DSL. I tested the routes using timer triggers and real input files (including JSON and CSV) and verified that messages were processed and written correctly into output directories.

This lab establishes strong foundations for upcoming work involving **XML DSL**, **custom processors**, and **enterprise integration patterns (EIPs)**.

✅ Lab 02 completed successfully. 🚀

---
