# 🧠 Lab 11 — Interview Q&A (Apache Camel REST DSL)

> These questions are written to reinforce the key exam + job concepts behind building REST APIs using Apache Camel.

---

## 1) What is Apache Camel REST DSL?
Apache Camel REST DSL is a domain-specific language that lets you define REST endpoints (paths, verbs, request/response types) in Camel routes. It allows you to map HTTP requests directly into Camel route flows using readable REST-style syntax.

---

## 2) In this lab, which HTTP component was used to expose the REST API?
The REST API was exposed using **Jetty** via Camel’s `camel-jetty` component.

---

## 3) What does `restConfiguration()` do in Camel?
`restConfiguration()` sets global REST settings such as:
- REST component (e.g., `jetty`, `undertow`)
- host and port
- binding mode (`json`, `off`)
- CORS configuration
- data format properties like pretty printing

---

## 4) What is `RestBindingMode.json` used for?
It enables **automatic JSON serialization/deserialization**.  
Camel converts:
- incoming JSON → Java objects (POJOs)
- outgoing Java objects → JSON responses  
(using Jackson when `camel-jackson` is present).

---

## 5) Why do we define `.consumes()` and `.produces()` in REST DSL?
- `.consumes("application/json")` tells clients what content type the API expects in requests.
- `.produces("application/json")` tells clients what content type the API returns in responses.  
This improves compatibility and clarity for integrations.

---

## 6) What is the purpose of `direct:` endpoints like `direct:getAllUsers`?
They act as **internal routing endpoints** within Camel.  
REST endpoints can forward requests to `direct:` routes, keeping REST definitions clean and separating:
- API definition (REST DSL)
- implementation logic (`from("direct:...")` routes)

---

## 7) How did the lab handle error responses such as “User not found”?
The route checked if the user exists:
- If not found → set HTTP status code to **404**
- Return a JSON error body such as:
  `{"error": "User not found", "id": 999}`

---

## 8) What is the difference between 400, 404, 500 HTTP codes in this lab?
- **400 Bad Request** → client sent invalid data (missing required fields like name/email)
- **404 Not Found** → resource ID does not exist (user not present)
- **500 Internal Server Error** → unexpected server-side failure (generic exception handler)

---

## 9) How was input validation handled for POST/PUT requests?
Basic validation was done in the route processor:
- Ensure `name` and `email` are not null
- If validation fails → return **400** with a JSON error response

---

## 10) Why is implementing correct HTTP status codes important?
It allows API consumers to correctly interpret results:
- 2xx indicates success
- 4xx indicates client mistakes
- 5xx indicates server errors  
This is critical for automation, microservices reliability, and troubleshooting.

---

## 11) What Maven commands were used to build and run the project?
- Build/compile:
  - `mvn clean compile`
- Run:
  - `mvn exec:java -Dexec.mainClass="com.example.camel.CamelRestApplication"`

---

## 12) What problem happened when running `test-api.sh` the first time?
The script failed because `jq` was missing:
- Error: `jq: command not found`

This is a realistic dependency issue when building automation scripts.

---

## 13) How was that issue fixed?
Installed `jq` using apt:
- `sudo apt-get update`
- `sudo apt-get install -y jq`

Then the script ran successfully.

---

## 14) Why did DELETE for user ID 2 return 404 during scripted testing?
User ID 2 had already been deleted earlier during manual curl testing.  
So when the script attempted to delete it again, the API correctly returned **404** (“not found”), proving the API state is persistent and behaving as expected.

---

## 15) What are production improvements you would add to this API?
Examples:
- Input validation with regex (email format validation)
- Authentication/authorization (JWT/OAuth2)
- Swagger/OpenAPI documentation
- Persistent storage (DB integration)
- Request logging + structured logs
- Rate limiting / API gateway integration
- Centralized exception handling with consistent error schemas
