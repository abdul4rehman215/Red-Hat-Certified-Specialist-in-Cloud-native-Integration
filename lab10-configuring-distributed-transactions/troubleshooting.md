# 🛠️ Troubleshooting Guide — Lab 10: Configuring Distributed Transactions in Apache Camel

> This guide covers common issues when running distributed transactions in Camel using Spring Boot + Atomikos + XA (H2) + ActiveMQ (JMS).

---

## ✅ Issue 1: Workspace path mismatch (`/home/student` vs `/home/toor`)

### **Problem**
```bash
cd /home/student/workspace
# No such file or directory
````

### **Fix**

```bash
mkdir -p /home/toor/workspace
cd /home/toor/workspace
pwd
```

---

## ✅ Issue 2: Spring Boot app starts but DB tables are missing

### **Symptoms**

* `/api/test/orders` fails
* SQL errors like "Table ORDERS not found"

### **Likely Cause**

`schema.sql` was not executed at startup or initializer not wired.

### **Fix**

Make sure:

* `schema.sql` exists in `src/main/resources/`
* `DatabaseInitializer` runs (implements `CommandLineRunner`)
* App logs show:

```text
Database initialized successfully
```

If missing, rebuild and run again:

```bash
mvn clean package -DskipTests
java -jar target/camel-distributed-transactions-1.0.0.jar
```

---

## ✅ Issue 3: REST endpoints return 404

### **Likely Causes**

* Missing `spring-boot-starter-web`
* Controller package not scanned
* Server not started (port mismatch)

### **Fix**

1. Ensure dependency exists in `pom.xml`:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

2. Ensure controller is under the same base package as `@SpringBootApplication`:

* `com.alnafi.camel.transactions.*`

3. Confirm server is running:

```bash
curl -s http://localhost:8080/api/test/orders
```

---

## ✅ Issue 4: Transaction manager errors / Atomikos not starting

### **Symptoms**

* Startup failure around JTA / Atomikos
* Bean creation exceptions for transaction manager

### **Likely Causes**

* Wrong Atomikos dependency/version
* Misconfigured JTA beans

### **Fix**

* Ensure Atomikos starter dependency exists:

```xml
<dependency>
  <groupId>com.atomikos</groupId>
  <artifactId>transactions-spring-boot-starter</artifactId>
  <version>5.0.9</version>
</dependency>
```

* Check `TransactionConfig` includes:

  * `UserTransactionManager`
  * `UserTransactionImp`
  * `JtaTransactionManager`

---

## ✅ Issue 5: XA DataSource pool size method errors

### **Symptoms**

Compilation errors like:

* `setPoolSize(...)` not found

### **Fix**

Use valid Atomikos setters:

```java
dataSource.setMinPoolSize(5);
dataSource.setMaxPoolSize(10);
```

---

## ✅ Issue 6: JMS connection issues (broker not available)

### **Symptoms**

* JMS consumer fails
* Connection refused / broker errors

### **Likely Cause**

Broker URL mismatch or missing embedded broker.

### **Fix**

In this lab, broker uses **in-memory** ActiveMQ:

```java
connectionFactory.setBrokerURL("vm://localhost?broker.persistent=false");
```

If you change broker style, ensure ActiveMQ is running and credentials match.

---

## ✅ Issue 7: SQL parameter binding errors in Camel SQL

### **Symptoms**

* SQL exceptions about missing parameters
* Insert/update doesn’t execute

### **Fix**

Camel SQL expects parameters as:

* `:#paramName` (from headers)
  So ensure the route sets headers first:

```java
.setHeader("customerName", simple("${body.customerName}"))
.setHeader("productName", simple("${body.productName}"))
.setHeader("quantity", simple("${body.quantity}"))
.setHeader("price", simple("${body.price}"))
```

Then SQL uses:

```text
VALUES (:#customerName, :#productName, :#quantity, :#price, 'PROCESSING')
```

---

## ✅ Issue 8: Rollback not behaving as expected

### **Symptoms**

Order appears inserted even after inventory failure.

### **Likely Causes**

* Route not actually inside `transacted(...)`
* Non-XA resources used (not participating in JTA)

### **Fix**

1. Confirm route contains:

```java
.transacted("atomikosTransactionManager")
```

2. Confirm DataSource and JMS connection are XA-aware:

* `AtomikosDataSourceBean` (H2 XA)
* `ActiveMQXAConnectionFactory`

3. Validate rollback by checking orders endpoint after invalid order:

```bash
curl -s http://localhost:8080/api/test/orders
```

---

## ✅ Issue 9: Inventory not updating on commit

### **Likely Causes**

* Product name mismatch (case / spelling)
* SQL update condition not matching
* Inventory record not present

### **Fix**

1. Check inventory records:

```bash
curl -s http://localhost:8080/api/test/inventory
```

2. Ensure product names match exactly:

* `Laptop`, `Mouse`, `Keyboard`, `Monitor`

---

## ✅ Issue 10: Ports already in use (8080 busy)

### **Symptoms**

* Tomcat fails to start
* "Address already in use"

### **Fix**

1. Stop previous process:

```bash
ps aux | grep java
kill <PID>
```

2. Or change port in `application.yml`:

```yaml
server:
  port: 8081
```

---

## ✅ Quick Health Checklist

```bash
# build
mvn clean package -DskipTests

# run
java -jar target/camel-distributed-transactions-1.0.0.jar

# verify endpoints
curl -s http://localhost:8080/api/test/inventory
curl -s -X POST http://localhost:8080/api/test/send-valid-order
curl -s http://localhost:8080/api/test/orders
curl -s http://localhost:8080/api/test/audit-log | head
```

---
