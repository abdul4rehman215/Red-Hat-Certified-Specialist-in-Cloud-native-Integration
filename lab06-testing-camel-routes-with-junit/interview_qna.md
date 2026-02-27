# 🎤 Interview Q&A — Lab 06: Testing Camel Routes with JUnit

## 1) Why do we unit test Apache Camel routes?
Unit testing ensures the route logic (routing decisions, transformations, validations, headers) behaves correctly without relying on real external systems. It improves reliability, prevents regressions, and makes integration changes safer.

## 2) What is `CamelTestSupport` and why is it useful?
`CamelTestSupport` is Camel’s test base class that bootstraps a Camel context for testing. It provides:
- a test CamelContext
- a `ProducerTemplate` (`template`) for sending messages
- utilities like `assertMockEndpointsSatisfied()` for validation

## 3) What is the purpose of using `mock:` endpoints in Camel tests?
Mock endpoints simulate external systems and allow you to:
- capture messages
- verify message count, headers, and body
- validate route behavior without real queues/files/APIs

## 4) How does `isMockEndpoints()` help in testing?
`isMockEndpoints()` automatically replaces matching endpoints in the real route with mock endpoints. This avoids changing production route code just for testing and makes tests cleaner and more isolated.

## 5) Why was `camel-xpath` dependency required in this lab?
Because the routes use `xpath()` expressions. In Camel 3.x, XPath language support is provided by the `camel-xpath` component, so it must be explicitly included.

## 6) Why was Java 17 used instead of Java 11 in this project?
The test code uses Java text blocks (`""" ... """`) which require Java 15+. Java 17 was chosen because it is available on the lab machine and is LTS.

## 7) What is the difference between unit tests and integration tests in this lab?
- **Unit tests** validate individual route logic (priority routing, transformations, validations) using mocks.
- **Integration tests** validate full flow across multiple routes with error handling and concurrent execution.

## 8) How did you verify priority-based routing worked correctly?
By sending HIGH/MEDIUM/LOW XML orders to `direct:processOrder` and asserting:
- only the correct mock queue received exactly 1 message
- other queues received 0 messages

## 9) How did you validate the order validation logic?
By testing:
- **valid order** → route sets `ValidationStatus=VALID` and proceeds
- **invalid order** (empty customerId / negative amount) → throws exception and no downstream queue receives messages

## 10) What types of assertions can you perform with `MockEndpoint`?
Common assertions include:
- `expectedMessageCount(n)`
- `expectedBodiesReceived(...)`
- `expectedHeaderReceived(key, value)`
- predicates on message bodies/headers using `allMessages()` and exchange inspection

## 11) What did the transformation route test validate?
It validated that:
- body becomes extracted customerId (e.g., `CUST001`)
- headers include `CustomerId`
- `ProcessedTimestamp` header is created

## 12) What is the purpose of `assertMockEndpointsSatisfied()`?
It checks all mock endpoint expectations. If any expected counts/headers/bodies don’t match, the test fails—making it the final verification point.

## 13) How did you test concurrency in the integration flow?
By using `template.asyncSendBody()` to send multiple orders concurrently to `direct:orderIntegration`, then asserting all results arrived within a timeout window.

## 14) Why is error handling (`onException`) important in integration routes?
Enterprise integrations must handle invalid input or downstream failures gracefully. Using `onException` allows routing errors to dedicated channels (like `mock:error`) for alerting, retries, or recovery logic.

## 15) What is the practical benefit of generating Surefire reports?
Surefire reports provide:
- test execution summary
- pass/fail visibility
- per-test details
- helpful artifacts for CI/CD pipelines and audit/documentation
