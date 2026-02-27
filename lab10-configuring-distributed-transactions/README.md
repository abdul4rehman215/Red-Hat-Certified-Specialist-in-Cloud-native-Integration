# 🧪 Lab 10: Configuring Distributed Transactions in Apache Camel

> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** `toor`  
> **Stack:** Apache Camel 3.20.x + Spring Boot 2.7.x + H2 (XA via Atomikos) + ActiveMQ (XA)  
> **Focus:** Distributed transaction coordination using Camel `transacted()` with JTA (Atomikos)

---

## 🎯 Objectives

By the end of this lab, I was able to:

- Understand distributed transaction fundamentals in Apache Camel
- Implement transactional message + database processing using Camel `transacted()`
- Configure a **JTA Transaction Manager** (Atomikos) to coordinate resources
- Use **XA-capable** data source + **XA-capable** JMS connection factory
- Test both **commit** and **rollback** scenarios
- Observe transaction failures and validate that partial changes do not persist
- Add auditing visibility for troubleshooting

---

## ✅ What I Built (High-Level)

This lab builds a **transactional order processing pipeline**:

### 🔁 Transaction Flow (JMS + DB in one unit of work)
1. A test API endpoint sends an order JSON into **JMS queue `orders`**
2. Camel consumes the message and enters a transaction using:
   - `transacted("atomikosTransactionManager")`
3. The route:
   - Validates the order
   - Inserts into `orders` table
   - Checks inventory and updates it
   - Sends confirmation to `confirmations` queue
   - Updates order status to `CONFIRMED`
4. If any stage fails, the transaction is rolled back and the order is **not committed** to DB.

---

## ✅ Tasks Performed (Overview Only)

### ✅ Task 1: Setup Distributed Transaction Environment
- Created Maven project and folder layout
- Added Spring Boot + Camel + Atomikos + H2 + ActiveMQ dependencies
- Configured `application.yml` with:
  - server port
  - datasource settings
  - camel + atomikos logs
- Created database schema (`schema.sql`) and seeded inventory

### ✅ Task 2: Implement Distributed Transactions (`transacted()`)
- Built Spring Boot main application
- Configured Atomikos JTA transaction manager
- Configured XA DataSource (H2) and XA JMS connection factory (ActiveMQ)
- Implemented Camel routes:
  - JMS intake → validation → DB insert → inventory update → confirmation → status update

### ✅ Task 3: Test Commit & Rollback Scenarios
- Added REST endpoints to send test orders:
  - valid order → commit
  - invalid product → rollback
  - insufficient inventory → rollback
  - invalid data → rollback
- Verified results using REST queries:
  - `/orders`
  - `/inventory`
  - `/audit-log`
- Confirmed recovery after errors and reset test state via `/reset-data`

---

## ✅ Results Verified

### ✅ Commit Case
- Order was inserted and status updated to `CONFIRMED`
- Inventory decreased accordingly and reserved count increased
- Confirmation sent to JMS queue

### ✅ Rollback Cases
- Non-existent product OR insufficient inventory:
  - DB changes rolled back
  - No partial order persisted
  - Inventory unchanged
- Validation failure:
  - Order rejected early
  - Transaction rolled back

---

## 📌 Why This Matters (Real-World Relevance)

Distributed transactions are critical when systems must stay consistent across multiple resources, e.g.:

- **Order processing** that must update DB + send message reliably
- Preventing partial state: “order saved but inventory not updated”
- Ensuring ACID-like behavior across integration steps

This lab models a real integration scenario found in enterprise integration and cloud-native middleware systems.

---

## 🧾 Repository Structure (This Lab Folder)

```text
lab10-configuring-distributed-transactions/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    └── camel-distributed-transactions/
        ├── pom.xml
        ├── src/
        │   ├── main/
        │   │   ├── java/
        │   │   │   └── com/alnafi/camel/transactions/
        │   │   │       ├── CamelTransactionApplication.java
        │   │   │       ├── config/
        │   │   │       │   ├── TransactionConfig.java
        │   │   │       │   └── DatabaseInitializer.java
        │   │   │       ├── controller/
        │   │   │       │   └── TestController.java
        │   │   │       ├── model/
        │   │   │       │   └── Order.java
        │   │   │       ├── processor/
        │   │   │       │   ├── OrderProcessor.java
        │   │   │       │   └── InventoryProcessor.java
        │   │   │       ├── routes/
        │   │   │       │   └── OrderProcessingRoute.java
        │   │   │       └── service/
        │   │   │           └── TestDataService.java
        │   │   └── resources/
        │   │       ├── application.yml
        │   │       └── schema.sql
        │   └── test/
        │       └── java/
        └── target/   # build output (local only)
````

---

## 🧠 What I Learned

* How `transacted()` works in Camel and why it’s used for coordinated operations
* How to configure and use **JTA** with **Atomikos**
* How XA resources (DB + JMS) are managed under a single transactional boundary
* How to test rollback scenarios and validate true ACID behavior
* How audit logging supports troubleshooting distributed transaction flows

---

✅ **Lab Status:** Completed successfully (commit + rollback verified)

---
