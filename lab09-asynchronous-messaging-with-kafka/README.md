# 🧪 Lab 09: Asynchronous Messaging with Apache Kafka (Camel + Kafka)

> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** `toor`  
> **Focus:** Apache Camel integration with Apache Kafka for asynchronous messaging (producers, consumers, topics, consumer groups, monitoring, and resilience)

---

## 🎯 Objectives

By the end of this lab, I was able to:

- Set up Apache Kafka integration with Apache Camel
- Create Camel routes to **produce** messages to Kafka topics
- Create Camel routes to **consume** messages from Kafka topics
- Validate end-to-end Kafka message flow using Camel
- Understand asynchronous messaging patterns for cloud-native integration
- Configure Kafka producers/consumers and observe reconnection behavior during broker outages

---

## 📌 Prerequisites

Before starting this lab, the following knowledge was required:

- Apache Camel basics (routes, endpoints, processors)
- Java fundamentals + Maven build workflow
- Messaging concepts (topics, producers, consumers, consumer groups)
- JSON basics (serialization/deserialization)
- Basic Linux command-line usage

---

## 🧰 Lab Environment Setup

The cloud VM used in this lab included:

- OpenJDK 11
- Apache Maven 3.8+
- Apache Kafka 2.8+
- Apache Camel 3.20+
- Text editor (nano/vim)

---

## 🗂️ Repository Structure

```text id="k9x7v1"
lab09-asynchronous-messaging-with-kafka/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    └── kafka-camel-integration/
        ├── pom.xml
        └── src/
            └── main/
                ├── java/
                │   └── com/alnafi/kafka/lab/
                │       ├── KafkaCamelApplication.java
                │       ├── Order.java
                │       ├── OrderProducerRoute.java
                │       └── OrderConsumerRoute.java
                └── resources/
                    └── simplelogger.properties
```

> ✅ Notes:

* Kafka binaries/services were managed under `/opt/kafka` during the lab run (runtime environment).
* The Maven project created via archetype is stored under `scripts/kafka-camel-integration/` for GitHub upload.

---

## ✅ Task Summary (What I Did)

### ✅ Task 1: Set Up Apache Kafka Integration

* Started Kafka ecosystem components:

  * Zookeeper
  * Kafka broker
* Verified both processes were running (`QuorumPeerMain` and `Kafka`)
* Created Kafka topics:

  * `order-events` (3 partitions)
  * `notification-events` (2 partitions)
* Verified topic creation by listing topics

✅ **Real environment adjustment**

* `~/workspace` directory was missing in this VM, so it was created before generating the Maven project.

---

### ✅ Task 2: Create Camel Routes to Produce and Consume Kafka Messages

#### Producer side (asynchronous publisher)

* Implemented a Camel timer-based producer that generates an `Order` every 5 seconds
* Serialized orders to JSON (Jackson)
* Produced JSON messages into Kafka topic: `order-events`

#### Stream processing / routing behavior

* Implemented a consumer route that processes **high-value orders** (`price > 500`)
* For high-value orders, a notification message is generated and produced into topic: `notification-events`

#### Consumer side (multiple consumer groups)

* Implemented multiple consumers with separate Kafka consumer groups:

  * `order-logger` → logs and prints order details
  * `high-value-processor` → filters and produces notification events
  * `notification-processor` → consumes and prints notification alerts
  * `customer-processor` → special handling for VIP customer (`CUST001`) and prints discount logic

✅ **Important correctness fixes applied (to make the lab runnable and consistent)**

* Kafka topic names in routes were aligned to match actual created topics:

  * `order-events` and `notification-events`
* Kafka endpoint URI line-break issues were corrected (serializers/deserializers)
* Consumer group IDs were aligned with the names verified later via `kafka-consumer-groups.sh --list`

---

### ✅ Task 3: Test End-to-End Kafka Message Flow

#### Run and observe live flow

* Built the Maven project successfully
* Ran the Camel application and observed:

  * orders generated every 5 seconds
  * consumers reading orders from Kafka
  * high-value alerts produced and consumed
  * VIP logic triggered for customer `CUST001`

#### Monitor topics directly using Kafka CLI tools

* Used Kafka console consumers to verify topic streams:

  * `kafka-console-consumer.sh --topic order-events`
  * `kafka-console-consumer.sh --topic notification-events`

#### Manual production test

* Produced JSON messages manually into `order-events` using:

  * `kafka-console-producer.sh`
* Verified Camel consumers processed those manual messages correctly

#### Consumer group verification

* Verified consumer groups exist and are consuming:

  * listed groups
  * described `order-logger` group offsets and lag (LAG = 0 observed)

#### Error handling / resilience behavior test

* Stopped Kafka broker temporarily to force connection failures
* Observed client reconnection warnings in Camel logs
* Restarted Kafka broker and confirmed application resumed processing

---

## ✅ Verification (Evidence of Success)

This lab was validated through:

* Kafka + Zookeeper running (JPS showed both processes)
* Topics created and listed:

  * `order-events`, `notification-events`
* Maven build success (`mvn clean compile`)
* Camel app produced/consumed events continuously
* Kafka CLI consumers showed JSON order stream and notification alerts
* Manual producer messages were consumed and triggered business logic
* Consumer groups existed and showed offsets + LAG monitoring
* Broker stop/start demonstrated reconnection + recovery

---

## 📚 Key Concepts Learned

* **Asynchronous messaging**

  * decoupled producers and consumers
  * event-driven processing
* **Kafka fundamentals**

  * topics, partitions, brokers
  * consumer groups and lag tracking
* **Camel + Kafka integration**

  * producing via `kafka:` endpoints
  * consuming via consumer groups
  * JSON marshalling/unmarshalling with Jackson
* **Message routing patterns**

  * filtering (high-value orders)
  * content-based processing (VIP customer logic)
  * notification generation pipeline

---

## 🌍 Why This Matters (Real-World Relevance)

This setup mirrors real enterprise patterns such as:

* e-commerce order event processing (order stream)
* alerting systems (high-value order notifications)
* customer segmentation (VIP handling)
* scalable asynchronous pipelines (partitions + consumer groups)

---

## ✅ Result

* ✅ Kafka services started and verified
* ✅ Topics created and validated
* ✅ Camel producer + consumers implemented
* ✅ High-value notification pipeline working end-to-end
* ✅ Consumer groups verified and monitored
* ✅ Broker outage simulation confirmed reconnection and recovery
* ✅ Ready for GitHub upload 🚀

---
