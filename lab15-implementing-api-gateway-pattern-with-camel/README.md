# 🧪 Lab 15: Implementing API Gateway Pattern with Camel

> **Track:** Red Hat Certified Specialist in Cloud-Native Integration (Exam)  
> **Lab Focus:** Build an API Gateway using Apache Camel (mediation), add authentication/authorization, apply rate limiting, aggregate responses, test performance, and troubleshoot operations.  
> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** toor  
> **Status:** ✅ Completed

---

## 📌 Folder Name

```text
lab15-implementing-api-gateway-pattern-with-camel/
````

---

## 🧱 Repository Structure (Lab Format)

```text
lab15-implementing-api-gateway-pattern-with-camel/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── gateway/
│   │   │               ├── ApiGatewayApplication.java
│   │   │               ├── ApiGatewayRoutes.java
│   │   │               ├── MockBackendRoutes.java
│   │   │               ├── SecurityConfig.java
│   │   │               ├── RateLimitingRoutes.java
│   │   │               └── AggregationRoutes.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
└── scripts/
    ├── test-rate-limiting.sh
    └── performance-test.sh
```

> ✅ Notes:
>
> * `src/main/java/...` contains the full Spring Boot + Camel routes (Gateway + mock services).
> * `scripts/` contains bash scripts for rate limiting and performance testing.
> * No secrets are stored. Authentication users/passwords are the lab’s demo credentials in `SecurityConfig.java`.

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Understand the **API Gateway pattern** and why it’s used in microservices architectures
* Implement an **API Gateway using Apache Camel** for API mediation (routing requests to backend services)
* Configure security mechanisms:

  * authentication (Basic Auth)
  * authorization (role-based access control)
* Implement **rate limiting** using Camel throttling to protect backend services
* Implement **response aggregation** (“dashboard” endpoint) to combine multiple backend service responses into one JSON response
* Test and evaluate gateway behavior under load using scripts (concurrent requests + throttling validation)
* Monitor and troubleshoot gateway operations using logs and health/metrics endpoints

---

## ✅ Prerequisites

Before starting this lab, it helps to have:

* REST API + HTTP methods knowledge (GET/POST/PUT/DELETE)
* Java + Maven basics
* Understanding of microservices architecture and why gateways exist
* Basic authentication/authorization concepts
* Familiarity with logging/monitoring fundamentals
* Linux command-line usage

---

## 🌥️ Lab Environment

Ready-to-use cloud machine (no manual setup needed), includes:

* Java 11 (OpenJDK)
* Apache Maven 3.8+
* Apache Camel 3.20+
* Postman (optional testing)
* curl
* nano/vim

---

## 🧩 What I Built

### ✅ 1) Mock Backend Services (simulated microservices)

Three internal services were created using **Camel Jetty**, listening on:

* User Service → `http://0.0.0.0:8081/users`
* Product Service → `http://0.0.0.0:8082/products`
* Order Service → `http://0.0.0.0:8083/orders`

They support GET/POST and return JSON responses.

### ✅ 2) API Gateway (single entry point)

A gateway REST API exposed on:

* Gateway Base → `http://0.0.0.0:8080/api/v1/*`

Gateway endpoints:

| Endpoint            | Method   | Backend Target            | Purpose                |
| ------------------- | -------- | ------------------------- | ---------------------- |
| `/api/v1/users`     | GET/POST | User Service (8081)       | User data mediation    |
| `/api/v1/products`  | GET/POST | Product Service (8082)    | Product data mediation |
| `/api/v1/orders`    | GET/POST | Order Service (8083)      | Orders mediation       |
| `/api/v1/dashboard` | GET      | Users + Products + Orders | Aggregated response    |

---

## 🔐 Security Model (Authentication + Authorization)

Implemented using **Spring Security**:

### Users (In-Memory)

* `user:password` → Role: `USER`
* `admin:admin` → Roles: `USER`, `ADMIN`

### Access Rules

* `USER` can access:

  * `/api/v1/users/**`
  * `/api/v1/products/**`
* `ADMIN` required for:

  * `/api/v1/orders/**`
  * `/api/v1/dashboard/**`

### Expected Behavior

* No auth → `401 Unauthorized`
* Wrong credentials → `401 Unauthorized`
* USER trying admin endpoint → `403 Forbidden`
* ADMIN access → `200 OK`

---

## 🚦 Rate Limiting (Gateway Protection)

Implemented with Camel throttling:

* Users endpoint: **10 req/min**
* Products endpoint: **15 req/min**
* Orders endpoint: **5 req/min** (more restrictive)

When throttling or service issues occur, the gateway returns a controlled fallback JSON:

* `503 Service temporarily unavailable`

> ✅ This demonstrates a gateway enforcing policy and protecting services from overload.

---

## 🧾 Response Aggregation (“Dashboard”)

`GET /api/v1/dashboard` aggregates multiple service calls using:

* `multicast()`
* `parallelProcessing()`
* custom `AggregationStrategy` (simple JSON merge style)

Output returns one combined JSON response:

```json
{
  "dashboard": {
    "users": {...},
    "products": {...},
    "orders": {...}
  }
}
```

---

## 🧪 Testing Performed

### Basic Gateway Tests (with curl)

* Users:

  * `curl -u user:password http://localhost:8080/api/v1/users`
* Products:

  * `curl -u user:password http://localhost:8080/api/v1/products`
* Orders (admin):

  * `curl -u admin:admin http://localhost:8080/api/v1/orders`
* Dashboard (admin):

  * `curl -u admin:admin http://localhost:8080/api/v1/dashboard`

### Rate Limiting Tests

Script executed:

* `scripts/test-rate-limiting.sh`
  Validated:
* first N requests succeed (HTTP 200)
* requests above limit return fallback (HTTP 503)

### Performance / Concurrency Tests

Script executed:

* `scripts/performance-test.sh`
  Validated:
* concurrent requests to endpoints
* response time per request (ms) and status codes

### Monitoring / Health

Checked:

* `/actuator/health`
* `/actuator/info`
* `/actuator/metrics`

---

## 📈 Result

This lab successfully implemented a working API Gateway using Apache Camel + Spring Boot:

* ✅ Mediation routes to backend services
* ✅ Authentication and role-based authorization
* ✅ Rate limiting per route to protect services
* ✅ Aggregation endpoint for combined dashboard response
* ✅ Tested functionality, throttling behavior, concurrency performance
* ✅ Verified monitoring endpoints and basic ops tooling

---

## 💡 What I Learned

* How an API Gateway simplifies microservices client integration (single entry point)
* How Camel routes can provide gateway features like:

  * routing/mediation
  * policy enforcement (rate limiting)
  * retries/timeouts + fallback responses
  * response composition (aggregation)
* How role-based access control reduces attack surface and enforces least privilege
* How to validate throttling and concurrency behavior with repeatable scripts
* How to use Spring Boot actuator endpoints for basic service observability

---

## 🌍 Why This Matters (Real-World Relevance)

API Gateways are critical in modern architectures because they:

* centralize security (authn/authz)
* provide rate limiting / throttling to prevent abuse
* offer monitoring/logging for all API traffic
* enable response aggregation (backend-for-frontend)
* allow backend services to evolve without breaking clients

This pattern is used heavily in:

* enterprise microservices
* platform engineering
* cloud-native integration and API management

---

## ✅ Conclusion

In Lab 15, I implemented a functional API Gateway pattern using Apache Camel:

* Created mock microservices (users/products/orders)
* Built a gateway with mediation routes
* Added authentication + authorization
* Enforced rate limits using throttling
* Aggregated multiple backend responses into a dashboard
* Tested performance, security, and throttling behavior

---
