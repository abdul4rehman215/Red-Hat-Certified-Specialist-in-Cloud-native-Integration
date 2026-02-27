# 📌 Interview Q&A — Lab 16: Dynamic Routing with Apache Camel

## 1) What is dynamic routing in Apache Camel?
Dynamic routing is the ability to decide **where a message should go at runtime** based on message content, headers, external configuration, or context (time, region, etc.), instead of using only static `.to("direct:...")` endpoints.

---

## 2) What is the difference between `choice()` and `recipientList()`?
- **`choice()`**: Used for **conditional branching** (if/else style). Camel evaluates predicates and chooses exactly one matching path (unless configured otherwise).
- **`recipientList()`**: Used for **dynamic destination routing** where the endpoint(s) are calculated at runtime (from headers or expressions). It can route to **one or multiple** endpoints.

---

## 3) Why did we use `jsonpath()` in this lab?
We used `jsonpath()` to evaluate JSON payload content directly inside Camel routes, for example:
- routing `/orders` by `priority`
- routing `/customers` by `customerType`

It allows routing decisions without manually parsing JSON in a processor for simple cases.

---

## 4) Why was `camel-jsonpath` added to the dependencies?
Because the routes use `jsonpath(...)` predicates. Without `camel-jsonpath`, Camel cannot evaluate JsonPath expressions and routing conditions will fail at runtime.

---

## 5) Why did we use external configuration (`routing-config.properties`)?
To make routing decisions **configurable without code changes**.  
It enables changing:
- category → processor endpoint mapping
- priority weights
- high-value threshold
- region queue mappings
- business/evening/off-hours logic  
This is more realistic for enterprise integration workflows.

---

## 6) What problem did the “No consumer available…” error indicate?
It indicated that the test script called endpoints (`/orders`, `/customers`) that were **not started** by the running `RouteBuilder` (AdvancedDynamicRouting originally only started `/products`, `/complex-orders`, `/time-sensitive`).

---

## 7) How did we fix the “No consumer available…” issue?
We added the missing routes (`/orders` and `/customers`) into `AdvancedDynamicRouting` so a single application instance supported all endpoints required by the test script.

---

## 8) What is the role of `ConfigurationService` in this lab?
`ConfigurationService` centralizes routing rules and loads external properties.  
It provides methods like:
- `getRouteForCategory(category)`
- `getPriorityWeight(priority)`
- `isHighValueOrder(amount)`
- `getProcessingQueue(region)`
- business hours start/end values

---

## 9) What does a “high value order” mean in this lab?
An order whose `amount` exceeds the configured threshold:
- `order.highvalue.threshold=1000.0`

If true, it routes to **premium processing**.

---

## 10) How does multi-criteria routing work in `/complex-orders`?
We extract values from JSON:
- priority, amount, region, category  
Then compute routing headers:
- priorityWeight, isHighValue, regionQueue, categoryRoute  
Finally route:
- high value → premium processing
- high priority → express processing
- otherwise → regionQueue + categoryRoute (multi-destination routing)

---

## 11) What is time-based routing and how did we implement it?
Time-based routing sends messages to different processors based on current hour:
- business hours → day shift processor
- evening hours → evening shift processor
- off hours → automated processor  
We used Camel’s `${date:now:HH}` expression and compared it with config values.

---

## 12) Why is using `recipientList(simple("${header.regionQueue},${header.categoryRoute}"))` powerful?
It enables routing to **multiple destinations dynamically** using runtime headers.  
This is useful for:
- parallel processing
- region + category workflows
- multi-system integration

---

## 13) Why did we use `exec-maven-plugin`?
Because the lab starts Java applications using:
- `mvn exec:java -Dexec.mainClass="..."`  
Without `exec-maven-plugin`, Maven cannot run the main class in that way.

---

## 14) What did the performance test validate?
The performance test validated that the routes can handle:
- concurrent requests
- multiple endpoints
- multiple routing scenarios  
It used Java `HttpClient` + thread pool to generate load.

---

## 15) What is a real-world example where this lab’s routing pattern applies?
Examples:
- routing customer requests by service tier (Premium/Standard)
- routing orders by SLA priority (High/Low)
- routing workloads based on region for compliance
- routing after-hours traffic to automated systems
