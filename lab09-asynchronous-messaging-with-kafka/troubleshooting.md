# 🛠️ Troubleshooting Guide — Lab 09: Asynchronous Messaging with Apache Kafka (Camel + Kafka)

> This guide covers common issues while starting Kafka/ZooKeeper, creating topics, running Camel-Kafka routes, and validating consumer groups.

---

## ✅ Issue 1: ZooKeeper not started / Kafka won’t start

### **Problem**
Kafka broker fails to start or exits quickly.

### **Likely Cause**
Kafka (ZooKeeper mode) requires ZooKeeper to be running first.

### **Fix**
Start ZooKeeper, then Kafka:

```bash id="8guf6m"
cd /opt/kafka
bin/zookeeper-server-start.sh config/zookeeper.properties &
sleep 10
bin/kafka-server-start.sh config/server.properties &
````

Verify both processes:

```bash id="eq7q5s"
jps | grep -E "(Kafka|QuorumPeerMain)"
```

Expected:

* `QuorumPeerMain`
* `Kafka`

---

## ✅ Issue 2: Topics fail to create due to wrong flags (replication-factor typo)

### **Problem**

Topic creation errors when flags are typed incorrectly (common typo in lab text).

### **Fix**

Correct flag is `--replication-factor`:

```bash id="h4w1id"
bin/kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
bin/kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1
```

---

## ✅ Issue 3: `~/workspace` does not exist

### **Problem**

```bash
cd ~/workspace
# No such file or directory
```

### **Fix**

Create it and retry:

```bash id="yxx4x8"
mkdir -p ~/workspace
cd ~/workspace
pwd
```

---

## ✅ Issue 4: “Topic does not exist” when consuming or producing

### **Problem**

Consumers/producers fail with missing topic errors.

### **Fix**

List and verify topics:

```bash id="b8o1n4"
cd /opt/kafka
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

If missing, create them:

```bash id="bvsb2q"
bin/kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
bin/kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1
```

---

## ✅ Issue 5: Camel produces/consumes but nothing appears (topic name mismatch)

### **Problem**

Camel routes run but no messages appear in CLI consumers.

### **Likely Cause**

Camel Kafka endpoint topic name does not match the actual created topic name.

### **Fix**

Ensure topic names match exactly:

* `order-events`
* `notification-events`

Example endpoint:

```text id="o3q6aq"
kafka:order-events?brokers=localhost:9092
kafka:notification-events?brokers=localhost:9092
```

---

## ✅ Issue 6: Kafka URI breaks due to line breaks (serializers/deserializers)

### **Problem**

Route fails to start or Kafka endpoint parsing fails.

### **Likely Cause**

Endpoint URI split across lines in Java strings (especially `valueSerializer` and `valueDeserializer`).

### **Fix**

Keep Kafka URI as a single concatenated string:

```java id="g3c5xn"
.to("kafka:order-events?brokers=localhost:9092"
    + "&keySerializer=org.apache.kafka.common.serialization.StringSerializer"
    + "&valueSerializer=org.apache.kafka.common.serialization.StringSerializer")
```

---

## ✅ Issue 7: JSON serialization / deserialization fails

### **Problem**

Camel throws Jackson errors or cannot convert JSON to `Order`.

### **Likely Cause**

* missing default constructor
* missing getters/setters
* incorrect JSON field names

### **Fix**

Ensure `Order` has:

* default constructor
* getters/setters for all fields
* correct JSON mappings (`@JsonProperty` if needed)

---

## ✅ Issue 8: Consumer groups not visible / unexpected group names

### **Problem**

`kafka-consumer-groups.sh --list` doesn’t show expected groups.

### **Likely Cause**

Group IDs differ from what you expect, or consumers haven’t started consuming yet.

### **Fix**

Confirm group IDs in Camel endpoints:

```text id="v0q5p0"
groupId=order-logger
groupId=high-value-processor
groupId=notification-processor
groupId=customer-processor
```

Then list groups:

```bash id="c2b4wz"
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

---

## ✅ Issue 9: Kafka connection refused / broker down

### **Problem**

Camel logs:

```text
Connection could not be established. Broker may not be available.
```

### **Fix**

Verify broker process is running:

```bash id="n6p7v9"
jps | grep Kafka
```

Restart broker if needed:

```bash id="m4m3uw"
cd /opt/kafka
bin/kafka-server-start.sh config/server.properties &
```

---

## ✅ Issue 10: Application stops processing after Kafka restart

### **Problem**

After stopping Kafka, the app shows warnings and seems stuck.

### **Likely Cause**

Clients need time to reconnect and refresh metadata.

### **Fix**

Restart Kafka and wait briefly. Confirm producer logs resume:

```bash id="p9r9q1"
bin/kafka-server-start.sh config/server.properties &
```

Then observe Camel logs for new “Generating new order…” cycles.

---

## ✅ Issue 11: Want to verify partitions and performance quickly

### **Fix**

Describe the topic:

```bash id="w9m2t4"
bin/kafka-topics.sh --describe --topic order-events --bootstrap-server localhost:9092
```

Check lag:

```bash id="g8v3gt"
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group order-logger --describe
```

Lag `0` = consumers caught up.

---

## ✅ Quick Health Checklist

# processes
```
jps | grep -E "(Kafka|QuorumPeerMain)"
```

# topics
```
cd /opt/kafka
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

# run camel project
```
cd ~/workspace/kafka-camel-integration
mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.kafka.lab.KafkaCamelApplication"
```

---
