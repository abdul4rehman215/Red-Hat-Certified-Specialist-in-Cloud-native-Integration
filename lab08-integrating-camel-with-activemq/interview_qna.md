# 🛠️ Troubleshooting Guide — Lab 08: Integrating Camel with Apache ActiveMQ

> This guide lists common issues encountered while installing ActiveMQ, integrating Camel with JMS, and testing message flows across queues/topics.

---

## ✅ Issue 1: ActiveMQ not found under `/opt`

### **Problem**
```bash
ls -la /opt/activemq* 2>/dev/null || echo "ActiveMQ not found in /opt"
# ActiveMQ not found in /opt
````

### **Likely Cause**

ActiveMQ was not pre-installed on the cloud VM image.

### **Fix**

Download and install ActiveMQ:

```bash id="p4j1ar"
wget https://archive.apache.org/dist/activemq/5.17.6/apache-activemq-5.17.6-bin.tar.gz
tar -xzf apache-activemq-5.17.6-bin.tar.gz
sudo mv apache-activemq-5.17.6 /opt/activemq
sudo ln -sf /opt/activemq /opt/activemq-current
sudo chown -R $USER:$USER /opt/activemq
```

---

## ✅ Issue 2: ActiveMQ starts but ports are not visible

### **Problem**

Running:

```bash
netstat -tlnp | grep -E ':(61616|8161)'
```

returns:

```bash
-bash: netstat: command not found
```

### **Likely Cause**

Minimal Ubuntu images may not have `net-tools` installed.

### **Fix**

Install net-tools and retry:

```bash id="e6a6oe"
sudo apt update -y
sudo apt install -y net-tools
netstat -tlnp | grep -E ':(61616|8161)'
```

---

## ✅ Issue 3: ActiveMQ web console returns HTTP 401

### **Problem**

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8161/admin/
# 401
```

### **Likely Cause**

Web console requires authentication (this is normal).

### **Fix**

Test with basic auth:

```bash id="rj4lcm"
curl -s -u admin:admin -o /dev/null -w "%{http_code}" http://localhost:8161/admin/
```

Expected:

```text
200
```

---

## ✅ Issue 4: Camel app fails to connect to broker

### **Problem**

Camel starts but JMS consumers fail, or you see connection errors.

### **Likely Cause**

* Broker not running on `tcp://localhost:61616`
* Port blocked / wrong URL
* Wrong credentials
* Missing dependencies (camel-jms / activemq-client)

### **Fix**

1. Verify broker:

```bash id="dhmyl8"
./bin/activemq status
netstat -tlnp | grep 61616
```

2. Verify `ActiveMQConfig.java` broker URL:

```java id="mx0v6m"
private static final String BROKER_URL = "tcp://localhost:61616";
```

3. Verify Camel dependencies exist in `pom.xml`:

* `camel-jms`
* `activemq-client`
* `activemq-pool`

---

## ✅ Issue 5: Messages sent, but receiver gets “timeout”

### **Problem**

Receiver prints:

```text
No message received ... (timeout)
```

### **Likely Cause**

* Sending to wrong queue
* Route not started / Camel app not running
* Consumer reading from wrong destination
* Message stuck in a different routing branch

### **Fix**

* Ensure Camel app is running in one terminal:

```bash id="t5qj6m"
mvn exec:java -Dexec.mainClass="com.example.camel.CamelActiveMQApplication"
```

* Confirm sender option matches expected queue:

  * option 1 → `input.queue`
  * option 2 → `order.queue`
  * option 3 → `notification.input`
  * option 4 → `batch.input`
  * option 5 → `request.queue`
  * option 6 → `risky.queue`

* Confirm receiver option matches correct output queue.

---

## ✅ Issue 6: Publish-subscribe sends 3 topics but only 2 outputs appear

### **Problem**

You publish to topics (email, sms, push) but only get 2 messages in `processed.notifications`.

### **Likely Cause**

Missing subscriber route for one topic (commonly push).

### **Fix**

Add subscriber route:

```java id="m7rcq0"
from("jms:topic:push.notifications")
    .routeId("push-notification-subscriber")
    .log("Push notification received: ${body}")
    .transform(simple("PUSH: ${body}"))
    .to("jms:queue:processed.notifications");
```

---

## ✅ Issue 7: Request-reply route shows response in logs but receiver sees nothing

### **Problem**

Camel logs show response creation but no message appears in any queue.

### **Likely Cause**

Route transforms and logs response but does not send to an observable destination.

### **Fix**

Send response to a queue:

```java id="my4chf"
from("jms:queue:request.queue")
    .routeId("request-reply-route")
    .log("Processing request: ${body}")
    .transform(simple("Response to: ${body}"))
    .log("Sending response: ${body}")
    .to("jms:queue:response.queue");
```

Then receive from `response.queue`.

---

## ✅ Issue 8: Risky route failures don’t show up in DLQ

### **Problem**

You expect failures to go to `dead.letter.queue` but nothing appears.

### **Likely Cause**

* Not enough messages to trigger random failure probability
* DLQ route requires multiple attempts (redelivery)
* Receiver is checking the wrong queue

### **Fix**

* Send multiple messages to increase chances:

```text
risky-message-1
risky-message-2
risky-message-3
...
```

* Check:
* success → `success.queue`
* failures → `dead.letter.queue`

---

## ✅ Issue 9: ActiveMQ won’t stop cleanly

### **Problem**

Stop command hangs or takes time.

### **Likely Cause**

ActiveMQ waits for process shutdown and file locks.

### **Fix**

Use:

```bash id="1midr1"
/opt/activemq/bin/activemq stop
```

Then verify PID is gone:

```bash id="h8wti4"
ps aux | grep activemq | grep -v grep
```

---

## ✅ Quick Verification Checklist

Run these quickly to confirm everything is healthy:

```bash id="u6oh2t"
# Broker
```
/opt/activemq/bin/activemq status
netstat -tlnp | grep -E ':(61616|8161)'
```

# Web console
```
curl -s -u admin:admin -o /dev/null -w "%{http_code}" http://localhost:8161/admin/
```

# Build + run Camel
```
cd ~/camel-activemq-lab
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.camel.CamelActiveMQApplication"
```

---
