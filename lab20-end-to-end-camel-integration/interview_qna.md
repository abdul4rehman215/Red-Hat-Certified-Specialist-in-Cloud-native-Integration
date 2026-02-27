# 📌 Interview Q&A — Lab 20: End-to-End Integration with Apache Camel Routes

## 1) What was the main goal of this lab?
To design and implement an **end-to-end integration system** using Apache Camel that connects:
- File system (CSV intake)
- PostgreSQL database (customer/product enrichment + persistence)
- REST API (inventory validation)
- Message queue (notifications via JMS/ActiveMQ)
- Output files (reports + notification artifacts)

---

## 2) What Enterprise Integration Patterns (EIPs) were used in this lab?
This lab demonstrated multiple EIPs working together:
- **Content-Based Routing (CBR)**: route by `totalAmount`
- **Split**: validate each `OrderItem` separately against inventory
- **Enrich**: lookup customer/product details and attach to the order
- **Dead Letter Channel**: route failures into an error folder
- **Message Channel / Publish-Subscribe style behavior** (via JMS queues)

---

## 3) How did Camel detect new orders?
Camel used the **File component**:
```java id="o2e6s9"
from("file:data/input?move=../processed&moveFailed=../error")
````

* Successful files move to `data/processed/`
* Failed files move to `data/error/`

---

## 4) Why was CSV unmarshalling needed?

Incoming orders arrived as **CSV rows**, not objects.
Camel’s **camel-csv** converted CSV into structured data (maps), then a processor built:

* `Order`
* `OrderItem[]`

---

## 5) How was order total calculated?

Each `OrderItem` computed:

* `lineTotal = unitPrice * quantity`
  Then the order `totalAmount` was:
* Sum of all line totals across items.

---

## 6) How did enrichment work in this pipeline?

Enrichment pulled data from PostgreSQL:

* Customer info by `customer_code`
* Product info by `product_code`
  Then it updated the Order object with:
* customerName, customerEmail
* productName and verified unitPrice (if missing/zero)

---

## 7) Why use a connection pool like HikariCP?

For production-style integration, opening/closing DB connections per exchange is expensive.
HikariCP provides:

* faster DB access
* stable connection reuse
* configurable timeouts and limits

---

## 8) How was inventory validation implemented?

For each OrderItem, Camel called the REST inventory service:

* `GET /inventory/check?productCode=...`
  and compared:
* `availableQuantity >= requiredQty`

If insufficient or missing product → exception raised.

---

## 9) Why was `.split(...).shareUnitOfWork()` used?

Because validation runs per item, but we want the overall exchange to behave as a single logical unit:

* if one item fails → the whole order fails
* keeps error handling consistent for the original order file

---

## 10) What did the error handler do?

A Dead Letter Channel strategy was applied:

* retries: **3**
* delay: **5000ms**
* failures routed to `data/error`

This simulates production retry behavior for transient issues.

---

## 11) How did Content-Based Routing decide between “standard vs priority” paths?

Routing rules were based on `totalAmount`:

* `> 1000` → high-value → priority notification
* `> 500` → medium-value → standard notification
* else → standard processing

---

## 12) Why store processed orders in the database?

So the system becomes more than file transformation:

* provides audit trail
* supports reporting
* allows downstream systems to query historical data
* demonstrates real-world integration persistence

---

## 13) Why did we include a Message Queue in this lab?

Because enterprise integrations often require:

* async notifications
* decoupled downstream handling
* reliable processing even if consumers are slow

We simulated MQ using **embedded ActiveMQ (JMS)**.

---

## 14) How did the MQ consumer help verify the pipeline?

Orders generated notifications → pushed to JMS → consumer route wrote confirmation:

* `mq_standard_*.txt`
* `mq_priority_*.txt`

This proves end-to-end delivery beyond file + DB.

---

## 15) What is the most common real-world failure scenario this lab demonstrates?

Data mismatch scenarios:

* unknown customer codes
* unknown products
* inventory not available
  And how to handle them safely with:
* retries
* dead-letter/error routing
* traceable outputs and logs

---
