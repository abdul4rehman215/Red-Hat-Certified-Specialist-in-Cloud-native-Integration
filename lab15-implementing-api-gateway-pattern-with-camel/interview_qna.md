# 🧠 Lab 15 — Interview Q&A (API Gateway Pattern with Apache Camel)

> These questions cover API Gateway concepts, Camel-based mediation, security (authn/authz), throttling, aggregation, and operational troubleshooting.

---

## 1) What is the API Gateway pattern and why is it used in microservices?
An API Gateway is a **single entry point** for clients to access multiple backend microservices.  
It centralizes cross-cutting concerns like:
- authentication/authorization
- rate limiting
- logging/monitoring
- request routing/mediation
- response aggregation  
This reduces client complexity and improves consistency across services.

---

## 2) What problem does an API Gateway solve for clients?
Without a gateway, clients must:
- know multiple service URLs
- handle different auth rules per service
- call multiple endpoints to build one UI view  
A gateway simplifies this by providing **one base URL** and standardized behavior.

---

## 3) How did this lab simulate microservices behind the gateway?
It created mock services using Camel Jetty routes:
- User Service on **8081**
- Product Service on **8082**
- Order Service on **8083**  
These returned JSON responses for GET/POST requests.

---

## 4) How did the gateway route requests to backend services?
The gateway used Camel routes like:
- `/api/v1/users` → `direct:get-users` → `http://localhost:8081/users`
- `/api/v1/products` → `direct:get-products` → `http://localhost:8082/products`
- `/api/v1/orders` → `direct:get-orders` → `http://localhost:8083/orders`  
It performed mediation by setting `CamelHttpMethod`, stripping HTTP headers, and proxying to the correct backend.

---

## 5) Why did the gateway remove headers like `CamelHttp*` before forwarding?
To avoid leaking inbound Camel/HTTP headers into outbound calls and to prevent:
- conflicts with backend services
- incorrect routing behavior
- unexpected header propagation  
This is a common gateway hygiene practice.

---

## 6) What security mechanism was implemented in the lab?
Spring Security **HTTP Basic Authentication** with role-based authorization:
- `user:password` → role `USER`
- `admin:admin` → roles `USER`, `ADMIN`

---

## 7) What is the difference between authentication and authorization in this lab?
- **Authentication**: who you are (username/password) → returns 401 if missing/invalid
- **Authorization**: what you are allowed to do (roles) → returns 403 if insufficient privileges

---

## 8) Which endpoints required ADMIN access?
- `/api/v1/orders/**`
- `/api/v1/dashboard/**`  
Accessing these endpoints as `user:password` returned **403 Forbidden**.

---

## 9) How was rate limiting implemented and why is it important?
Rate limiting was implemented using Camel `throttle()` to cap requests per minute:
- Users: 10/min
- Products: 15/min
- Orders: 5/min  
This protects backend services from overload, abuse, and unexpected spikes.

---

## 10) What happened when rate limits were exceeded in testing?
Requests beyond the configured throttle resulted in a controlled fallback:
- JSON: `{"error": "Service temporarily unavailable"}`
- HTTP status: **503**  
This simulated gateway protection and a clean failure response.

---

## 11) What is response aggregation and how was it demonstrated?
Response aggregation combines outputs from multiple services into one response.  
The gateway implemented `/api/v1/dashboard` that calls:
- users service
- products service
- orders service  
Then merges into a single JSON dashboard response.

---

## 12) How was aggregation implemented in Camel?
Using:
- `multicast(...)`
- `parallelProcessing()`
- custom `AggregationStrategy` to merge JSON strings  
This is a common pattern for “Backend-for-Frontend” dashboards.

---

## 13) What are the limitations of the JSON aggregation approach used here?
The lab used **string-based JSON merging**, which is simple but fragile:
- can break on formatting differences
- hard to validate schema
- no safe parsing/escaping  
In production, you would use a JSON library (Jackson) to merge objects safely.

---

## 14) How was gateway performance tested?
Two scripts were used:
- Rate limit tester (`test-rate-limiting.sh`) for repeated sequential requests
- Performance tester (`performance-test.sh`) for **concurrent requests** and measuring duration (ms)  
This validated throughput and response time behavior at a basic level.

---

## 15) What production improvements would you add to this gateway design?
Examples:
- token-based auth (JWT/OAuth2) instead of Basic Auth
- HTTPS/TLS termination and secure headers
- proper Prometheus metrics + distributed tracing
- circuit breaker + retries + fallback per backend
- caching for common GET endpoints
- service discovery (instead of hardcoded localhost:ports)
- request/response validation (schema checks)
- structured JSON aggregation (Jackson object merge)

---
