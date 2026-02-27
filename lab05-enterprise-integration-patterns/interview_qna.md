# 🎤 Interview Q&A — Lab 05: Implementing Enterprise Integration Patterns (EIPs)

? This file contains interview-style questions and answers based on **Lab 05**, where I implemented **Content-Based Router** and **Splitter** patterns using Apache Camel inside a Spring Boot application and tested them via REST endpoints.

---

## 1) What are Enterprise Integration Patterns (EIPs)?
EIPs are reusable design patterns that describe how to integrate systems using messaging and routing concepts. They solve common integration problems such as routing, transformation, splitting, aggregation, filtering, and error handling.

---

## 2) What is the Content-Based Router pattern?
Content-Based Router inspects message content (fields, headers, payload structure) and routes the message to different destinations based on rules.  
In this lab, orders were routed based on:
- customer type (PREMIUM/standard)
- order amount
- priority
- product category (ELECTRONICS, etc.)

---

## 3) How did you implement Content-Based Router in Apache Camel?
Using Camel Java DSL:
- `choice()`
- multiple `when(...)` conditions
- `otherwise()` fallback

Example behavior:
- Premium + amount > 1000 → premium-high-value route
- Priority HIGH → high-priority route
- Category ELECTRONICS → electronics route
- Otherwise → standard route

---

## 4) Why is the order of `when()` conditions important?
Camel evaluates `when()` rules **top to bottom** and executes the first match.  
So in this lab:
- Premium + high-value was checked before premium-regular
- Premium routing happens before checking `priority == HIGH`, so a premium order may never reach the high-priority branch depending on the conditions

---

## 5) What is the Splitter pattern?
Splitter takes one large message containing multiple items and breaks it into multiple smaller messages.  
In this lab, a `BatchOrder` containing multiple `CustomerOrder` entries was split into individual orders.

---

## 6) How did you implement the Splitter in Camel?
Using:
- `.split(simple("${body.orders}"))`
- `.streaming()` to process one-by-one (memory efficient)

Each split order was then marshaled back to JSON and routed into `direct:processOrder` (the Content-Based Router entry point).

---

## 7) Why did you use `.streaming()` in the Splitter route?
`.streaming()` processes split messages in a streaming fashion rather than loading everything into memory first. This is useful for large batches and is more production-friendly.

---

## 8) How did you connect REST APIs to Camel routes?
Spring Boot REST controllers used `ProducerTemplate` to send messages into Camel `direct:` endpoints:
- `/api/orders/process` → sends into `direct:processOrder`
- `/api/batch/process` → sends into `direct:processBatchOrder`
- `/api/batch/process-with-aggregation` → sends into `direct:processBatchWithAggregation`

---

## 9) What was the “controller vs unmarshal” issue and how was it fixed?
The route started with:
```java
.unmarshal().json(JsonLibrary.Jackson, CustomerOrder.class)
````

So it expected a JSON string.
But the controller originally sent a Java object. That mismatch can break route parsing.

✅ Fix applied:

* Controller converts the Java object to JSON using Jackson:

```java
String json = new ObjectMapper().writeValueAsString(order);
producerTemplate.sendBody("direct:processOrder", json);
```

Same approach was used for batch orders.

---

## 10) Why is JSON marshalling/unmarshalling important in integration systems?

Real systems often exchange messages as JSON across:

* REST APIs
* message queues
* log pipelines
* event systems
  Marshalling/unmarshalling ensures:
* consistent schema handling
* strong typing in code
* predictable routing decisions based on parsed fields

---

## 11) How did you validate routing worked correctly?

By:

* sending test orders via `curl`
* observing:

  * controller responses (HTTP 200)
  * Camel logs (Received order, Routing to X)
  * route console prints (premium/high-priority/electronics/standard outputs)

---

## 12) What is the role of `direct:` endpoints in this lab?

`direct:` endpoints are in-memory synchronous endpoints used to link routes together. They are excellent for:

* modular route design
* testing routes quickly
* composing multi-step pipelines within the same Camel context

---

## 13) What was the purpose of the “batch with aggregation” route?

It demonstrated an additional enterprise concept:

* split a batch
* process each item
* aggregate results back together after a timeout (`completionTimeout(5000)`)

Even though it’s not a full production aggregator pattern, it shows how splitting and aggregation can be chained.

---

## 14) How did you perform basic performance/concurrency testing?

Using a bash script that fired 10 `curl` POST requests concurrently (background `&`) and waited for completion. The mixed/merged output is expected because background processes print at the same time.

---

## 15) Where do these patterns appear in real-world systems?

Common use cases:

* e-commerce order routing and fulfillment
* payment/transaction routing in financial systems
* healthcare batch ingestion pipelines
* incident/event routing (priority-based)
* ETL pipelines where data arrives as bulk batches and must be split and processed individually

---
