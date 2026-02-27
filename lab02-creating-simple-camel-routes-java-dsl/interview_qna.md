# 🎤 Interview Q&A — Lab 02: Creating Simple Camel Routes Using Java DSL

This file contains interview-style questions and answers based on the work performed in **Lab 02**, where I created and tested Apache Camel routes using **Java DSL** in a standalone Maven project.

---

## 1) What is Camel Java DSL?
Camel Java DSL is a **Java-based Domain Specific Language** used to define Camel routes in code. It allows developers to build integration flows using fluent method chaining like:
- `from(...)`
- `process(...)`
- `to(...)`

Because it’s Java, it is **type-safe**, IDE-friendly, and supports refactoring and testing.

---

## 2) What is the purpose of `from()` in Camel?
`from()` defines the **source endpoint** where messages/events originate. Examples used in this lab:
- `from("timer:hello?period=5000")` → scheduled triggers
- `from("file:input?noop=true")` → files dropped into a directory

---

## 3) What does `to()` do in Camel routes?
`to()` defines the **destination endpoint** where Camel sends the message after processing. In this lab:
- `to("file:output?...")` writes content to a file
- `to("file:processed")` writes transformed files to another directory

---

## 4) What is the purpose of `process()`?
`process()` applies custom logic to a message using a **Processor** or lambda. It’s used for:
- transformations
- enrichment
- validation
- adding headers
- content changes

This lab used:
- custom `Processor` classes
- inline lambda processing

---

## 5) What is a Camel `Exchange`?
An Exchange is the message container Camel uses while routing. It includes:
- message body (`exchange.getIn().getBody()`)
- headers (`exchange.getIn().getHeader(...)`)
- metadata such as Exchange ID (useful for tracing)

In this lab, Exchange was used to:
- read file content
- read file name headers
- set a new message body
- set updated output file names

---

## 6) Why did you use `camel-main`?
`camel-main` allows running Camel as a **standalone Java application**, without needing a container like Spring Boot or Karaf. It’s simple for learning and quick route validation.

---

## 7) What were the three routes built in this lab?
### Route 1 — Timer → Processor → File
- Generates a message every 5 seconds
- Adds headers and a custom formatted body
- Writes to `output/processed-message-<timestamp>.txt`

### Route 2 — File → Processor → File
- Reads files from `input/`
- Creates a “processing report” wrapper around file content
- Writes output files to `processed/` as `processed-<originalFileName>`

### Route 3 — Timer → Inline Transform → File
- Creates a simple message every 10 seconds
- Converts it to uppercase and appends timestamp
- Writes to `simple-output/simple-<time>.txt`

---

## 8) What does `noop=true` do for file endpoints?
`noop=true` tells Camel to **not move or delete** the original file after reading it. This is useful for lab testing because input files remain available for repeated tests.

---

## 9) How did you verify the routes were working?
I verified by checking:
- new files being generated in:
  - `output/`
  - `processed/`
  - `simple-output/`
- content correctness with:
  - `cat output/processed-message-*.txt`
  - `cat processed/processed-test-message.txt`
  - `cat simple-output/simple-*.txt`
- application console logs like:
  - `Message processed: ...`
  - `File processed: ...`

---

## 10) Why is Maven useful in Camel development?
Maven is useful because it:
- manages dependencies (`camel-core`, `camel-file`, etc.)
- compiles and packages the application reliably
- integrates with CI/CD pipelines
- supports plugins like `exec-maven-plugin` to run applications easily

---

## 11) What does `mvn clean compile` confirm?
It confirms:
- dependencies resolve successfully
- Java code compiles
- there are no syntax/type errors
- build output can be produced in `target/`

In this lab, it ended with:
- `BUILD SUCCESS`
- `Build Status: 0`

---

## 12) How would you debug issues if files aren’t being processed?
Steps:
1. Confirm files exist and are readable:
   - `ls -la input/`
   - `chmod 644 input/*`
2. Confirm routes are running (application still running)
3. Confirm output directories exist:
   - `mkdir -p output processed simple-output`
4. Increase logs using SLF4J simplelogger config
5. Use Maven dependency checks:
   - `mvn dependency:tree`

---

## 13) Why did you add `simplelogger.properties`?
To enable more detailed logging, especially for Camel internals:
- Set default log level
- Enable debug logging for `org.apache.camel`
- Add timestamps to logs  
This helps troubleshoot route startup and endpoint behavior.

---

## 14) What is a Dead Letter Channel and why is it useful?
A Dead Letter Channel is an error-handling pattern where failed messages are redirected to a safe location after retries. It helps in production to avoid losing messages and to support investigation.

In this lab, the enhanced builder configured:
- retries (`maximumRedeliveries(3)`)
- delay between retries (`redeliveryDelay(1000)`)
- failed outputs written into an `error/` directory

---

## 15) What best practices does this lab introduce for enterprise integration?
- Build routes using clean separation (routes vs app startup)
- Use processors for reusable transformation logic
- Use structured output and filenames for traceability
- Validate with multiple message formats (TXT/JSON/CSV)
- Prepare logging and error handling patterns early
