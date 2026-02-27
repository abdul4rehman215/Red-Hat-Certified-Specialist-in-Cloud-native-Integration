# 🧪 Lab 20: End-to-End Integration with Camel Routes (Order Processing System)

---

## 🧱 Repository Structure

```text
lab20-end-to-end-camel-integration/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── alnafi/
│       │           └── integration/
│       │               ├── IntegrationApplication.java
│       │               ├── model/
│       │               │   ├── Order.java
│       │               │   └── OrderItem.java
│       │               ├── processor/
│       │               │   ├── CsvOrderProcessor.java
│       │               │   └── CustomerEnrichmentProcessor.java
│       │               ├── route/
│       │               │   ├── NotificationConsumerRoute.java
│       │               │   └── OrderProcessingRoute.java
│       │               └── service/
│       │                   └── InventoryServiceSimulator.java
│       └── resources/
│           └── database.properties
└── data/
    ├── input/
    │   ├── order_1001.csv
    │   └── order_bad.csv
    ├── output/
    │   ├── order_1001_confirmation.txt
    │   ├── notification_YYYYMMDD_HHMMSS.txt
    │   └── mq_standard_YYYYMMDD_HHMMSSmmm.txt
    ├── processed/
    │   └── order_1001.csv
    └── error/
        └── order_bad.csv
````

> ✅ Note: This lab is designed as a realistic **end-to-end integration pipeline**:
> **File ingestion → CSV parsing → DB enrichment → REST validation → routing → reporting → messaging (JMS)**
> with **retries + dead-letter/error handling**.

---

## 🎯 Objectives

By completing this lab, I was able to:

* Design and implement an end-to-end integration solution using **Apache Camel**
* Connect heterogeneous systems:

  * **File System** (CSV order intake)
  * **PostgreSQL** (customer + product enrichment, order persistence)
  * **REST API** (inventory validation)
  * **Message Queue (JMS/ActiveMQ)** (order notifications)
  * **File Output** (confirmation reports + notification artifacts)
* Apply enterprise integration patterns (EIPs):

  * **Content-Based Routing**
  * **Split**
  * **Enrich**
  * **Dead Letter Channel**
* Configure error handling, retries, and failure routing for production-style behavior
* Implement transformations and validation flow suitable for real integration projects

---

## 📌 Prerequisites

* Java (11+)
* Maven basics
* REST concepts (HTTP GET, JSON)
* PostgreSQL + SQL fundamentals
* Linux terminal workflow
* CSV / JSON familiarity

---

## 🧰 Lab Environment

* Linux-based cloud machine
* OpenJDK 11+
* Apache Maven 3.8+
* Apache Camel 3.20+
* PostgreSQL 14
* curl + common CLI tools
* nano/vim

---

## 🏗️ Integration Scenario

### Systems Connected

1. **File System (CSV Orders)**

* Incoming files dropped into `data/input/`

2. **Database (PostgreSQL)**

* Holds:

  * `customers`
  * `products`
  * `orders`
  * `order_items`

3. **REST API (Inventory Service)**

* A lightweight local simulator:

  * `GET /inventory/check?productCode=PROD001`

4. **Message Queue (JMS / ActiveMQ)**

* Embedded ActiveMQ broker started inside the app
* Sends order notifications:

  * `order.notifications.standard`
  * `order.notifications.priority`

5. **File Output**

* Confirmation reports + notification logs written to `data/output/`

---

## 🔁 End-to-End Flow 

### ✅ Step 1 — File Intake

* Camel monitors `data/input/` for CSV files
* On success: file is moved to `data/processed/`
* On failure: file is moved to `data/error/`

### ✅ Step 2 — Parse + Transform CSV → Order Object

* CSV rows are parsed and mapped into:

  * `Order`
  * `OrderItem[]`
* Total amount calculated

### ✅ Step 3 — Enrichment from PostgreSQL

* Customer details retrieved by `customer_code`
* Product details retrieved by `product_code`
* Order totals recalculated after enrichment

### ✅ Step 4 — Inventory Validation via REST

* For each `OrderItem`, the system calls:

  * `http://localhost:8080/inventory/check?productCode=...`
* If any item fails validation:

  * exception raised → retry applied → failure routed to error

### ✅ Step 5 — Content-Based Routing (Business Rules)

Orders are routed based on `totalAmount`:

* `> 1000` → High-value flow (priority MQ notification)
* `> 500` → Medium-value flow (standard MQ notification)
* else → Standard flow (report only, or minimal notifications depending on route)

### ✅ Step 6 — Persist + Generate Outputs

* Writes to DB:

  * order header (orders)
  * order line items (order_items)
* Generates confirmation report:

  * `data/output/order_<orderNumber>_confirmation.txt`

### ✅ Step 7 — Messaging (Notifications)

* Writes human-readable notification files
* Publishes a JMS message
* Consumer routes read from JMS queues and write MQ logs to `data/output/`

---

## 🧪 Testing Performed

### ✅ Success Case

* `data/input/order_1001.csv` processed successfully:

  * moved to `data/processed/`
  * confirmation report generated
  * standard notification written
  * MQ consumer wrote `mq_standard_...txt`

### ✅ Failure Case

* `data/input/order_bad.csv` contains unknown product:

  * triggers retries
  * moved to `data/error/`

---

## 🧠 What I Learned / Why This Matters

* How to design integration pipelines that are **production-shaped**, not toy examples
* How to combine Camel EIPs into one cohesive system:

  * **Enrich + Split + CBR + DLQ**
* Why resilience matters:

  * retries for transient failures
  * error folder / DLQ for investigation and reprocessing
* How to build realistic “integration glue” between legacy and modern systems:

  * files + database + REST + messaging

---

## ✅ Result

A complete working integration that demonstrates:

* File-driven ingestion with safe file movement (processed/error)
* Database-driven enrichment and persistence
* REST-driven validation
* Business routing based on rules
* Multi-output reporting + notifications
* Embedded MQ simulation + consumer routes
* Error handling with retries and dead-letter behavior

---
