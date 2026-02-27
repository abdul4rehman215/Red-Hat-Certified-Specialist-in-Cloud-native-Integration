# 🎤 Interview Q&A — Lab 09: Asynchronous Messaging with Apache Kafka (Camel + Kafka)

## 1) What does “asynchronous messaging” mean in Kafka-based systems?
It means producers publish events to topics without waiting for consumers to process them. Consumers can process messages later and independently, enabling loose coupling and scalable architectures.

## 2) Why do Kafka deployments traditionally use ZooKeeper?
In Kafka 2.8 and earlier (common lab setups), ZooKeeper manages broker metadata and coordination (leader election, cluster state). Newer Kafka versions can run without ZooKeeper (KRaft), but this lab used the ZooKeeper-based setup.

## 3) What is a Kafka topic and why did you create `order-events` and `notification-events`?
A topic is a named stream of records.  
- `order-events` carries order JSON events  
- `notification-events` carries derived alert messages (e.g., high-value order notifications)

## 4) What is a partition and why did `order-events` use 3 partitions?
Partitions enable parallelism and scalability. With 3 partitions, multiple consumers in a group could share load. It also models real-world event streams where throughput matters.

## 5) What is a consumer group and why use multiple consumer groups in this lab?
A consumer group is a set of consumers that share work for a topic.  
Multiple consumer groups let different services process the same topic independently:
- `order-logger` logs all orders
- `high-value-processor` detects high-value orders and emits notifications
- `customer-processor` applies VIP handling logic
- `notification-processor` consumes notifications

## 6) How did Camel produce messages to Kafka?
Using the Camel Kafka endpoint:
```text
kafka:order-events?brokers=localhost:9092
````

The route marshalled an Order object to JSON and sent it to Kafka with String serializers.

## 7) How did Camel consume messages from Kafka?

Using Camel Kafka consumer endpoints with `groupId` + deserializers:

```text
kafka:order-events?brokers=localhost:9092&groupId=order-logger
```

Messages were read as Strings, then unmarshalled into `Order` objects using Jackson.

## 8) Why use `camel-jackson` in this lab?

It provides JSON marshalling/unmarshalling support so Camel can convert:

* Order object → JSON string (producer)
* JSON string → Order object (consumer)

## 9) How did you implement “high-value order” logic?

A consumer route (group `high-value-processor`) read from `order-events`, unmarshalled JSON into Order, then filtered:

```text
${body.price} > 500
```

If true, it created a notification message and produced it into `notification-events`.

## 10) How did you implement VIP customer logic?

The `customer-processor` consumer group checked:

```text
${body.customerId} == 'CUST001'
```

For VIP orders it printed special handling and simulated a 10% discount.

## 11) How did you verify the topics contained the expected messages?

Using Kafka CLI:

* `kafka-console-consumer.sh --topic order-events --from-beginning`
* `kafka-console-consumer.sh --topic notification-events --from-beginning`

This confirmed the raw messages in Kafka independently of Camel.

## 12) Why did you manually produce messages with `kafka-console-producer.sh`?

To validate that the consumers and routing logic work for external producers, not only for messages generated inside Camel’s timer route.

## 13) What does consumer lag represent and how did you check it?

Lag is the difference between how many messages exist and how many the consumer group has processed.
Checked via:

```bash
kafka-consumer-groups.sh --group order-logger --describe
```

Lag `0` indicated consumers were keeping up with the stream.

## 14) What happened when Kafka was stopped during the lab?

Camel/Kafka clients logged warnings like:

* connection could not be established
  Producers/consumers attempted to reconnect, demonstrating resilience behavior.

## 15) How did you confirm recovery after restarting Kafka?

After broker restart, the producer resumed sending and consumers resumed receiving orders. This confirmed that the integration recovered without manual route restarts.

---
