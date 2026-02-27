# 📌 Interview Q&A — Lab 17: Using EIPs for Scalable Integrations (Apache Camel)

## 1) What are Enterprise Integration Patterns (EIPs)?
EIPs are proven architectural patterns for integrating systems reliably and scalably. They provide standard solutions for common integration problems like routing, transformation, correlation, fan-out, and error handling.

---

## 2) What problem does the Splitter pattern solve?
Splitter breaks a large message (like an Order containing many items) into smaller messages (OrderItem messages), so each part can be processed independently and in parallel. This improves scalability and supports high-volume workflows.

---

## 3) Why did we use `streaming()` in the Splitter route?
`streaming()` allows Camel to process split parts as a stream instead of loading the full split collection into memory first. This reduces memory usage and is useful for large payloads.

---

## 4) What is `parallelProcessing()` and why is it useful?
`parallelProcessing()` tells Camel to process split parts concurrently using threads. It improves throughput and reduces total processing time, especially for CPU or I/O heavy operations.

---

## 5) What is message correlation and why is it critical in Aggregator?
Correlation is linking related messages together (like multiple OrderItem messages belonging to one Order). Without correlation, Aggregator can’t know which messages belong to the same group, so it cannot safely rebuild the original order.

---

## 6) What correlation key did we use in this lab?
We used:
- `header("correlationId")`  
which was set in the Splitter using the original order ID (originalOrderId).

---

## 7) Why did we configure both `completionSize()` and `completionTimeout()`?
- `completionSize(4)` completes aggregation when 4 items arrive (matches sample order size)
- `completionTimeout(5000)` ensures the group finishes even if some messages are delayed/missing  
Using both prevents aggregations from getting stuck indefinitely.

---

## 8) What is an AggregationStrategy in Camel?
It defines how Camel merges messages into one. In this lab, `OrderAggregationStrategy` collected items into an `AggregatedOrder`, summed totals, and increased itemCount.

---

## 9) What does the Recipient List pattern do?
Recipient List dynamically routes a single message to multiple destinations. Destinations are calculated at runtime based on message content, headers, or business rules.

---

## 10) How did we calculate recipients in this lab?
We used:
```java
recipientList(method(OrderRecipientListResolver.class, "resolveRecipients"))
````

The resolver returned a comma-separated list of endpoints like:
`direct:orderProcessing,direct:inventoryUpdate,...`

---

## 11) Why did we use `.stopOnException()` in Recipient List?

To prevent partial delivery. If one downstream endpoint fails, Camel stops routing the message to remaining recipients so we avoid inconsistent states where only some systems received the update.

---

## 12) Why did we set a `.timeout(10000)`?

To prevent routing from hanging when a downstream “system” is slow or unresponsive. This enforces a maximum wait time and improves reliability in distributed workflows.

---

## 13) What was the purpose of adding a REST API in this lab?

The REST API made the pipeline testable with real HTTP calls:

* `GET /api/eip/sample` to generate and process a sample order
* `POST /api/eip/order` to submit a full JSON order
  This mirrors real integration services where upstream apps send orders to an integration layer.

---

## 14) Why was `camel-rest` added as a dependency?

Because the REST DSL (`restConfiguration()` and `rest(...)`) requires Camel REST support. Without `camel-rest`, the REST route would not compile/run correctly.

---

## 15) What is the key scalability takeaway from this lab?

Scalability comes from:

* splitting large payloads into smaller units
* processing in parallel with controlled correlation
* aggregating with safe completion conditions
* dynamically routing to many endpoints with timeouts and failure control
  These patterns are core to building high-volume, reliable integration systems.

---
