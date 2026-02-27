# 🧪 Lab 11: Exposing RESTful APIs with Apache Camel (REST DSL)

> **Track:** Red Hat Certified Specialist in Cloud-Native Integration (Exam)  
> **Lab Focus:** Build and expose RESTful APIs using **Apache Camel REST DSL** with a standalone HTTP server (Jetty).  
> **Status:** ✅ Completed (Cloud Lab Environment)

---

## 🧱 Repository Structure (Lab Format)

```text
lab11-exposing-restful-apis-with-camel/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    └── test-api.sh
```

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Set up and configure **Apache Camel** to expose REST APIs using Camel **REST DSL**
* Implement HTTP verbs: **GET, POST, PUT, DELETE**
* Build a complete RESTful service with request/response handling
* Test REST APIs using **curl** and a reusable test script
* Understand how Camel REST DSL integrates with HTTP components (Jetty)
* Implement error handling and consistent JSON responses

---

## ✅ Prerequisites

Before starting this lab, I had familiarity with:

* REST APIs and HTTP methods
* Java basics
* Maven build workflow
* JSON format and validation
* Linux terminal usage
* Basic understanding of Camel routing (recommended)

---

## 🖥️ Lab Environment

This lab was performed in a **cloud-based training environment** (pre-configured tooling).

| Component             | Details                         |
| --------------------- | ------------------------------- |
| OS                    | Ubuntu Linux (Cloud Lab VM)     |
| Java                  | 11+                             |
| Build Tool            | Apache Maven 3.6+               |
| Integration Framework | Apache Camel 3.20.0             |
| HTTP Server Component | Camel Jetty                     |
| JSON Binding          | Camel Jackson                   |
| API Testing           | curl + (optional Postman)       |
| Extra Tool            | `jq` (installed during testing) |

> 🔒 **Security Note:** Hostnames/IPs shown in raw outputs are sanitized in this repo where needed for safe public sharing.

---

## 🧩 What I Built

A standalone Camel application that exposes a REST API:

* Base URL: `http://localhost:8080/api/users`

### Endpoints Implemented

| Method | Endpoint          | Purpose          | Expected Status |
| ------ | ----------------- | ---------------- | --------------- |
| GET    | `/api/users`      | Fetch all users  | 200             |
| GET    | `/api/users/{id}` | Fetch user by ID | 200 / 404       |
| POST   | `/api/users`      | Create a user    | 201 / 400       |
| PUT    | `/api/users/{id}` | Update a user    | 200 / 404 / 400 |
| DELETE | `/api/users/{id}` | Delete a user    | 204 / 404       |

---

## 🛠️ Tasks Overview (High-Level)

### ✅ Task 1 — Set up a Camel Route to Expose a REST API

* Created a Maven project structure
* Added `pom.xml` with Camel Core, Camel Main, REST DSL, Jetty, Jackson
* Created a basic `User` model
* Built an in-memory `UserService` to manage CRUD operations

### ✅ Task 2 — Define HTTP Verbs for API Endpoints

* Implemented REST endpoints using Camel **REST DSL**
* Configured REST server component (Jetty) + JSON binding (Jackson)
* Added basic error handling + HTTP status codes
* Built and ran the application using Maven

> ⚠️ Realistic Fix Applied:
> The lab text contained a broken Java string literal in an error message (newline split).
> I kept the exact meaning but corrected it as a **single-line Java string** to avoid compilation errors.

### ✅ Task 3 — Test the API using CURL + Postman

* Tested every endpoint using curl (GET/POST/PUT/DELETE)
* Verified error conditions (404, 400)
* Created a reusable validation script `test-api.sh`
* Installed `jq` after the first script run failed (realistic troubleshooting)

---

## 🔁 Execution Flow (How the Lab Ran)

1. Create Maven project structure
2. Add Camel dependencies in `pom.xml`
3. Build Java model + service layer
4. Define REST endpoints using Camel REST DSL
5. Build project with Maven
6. Run application (Jetty listens on port 8080)
7. Test endpoints using curl
8. Create automated test script
9. Install missing dependency (`jq`) and re-run tests
10. Confirm correct status codes and state changes (create/update/delete)

---

## ✅ Verification & Validation

### Confirmed via curl tests:

* ✅ List users returns JSON array + 200
* ✅ User lookup works for valid ID + 200
* ✅ Invalid ID returns JSON error + 404
* ✅ Create user returns new ID + 201
* ✅ Invalid payload returns 400 with clear message
* ✅ Update returns 200 and reflects changed fields
* ✅ Delete returns 204 when valid, 404 if already deleted

### Verified via test script:

* First run failed due to missing `jq` (expected realistic issue)
* Installed `jq`
* Re-ran script successfully and confirmed expected API behavior

> ✅ Realistic observation:
> DELETE for user ID `2` returned **404** during script testing because user `2` was already deleted earlier through manual curl testing — this confirms state persistence.

---

## 📈 Result

* ✅ Camel REST API started successfully
* ✅ All REST endpoints exposed and functional
* ✅ JSON binding works automatically via Jackson
* ✅ Proper HTTP response codes implemented
* ✅ Error cases handled with consistent JSON error responses
* ✅ Automated test script created and validated

---

## 💡 What I Learned

* How to expose REST APIs with **Camel REST DSL**
* How Camel integrates REST definitions with **Jetty HTTP server**
* How to bind POJOs to JSON using **camel-jackson**
* How to implement clean CRUD behavior with correct status codes
* How to validate APIs using curl and automated scripts
* Why consistent error handling improves client integrations

---

## 🌍 Why This Matters

REST APIs are the foundation of:

* Enterprise integration systems
* Microservices architecture
* Cloud-native application design
* Platform interoperability across teams/tools/services

Knowing how to implement REST endpoints using Camel REST DSL is directly useful for:

* Integration engineering roles
* Middleware and platform teams
* Service orchestration pipelines
* Cloud-native integration exam objectives

---

## 🧪 Real-World Applications

* Exposing business services as REST endpoints
* Creating microservices for internal tooling
* Building integration gateways between legacy and modern systems
* Implementing JSON-based interfaces for frontend/mobile applications
* Creating automation and orchestration APIs in platform engineering

---

## ✅ Conclusion

This lab successfully demonstrated how to build a complete REST API using Apache Camel:

* Implemented CRUD endpoints (GET/POST/PUT/DELETE)
* Enabled JSON serialization/deserialization
* Verified behavior with curl + automated test script
* Applied realistic troubleshooting (dependency missing → install jq → re-test)
* Reinforced skills relevant to cloud-native enterprise integration patterns

---
