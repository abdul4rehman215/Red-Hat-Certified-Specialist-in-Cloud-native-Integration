# 🧪 Lab 08: Integrating Camel with Apache ActiveMQ

> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** `toor`  
> **Focus:** Integrating Apache Camel routes with **Apache ActiveMQ** for broker-based messaging, including multiple Enterprise Integration Patterns (EIPs)

---

## 🎯 Objectives

By the end of this lab, I was able to:

- Set up and configure an **Apache ActiveMQ** message broker
- Create Apache Camel routes that integrate with ActiveMQ for message-based communication
- Implement message producers and consumers using Camel + ActiveMQ
- Test end-to-end communication through a broker (queues + topics)
- Understand fundamentals of EIPs using Camel + ActiveMQ
- Configure connection pooling and basic error handling for more robust messaging

---

## 📌 Prerequisites

Before performing this lab, the following knowledge/tools were needed:

- Basic Java programming concepts
- Maven fundamentals
- XML configuration familiarity
- Understanding of message-oriented middleware concepts
- Basic Linux CLI usage
- Java JDK 11+ available

---

## 🧰 Lab Environment Setup

Cloud environment came with required tools pre-installed:

- Java JDK 11
- Apache Maven 3.8+
- Apache ActiveMQ 5.17+
- Apache Camel 3.20+
- nano/vim editors
- Dependencies available via Maven repositories

---

## 🗂️ Repository Structure

```text id="pse5a7"
lab08-integrating-camel-with-activemq/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    ├── activemq/
    │   └── conf/
    │       └── activemq.xml
    └── camel-activemq-lab/
        ├── pom.xml
        ├── logs/                         # runtime-created
        └── src/
            └── main/
                ├── resources/
                │   └── logback.xml
                └── java/
                    └── com/example/camel/
                        ├── ActiveMQConfig.java
                        ├── MessageProcessor.java
                        ├── OrderProcessor.java
                        ├── CamelRouteBuilder.java
                        ├── CamelActiveMQApplication.java
                        ├── MessageSender.java
                        └── MessageReceiver.java
```

> ✅ Notes:

* ActiveMQ configuration changes are captured under `scripts/activemq/conf/activemq.xml` for GitHub.
* The Camel integration project lives under `scripts/camel-activemq-lab/`.
* Runtime artifacts like `logs/` are produced when running the app (kept as realistic evidence folder).

---

## ✅ Task Summary (What I Did)

### ✅ Task 1: Set up an ActiveMQ Broker

#### 1) Verify installation

* Checked `/opt/activemq*` and confirmed ActiveMQ was not present initially.

#### 2) Download, extract, and install ActiveMQ

* Downloaded `apache-activemq-5.17.6-bin.tar.gz`
* Extracted and moved it to `/opt/activemq`
* Created symlink `/opt/activemq-current`
* Adjusted ownership and validated installation path

#### 3) Configure ActiveMQ

* Backed up original `activemq.xml`
* Updated broker configuration to:

  * enable multiple transport connectors (OpenWire, AMQP, STOMP, MQTT, WS)
  * set basic memory/store/temp limits
  * enable persistence with KahaDB

#### 4) Start and verify broker

* Started ActiveMQ with `./bin/activemq start`
* Confirmed status running
* Verified process with `ps`
* Installed `net-tools` because `netstat` was missing
* Confirmed ports listening:

  * `61616` (OpenWire)
  * `8161` (Web console)

#### 5) Validate web console

* Confirmed Jetty up with `curl` returning `401` unauthenticated
* Confirmed authenticated access returns `200` using `admin/admin`

---

### ✅ Task 2: Create Camel Route with ActiveMQ Integration

#### 1) Create Maven project

* Created `~/camel-activemq-lab`
* Added Maven directory structure for source and resources

#### 2) Create `pom.xml` dependencies

Included:

* camel-core, camel-main
* camel-jms
* activemq-client
* **activemq-pool** (connection pooling)
* logging (slf4j + logback)
* basic test dependencies

#### 3) Configure logging

* Added `logback.xml`
* Created `logs/` directory for file logging

#### 4) Build connection configuration

* Implemented `ActiveMQConfig.java`:

  * broker URL `tcp://localhost:61616`
  * pooled connection factory
  * JMS component configuration with consumer tuning

---

### ✅ Task 3: Implement Messaging + EIP Patterns and Test End-to-End

#### Camel routes implemented (`CamelRouteBuilder.java`)

This lab implemented multiple EIP patterns:

1. **Simple transformation (Queue → Queue)**

* `input.queue` → process → `output.queue`

2. **Content-based routing (Orders)**

* `order.queue` → random APPROVED/PENDING → `fulfillment.queue` or `review.queue`

3. **Publish-Subscribe (Topics)**

* `notification.input` → multicast to topics:

  * `email.notifications`
  * `sms.notifications`
  * `push.notifications`

4. **Topic Subscribers**

* email → `processed.notifications`
* sms → `processed.notifications`
* push → `processed.notifications`

5. **Aggregation**

* `batch.input` aggregates 3 messages → splits → `batch.output`

6. **Request-Reply**

* `request.queue` transforms → sends response to `response.queue`

7. **Dead Letter Channel**

* `risky.queue` simulates random failures
* on repeated failure routes to `dead.letter.queue`
* on success routes to `success.queue`

#### Producer/Consumer utilities

To validate end-to-end behavior, I used:

* `MessageSender.java` (menu-based producer)
* `MessageReceiver.java` (menu-based consumer)

✅ This confirmed message flow through:
Producer → ActiveMQ Broker → Camel Route → Output Queue/Topic → Consumer

---

## ✅ Verification (Evidence of Success)

This lab was verified by:

* ActiveMQ running:

  * `./bin/activemq status` → running
  * ports listening on `61616` and `8161`
* Web console accessible:

  * `curl http://localhost:8161/admin/` → 401
  * `curl -u admin:admin ...` → 200
* Maven build success:

  * `mvn clean compile`
  * `mvn package` produced JAR under `target/`
* Camel routes running and processing messages
* Sender/Receiver utilities confirming outputs from:

  * output.queue
  * fulfillment.queue / review.queue
  * processed.notifications
  * batch.output
  * response.queue
  * success.queue / dead.letter.queue

---

## 📌 What I Learned

* How to install, configure, and run an ActiveMQ broker on Linux
* How to integrate Camel JMS routes with ActiveMQ queues/topics
* How to implement multiple EIPs using Camel routing DSL
* How to validate messaging pipelines using producer/consumer utilities
* How connection pooling (`activemq-pool`) improves JMS client efficiency
* How to use Dead Letter Channel for failure isolation and recovery

---

## 🌍 Why This Matters (Real-World Relevance)

Message brokers are a core building block in enterprise systems:

* decouple services
* support async processing
* increase reliability and throughput
* enable integration between heterogeneous systems

Camel + ActiveMQ is a practical combo used in:

* integration middleware
* message-driven microservices
* event-driven architectures
* enterprise workflows

---

## ✅ Result

✅ ActiveMQ installed, configured, started, verified
✅ Camel integrated with ActiveMQ JMS queues/topics
✅ End-to-end messaging verified using Sender/Receiver
✅ Verified EIP patterns:

* Queue → Queue transformation
* Content-based routing
* Publish-subscribe
* Aggregation
* Request-reply
* Dead letter channel

