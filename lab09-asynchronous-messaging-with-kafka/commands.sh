#!/bin/bash
# Lab 09 - Asynchronous Messaging with Apache Kafka (Camel + Kafka)
# Commands Executed During Lab (Sequential / No Explanations)

cd /opt/kafka
bin/zookeeper-server-start.sh config/zookeeper.properties &

sleep 10
bin/kafka-server-start.sh config/server.properties &

jps | grep -E "(Kafka|QuorumPeerMain)"

bin/kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
bin/kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

cd ~/workspace

mkdir -p ~/workspace
cd ~/workspace
pwd

mvn archetype:generate -DgroupId=com.alnafi.kafka.lab \
 -DartifactId=kafka-camel-integration \
 -DarchetypeArtifactId=maven-archetype-quickstart \
 -DinteractiveMode=false

cd kafka-camel-integration
ls -la

nano pom.xml

mkdir -p src/main/java/com/alnafi/kafka/lab
mkdir -p src/main/resources

nano src/main/java/com/alnafi/kafka/lab/Order.java

nano src/main/java/com/alnafi/kafka/lab/OrderProducerRoute.java

nano src/main/java/com/alnafi/kafka/lab/OrderConsumerRoute.java

nano src/main/java/com/alnafi/kafka/lab/KafkaCamelApplication.java

nano src/main/resources/simplelogger.properties

mvn clean compile

echo "Compilation status: $?"

mvn exec:java -Dexec.mainClass="com.alnafi.kafka.lab.KafkaCamelApplication"

cd /opt/kafka
bin/kafka-console-consumer.sh --topic order-events --from-beginning --bootstrap-server localhost:9092

cd /opt/kafka
bin/kafka-console-consumer.sh --topic notification-events --from-beginning --bootstrap-server localhost:9092

cd /opt/kafka
bin/kafka-console-producer.sh --topic order-events --bootstrap-server localhost:9092

bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group order-logger --describe

bin/kafka-server-stop.sh

bin/kafka-server-start.sh config/server.properties &

bin/kafka-topics.sh --describe --topic order-events --bootstrap-server localhost:9092

bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group order-logger --describe
