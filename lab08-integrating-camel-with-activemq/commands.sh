#!/bin/bash
# Lab 08 - Integrating Camel with Apache ActiveMQ
# Commands Executed During Lab (Sequential / No Explanations)

cd ~
pwd

ls -la /opt/activemq* 2>/dev/null || echo "ActiveMQ not found in /opt"

wget https://archive.apache.org/dist/activemq/5.17.6/apache-activemq-5.17.6-bin.tar.gz

tar -xzf apache-activemq-5.17.6-bin.tar.gz
ls -la | grep activemq

sudo mv apache-activemq-5.17.6 /opt/activemq

sudo ln -sf /opt/activemq /opt/activemq-current

sudo chown -R $USER:$USER /opt/activemq
ls -la /opt | grep activemq

cd /opt/activemq/conf
ls -la | head

cp activemq.xml activemq.xml.backup
ls -la activemq.xml*

nano activemq.xml

cd /opt/activemq
./bin/activemq start

sleep 10

./bin/activemq status

ps aux | grep activemq | grep -v grep

netstat -tlnp | grep -E ':(61616|8161)'

sudo apt update -y
sudo apt install -y net-tools

netstat -tlnp | grep -E ':(61616|8161)'

curl -s -o /dev/null -w "%{http_code}" http://localhost:8161/admin/
curl -s -u admin:admin -o /dev/null -w "%{http_code}" http://localhost:8161/admin/

echo "ActiveMQ Web Console is available at: http://localhost:8161/admin/"
echo "Default credentials: admin/admin"

mkdir -p ~/camel-activemq-lab
cd ~/camel-activemq-lab

mkdir -p src/main/java/com/example/camel
mkdir -p src/main/resources
mkdir -p src/test/java

nano pom.xml

nano src/main/resources/logback.xml
mkdir -p logs

nano src/main/java/com/example/camel/ActiveMQConfig.java

nano src/main/java/com/example/camel/MessageProcessor.java
nano src/main/java/com/example/camel/OrderProcessor.java

nano src/main/java/com/example/camel/CamelRouteBuilder.java

nano src/main/java/com/example/camel/CamelActiveMQApplication.java

cd ~/camel-activemq-lab
mvn clean compile

mvn package

echo "Build completed. JAR file location:"
ls -la target/*.jar

nano src/main/java/com/example/camel/MessageSender.java

nano src/main/java/com/example/camel/MessageReceiver.java

nano src/main/java/com/example/camel/CamelRouteBuilder.java

mvn clean compile

mvn exec:java -Dexec.mainClass="com.example.camel.CamelActiveMQApplication"

mvn exec:java -Dexec.mainClass="com.example.camel.MessageReceiver"

mvn exec:java -Dexec.mainClass="com.example.camel.MessageSender"

curl -s -u admin:admin -o /dev/null -w "%{http_code}" http://localhost:8161/admin/

/opt/activemq/bin/activemq stop
