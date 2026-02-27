# 🛠️ Troubleshooting Guide — Lab 01: Setting Up the Camel Environment

> This document lists common issues faced while setting up **Apache Camel + Karaf + ActiveMQ** and how to fix them quickly in a Linux-based lab environment.

---

## ✅ Quick Health Checks (Before Debugging)

Use these checks first:

### 1) Confirm Java is available
```bash
java -version
javac -version
echo $JAVA_HOME
````

### 2) Confirm ActiveMQ broker status

```bash
cd $ACTIVEMQ_HOME
./bin/activemq status
```

### 3) Confirm Karaf is running

If Karaf is running, you should see the Karaf console prompt:

```text
karaf@root()>
```

### 4) Confirm Camel routes deployed and started (Karaf console)

```bash
camel:route-list
```

---

## 🧩 Issue 1: Java Not Found

### ❌ Problem

* Running `java -version` returns:

  * `command not found`
* Or Karaf/ActiveMQ fails because Java isn’t installed.

### ✅ Solution (Ubuntu)

```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

### ✅ Solution (CentOS/RHEL)

```bash
sudo dnf install java-11-openjdk java-11-openjdk-devel
```

### ✅ Verify

```bash
java -version
javac -version
```

---

## 🧩 Issue 2: JAVA_HOME Not Set

### ❌ Problem

* `echo $JAVA_HOME` returns blank
* Some Java-based tools fail to locate JDK correctly

### ✅ Solution

```bash
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### ✅ Verify

```bash
echo $JAVA_HOME
```

---

## 🧩 Issue 3: ActiveMQ Port Already in Use (61616)

### ❌ Problem

ActiveMQ fails to start because port `61616` is already occupied.

### ✅ Identify process using the port

```bash
netstat -tulpn | grep :61616
```

### ✅ Kill the conflicting process (use with caution)

```bash
sudo kill -9 <process_id>
```

### ✅ Retry ActiveMQ start

```bash
cd $ACTIVEMQ_HOME
./bin/activemq start
./bin/activemq status
```

---

## 🧩 Issue 4: ActiveMQ Starts But Web Console Not Accessible (8161)

### ❌ Problem

Cannot access:

* `http://localhost:8161/admin`

### ✅ Fix Checklist

1. Confirm ActiveMQ is running:

```bash
./bin/activemq status
```

2. Confirm port is listening:

```bash
netstat -tulpn | grep :8161
```

3. If using a cloud VM with no GUI/browser:

* You may need port forwarding or CLI-based verification instead.

---

## 🧩 Issue 5: Karaf Console Not Responding / Appears Frozen

### ❌ Problem

Karaf console stops responding after a command or hangs.

### ✅ Solutions

* Press `Ctrl+C` to interrupt the current command.
* If Karaf is unstable, restart cleanly:

```bash
system:shutdown
```

Then start again:

```bash
cd $KARAF_HOME
./bin/karaf
```

---

## 🧩 Issue 6: Camel Features Fail to Install in Karaf

### ❌ Problem

* `feature:repo-add camel 3.20.7` fails
* `feature:install camel-core` fails

### ✅ Likely Causes

* No internet connectivity
* Maven repo not reachable
* Wrong Camel version

### ✅ Fix Checklist

1. Confirm network access:

```bash
ping -c 2 archive.apache.org
```

2. Retry adding repository:

```bash
feature:repo-add camel 3.20.7
```

3. Retry installing features:

```bash
feature:install camel-core
feature:install camel-blueprint
feature:install camel-jms
feature:install camel-activemq
```

---

## 🧩 Issue 7: Blueprint Not Deploying (No Routes Showing)

### ❌ Problem

* Blueprint copied to `$KARAF_HOME/deploy/` but no routes appear
* `camel:route-list` shows nothing

### ✅ Fix Checklist

1. Confirm file exists in deploy folder:

```bash
ls -la $KARAF_HOME/deploy/
```

2. Check bundle list for the XML:

```bash
bundle:list | grep camel-context
```

3. If bundle isn’t active, check Karaf logs:

```bash
log:tail
```

4. Validate XML formatting (common error source):

* missing tags
* wrong schema locations
* wrong Camel namespace

---

## 🧩 Issue 8: Route Started But Files Not Moving to Output

### ❌ Problem

* Routes show `Started`
* Input file exists, but output file doesn’t appear

### ✅ Fix Checklist

1. Confirm input directory exists:

```bash
ls -la ~/camel-lab/input
```

2. Confirm output directory exists:

```bash
ls -la ~/camel-lab/output
```

3. Check Karaf logs for processing activity:

```bash
log:tail
```

4. Ensure directory permissions allow writing:

```bash
chmod 755 ~/camel-lab/input ~/camel-lab/output
```

---

## ✅ Recovery Steps (Safe Reset)

If things become messy, reset using:

### 1) Stop ActiveMQ

```bash
cd $ACTIVEMQ_HOME
./bin/activemq stop
```

### 2) Shutdown Karaf cleanly

Inside Karaf console:

```bash
system:shutdown
```

### 3) Restart ActiveMQ + Karaf

```bash
cd $ACTIVEMQ_HOME && ./bin/activemq start
cd $KARAF_HOME && ./bin/karaf
```

---

## ✅ Notes

* Keep **ActiveMQ running** before testing JMS routes.
* Always verify routes using:

  * `camel:route-list`
* Always verify data movement using:

  * `ls -la ~/camel-lab/output/`
  * `cat <output-file>`

✅ Troubleshooting guide complete.

---
