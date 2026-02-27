#!/bin/bash
# Lab 17 - Using EIPs for Scalable Integrations (Apache Camel)
# Commands Executed During Lab (sequential, no explanations)

mkdir camel-eip-lab
cd camel-eip-lab

mvn archetype:generate -DgroupId=com.alnafi.camel.eip \
 -DartifactId=camel-eip-patterns \
 -DarchetypeArtifactId=maven-archetype-quickstart \
 -DinteractiveMode=false

cd camel-eip-patterns

code pom.xml
nano pom.xml

mvn clean compile

mkdir -p src/main/java/com/alnafi/camel/eip/splitter
mkdir -p src/main/resources

nano src/main/java/com/alnafi/camel/eip/splitter/Order.java
nano src/main/java/com/alnafi/camel/eip/splitter/OrderItem.java
nano src/main/java/com/alnafi/camel/eip/splitter/SplitterRoute.java

nano src/main/java/com/alnafi/camel/eip/SplitterApplication.java

mvn clean compile exec:java -Dexec.mainClass="com.alnafi.camel.eip.SplitterApplication"

^C

mkdir -p src/main/java/com/alnafi/camel/eip/aggregator

nano src/main/java/com/alnafi/camel/eip/aggregator/OrderAggregationStrategy.java
nano src/main/java/com/alnafi/camel/eip/aggregator/AggregatedOrder.java
nano src/main/java/com/alnafi/camel/eip/aggregator/AggregatorRoute.java

nano src/main/java/com/alnafi/camel/eip/SplitterAggregatorApplication.java

mvn clean compile exec:java -Dexec.mainClass="com.alnafi.camel.eip.SplitterAggregatorApplication"

^C

mkdir -p src/main/java/com/alnafi/camel/eip/recipientlist

nano src/main/java/com/alnafi/camel/eip/recipientlist/OrderRecipientListResolver.java
nano src/main/java/com/alnafi/camel/eip/recipientlist/RecipientListRoute.java

nano src/main/java/com/alnafi/camel/eip/CompleteEIPApplication.java

mvn clean compile exec:java -Dexec.mainClass="com.alnafi.camel.eip.CompleteEIPApplication"

^C

mkdir -p src/main/java/com/alnafi/camel/eip/rest

nano pom.xml

nano src/main/java/com/alnafi/camel/eip/rest/EIPRestRoute.java

nano src/main/java/com/alnafi/camel/eip/CompleteEIPRestApplication.java

mvn clean compile

mvn exec:java -Dexec.mainClass="com.alnafi.camel.eip.CompleteEIPRestApplication"

cd ~/camel-eip-lab/camel-eip-patterns

curl -s http://localhost:8080/api/eip/sample

nano rest-order.json

curl -s -X POST http://localhost:8080/api/eip/order \
 -H "Content-Type: application/json" \
 -d @rest-order.json

^C
