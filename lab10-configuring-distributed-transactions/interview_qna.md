# 🎤 Interview Q&A — Lab 10: Configuring Distributed Transactions in Apache Camel

## 1) What is a distributed transaction?
A distributed transaction is a transaction that spans multiple resources (e.g., database + message broker). The goal is to ensure all resources either **commit together** or **rollback together**, avoiding partial success.

## 2) What does ACID mean and why is it important here?
- **Atomicity:** all steps succeed or none do  
- **Consistency:** data remains valid after the transaction  
- **Isolation:** concurrent operations don’t interfere incorrectly  
- **Durability:** committed changes persist  
In this lab, ACID matters because order processing touches DB + JMS and must stay consistent.

## 3) What is the role of JTA in Java transactions?
JTA (Java Transaction API) provides a standard way to manage transactions across multiple resources, usually via a transaction manager that coordinates commit/rollback.

## 4) Why did we use Atomikos in this lab?
Atomikos is a JTA transaction manager that can coordinate **XA resources** (XA database + XA JMS). It supports two-phase commit-like coordination for distributed transactions.

## 5) What is an XA resource?
An XA resource is a transaction-aware resource that can participate in distributed transactions (e.g., XA DataSource, XA JMS ConnectionFactory).

## 6) How does Apache Camel enable transactional routing?
Camel provides the `transacted()` method. When used inside routes, Camel ensures the route execution participates in a transaction controlled by a transaction manager.

## 7) What does `transacted("atomikosTransactionManager")` do?
It tells Camel that the route should run inside a transaction managed by Atomikos. If anything fails downstream, Camel triggers rollback behavior.

## 8) Why combine JMS + database actions in one transaction?
Because real workflows often require “consume message → update database → send confirmation.”  
If DB update fails, we should not confirm or partially save data.

## 9) What commit scenario did you test in this lab?
A valid order (`John Doe` / `Laptop`) succeeded:
- DB insert happened
- inventory decreased
- confirmation message sent
- order status updated to `CONFIRMED`

## 10) What rollback scenario did you test in this lab?
Two rollback cases:
- Non-existent product → inventory validation fails → rollback
- Insufficient quantity (Monitor request 20, available 8) → rollback  
Result: DB changes did not persist and inventory remained unchanged.

## 11) How did you verify rollback actually happened?
I checked via API endpoints:
- `/api/test/orders` → only committed orders exist
- `/api/test/inventory` → unchanged when rollback happened
- `/api/test/audit-log` → rollback events visible

## 12) What is the purpose of the audit log table?
It provides a trace of operations such as inserts/updates and rollback events, helping troubleshoot distributed transaction behavior.

## 13) What is “two-phase commit” (2PC) in simple words?
2PC is a coordination process where the transaction manager:
1) asks all resources if they are ready to commit (prepare phase)
2) commits all if everyone agrees, otherwise rolls back (commit/rollback phase)

## 14) What happens if one resource fails during processing?
The transaction manager triggers a rollback. The overall workflow should not partially commit data (e.g., order saved but inventory not updated).

## 15) Why is distributed transaction handling important in enterprise integration?
It prevents data corruption and inconsistent system state in real-world integrations like:
- order processing
- payments + inventory updates
- reliable messaging + database writes
