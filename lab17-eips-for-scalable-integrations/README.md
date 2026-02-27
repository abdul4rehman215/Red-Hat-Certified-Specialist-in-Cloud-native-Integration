# 🧪 Lab 17: Using EIPs for Scalable Integrations (Apache Camel)

---

## 🧱 Repository Structure

```text
lab17-eips-for-scalable-integrations/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── pom.xml
├── rest-order.json
└── src/
    └── main/
        └── java/
            └── com/
                └── alnafi/
                    └── camel/
                        └── eip/
                            ├── SplitterApplication.java
                            ├── SplitterAggregatorApplication.java
                            ├── CompleteEIPApplication.java
                            ├── CompleteEIPRestApplication.java
                            ├── splitter/
                            │   ├── Order.java
                            │   ├── OrderItem.java
                            │   └── SplitterRoute.java
                            ├── aggregator/
                            │   ├── AggregatedOrder.java
                            │   ├── OrderAggregationStrategy.java
                            │   └── AggregatorRoute.java
                            ├── recipientlist/
                            │   ├── OrderRecipientListResolver.java
                            │   └── RecipientListRoute.java
                            └── rest/
                                └── EIPRestRoute.java
````

> ✅ Note: Package naming in code follows the lab project scaffolding. Documentation here avoids vendor/training-platform branding.

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Understand and implement **Enterprise Integration Patterns (EIPs)** using Apache Camel
* Configure and use the **Splitter** pattern to divide large messages into smaller parts
* Implement the **Aggregator** pattern to combine related messages using **correlation IDs** and **completion rules**
* Deploy the **Recipient List** pattern for dynamic message dispatch to multiple endpoints
* Build scalable integration flows that can handle high-volume processing (parallel & streaming)
* Apply best practices for correlation, completion strategies, timeouts, and error handling

---

## 📌 Prerequisites

* Java fundamentals
* Maven build tool basics
* JSON/XML familiarity
* Messaging basics (routing, queues/topics)
* CLI basics
* REST API concepts

---

## 🧰 Lab Environment

* **Platform:** Cloud-based Linux lab environment
* **Java:** OpenJDK 11
* **Maven:** 3.8+
* **Camel:** 3.20.x (used: 3.20.2)
* **Tools:** nano (used as editor), curl (for REST testing)

---

## ✅ What I Built (EIP Pipeline)

This lab builds a complete end-to-end pipeline using three key EIPs:

### 1) ✂️ Splitter (Order → OrderItems)

* Generates a sample `Order` containing multiple `OrderItem`s
* Splits the order into individual items:

  * **streaming()** for memory efficiency
  * **parallelProcessing()** for throughput
* Adds headers used later for correlation and aggregation:

  * `originalOrderId` / `correlationId`
  * `itemId`
  * `itemTotal` (computed per item)

### 2) 🧩 Aggregator (OrderItems → AggregatedOrder)

* Aggregates item messages back into a single `AggregatedOrder`
* Uses:

  * **Correlation key:** `header("correlationId")`
  * **Completion rules:**

    * `.completionSize(4)` (matches sample order items)
    * `.completionTimeout(5000)` (prevents stuck groups)
* Calculates:

  * itemCount
  * totalAmount
  * aggregation duration
* Applies simple business rules:

  * discount for high totals
  * tax calculation
* Forwards the final aggregated order to the next stage

### 3) 📬 Recipient List (Dynamic Multi-Dispatch)

* Dynamically computes which internal endpoints should receive the order
* Sends the aggregated order to multiple “systems”:

  * order processing
  * inventory update
  * shipping arrangement
  * finance approval
  * customer notification
  * analytics processing
* Uses:

  * `.parallelProcessing()` to scale dispatch
  * `.stopOnException()` to avoid partial success
  * `.timeout(10000)` to avoid hanging

---

## 🌐 REST API Extension (Testing with curl)

To make the pipeline testable via API calls, I added a REST layer:

* `GET  /api/eip/sample` → generates a sample order and returns orderId
* `POST /api/eip/order` → submits a JSON order into the Splitter → Aggregator → RecipientList pipeline

This makes the solution closer to a real integration service where upstream systems submit orders over HTTP.

---

## ✅ Task Overview (High-Level)

### ✅ Task 1 — Splitter Pattern

* Maven project created using archetype
* Implemented:

  * `Order` and `OrderItem` models
  * `SplitterRoute` for splitting and parallel processing
  * `SplitterApplication` for demo execution

### ✅ Task 2 — Aggregator Pattern

* Implemented:

  * `OrderAggregationStrategy`
  * `AggregatedOrder`
  * `AggregatorRoute`
  * `SplitterAggregatorApplication` to run Splitter + Aggregator together

### ✅ Task 3 — Recipient List Pattern

* Implemented:

  * `OrderRecipientListResolver` (dynamic recipient calculation)
  * `RecipientListRoute` (parallel dispatch to multiple endpoints)
  * `CompleteEIPApplication` to run the full pipeline

### ✅ (Extension) REST Testing

* Added REST route + REST-enabled main application:

  * `EIPRestRoute`
  * `CompleteEIPRestApplication`
* Validated with curl:

  * sample generation endpoint
  * order submission endpoint using `rest-order.json`

---

## 🧪 Result

* ✅ Splitter successfully divided orders into items and processed them in parallel
* ✅ Aggregator recombined items correctly using correlationId + completion rules
* ✅ Recipient List dynamically routed to multiple internal endpoints in parallel
* ✅ REST API enabled realistic testing of the full pipeline with external JSON inputs
* ✅ Observed end-to-end flow in logs:

  * Order Generation → Splitting → Item Processing → Aggregation → Distribution

---

## 💡 Why This Matters

EIPs are the foundation of scalable integration systems:

* event-driven architectures
* microservices integration
* distributed workflows
* enterprise routing and transformation

Patterns like Splitter/Aggregator/RecipientList map directly to real production concerns:

* handling large payloads efficiently
* correlation in distributed systems
* preventing stuck aggregations with timeouts
* multi-system fan-out with reliability controls

---

## 🌍 Real-World Applications

* Splitting bulk invoices into line items for validation and processing
* Aggregating telemetry/events back into session-level summaries
* Routing customer orders to multiple systems: inventory, finance, shipping, notifications
* API-driven integration services that accept JSON orders and orchestrate downstream actions

---

## ✅ Notes on Small Real-World Fixes Applied (to run cleanly)

These were minimal fixes required to make the lab run end-to-end:

* **`code` CLI not available** → used `nano` instead (same result)
* Added **`exec-maven-plugin`** because the lab runs `mvn ... exec:java`
* Added **SLF4J Logger** where `log.info(...)` was used (to prevent compilation errors)
* Added **Camel REST dependency** and implemented a complete REST route + REST-enabled main application so curl testing works as expected

---

## ✅ Conclusion

In this lab, I built a scalable Apache Camel integration pipeline using core Enterprise Integration Patterns:

* **Splitter** for breaking large messages into parallelizable units
* **Aggregator** for correlation + recombination using completion size/time windows
* **Recipient List** for dynamic fan-out routing to multiple internal systems
* **REST API layer** to test the whole pipeline realistically with curl + JSON orders

✅ Lab completed successfully in a cloud-based Linux environment and validated via Maven builds, runtime logs, and REST testing.

