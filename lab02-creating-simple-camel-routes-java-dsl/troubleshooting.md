# 🛠️ Troubleshooting Guide — Lab 02: Creating Simple Camel Routes Using Java DSL

> This document covers common issues encountered when building and running a **standalone Apache Camel (Java DSL)** application using Maven.

---

## ✅ Quick Health Checks (Run These First)

### 1) Confirm Java + Maven
```bash
java -version
mvn -version
````

### 2) Confirm you’re in the correct directory

```bash
pwd
ls -la
```

You should be inside:

```text
~/camel-java-dsl-lab/simple-routes
```

### 3) Confirm required directories exist

```bash
ls -la input output processed simple-output
```

If missing:

```bash
mkdir -p input output processed simple-output
```

---

## 🧩 Issue 1: `mvn` command not found

### ❌ Problem

Running `mvn` returns:

```text
mvn: command not found
```

### ✅ Solution (Ubuntu)

```bash
sudo apt update
sudo apt install -y maven
```

### ✅ Solution (CentOS/RHEL)

```bash
sudo dnf install -y maven
```

### ✅ Verify

```bash
mvn -version
```

---

## 🧩 Issue 2: Maven build fails due to dependency resolution

### ❌ Problem

You see errors like:

* Could not resolve dependencies
* Transfer failed / connection refused
* Artifact not found

### ✅ Fix Checklist

1. Confirm network connectivity:

```bash
ping -c 2 repo.maven.apache.org
```

2. Retry build (sometimes it’s temporary):

```bash
mvn clean compile
```

3. Inspect dependency tree to spot conflicts:

```bash
mvn dependency:tree
```

4. If the version is wrong, confirm Camel version used in `pom.xml`:

* This lab uses:

```xml
<camel.version>3.20.2</camel.version>
```

---

## 🧩 Issue 3: Compilation errors (Java syntax / package issues)

### ❌ Problem

`mvn clean compile` fails with:

* cannot find symbol
* package does not exist
* class not found

### ✅ Fix Checklist

1. Ensure file paths match package declaration:
   Example:

```java
package com.alnafi.camel;
```

Must be stored in:

```text
src/main/java/com/alnafi/camel/
```

2. Confirm both Java files exist:

```bash
find src/main/java -type f -name "*.java" -print
```

3. Retry compilation:

```bash
mvn clean compile
```

---

## 🧩 Issue 4: App runs but no output files are generated

### ❌ Problem

Application starts, but nothing appears in:

* `output/`
* `processed/`
* `simple-output/`

### ✅ Root Causes & Fixes

#### ✅ Cause A: App is not running

Timer routes only produce output while the app is running.

**Check**: Your terminal should still be running `mvn exec:java`.

#### ✅ Cause B: Missing directories

```bash
mkdir -p input output processed simple-output
```

#### ✅ Cause C: Permission issue

```bash
ls -la
chmod -R u+rwX output processed simple-output input
```

---

## 🧩 Issue 5: File route doesn’t process files in `input/`

### ❌ Problem

You create files, but nothing moves into `processed/`.

### ✅ Fix Checklist

1. Ensure files exist:

```bash
ls -la input/
```

2. Ensure files are readable:

```bash
chmod 644 input/*
```

3. Remember: with `noop=true`, input files remain in place.
   This is expected behavior for lab testing.

4. Watch real-time directory change:

```bash
watch -n 2 'ls -la processed/'
```

---

## 🧩 Issue 6: `mvn exec:java` fails because exec plugin not configured

### ❌ Problem

You might see:

* "No plugin found for prefix 'exec'"
* "Failed to execute goal org.codehaus.mojo:exec-maven-plugin"

### ✅ Solution

Run:

```bash
mvn -q -Dexec.mainClass="com.alnafi.camel.CamelApplication" exec:java
```

If still failing, install plugin explicitly in `pom.xml` (optional enhancement):

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
</plugin>
```

Then retry:

```bash
mvn exec:java -Dexec.mainClass="com.alnafi.camel.CamelApplication"
```

---

## 🧩 Issue 7: Logs are too quiet / difficult to debug

### ✅ Solution: Enable Camel debug logs

1. Create resources directory:

```bash
mkdir -p src/main/resources
```

2. Add `simplelogger.properties`:

```properties
org.slf4j.simpleLogger.defaultLogLevel=info
org.slf4j.simpleLogger.log.org.apache.camel=debug
org.slf4j.simpleLogger.showDateTime=true
org.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss
```

3. Restart the app:

```bash
mvn exec:java -Dexec.mainClass="com.alnafi.camel.CamelApplication"
```

---

## 🧩 Issue 8: Output directories fill up and make testing confusing

### ✅ Solution: Clear outputs for clean retesting

```bash
rm -f output/* processed/* simple-output/*
ls -la output/ processed/ simple-output/
```

---

## ✅ Safe Recovery Steps (Reset Workflow)

If you want a clean run:

1. Stop the app:

* Press `Ctrl+C`

2. Clean outputs:

```bash
rm -f output/* processed/* simple-output/*
```

3. Rebuild + run:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.CamelApplication"
```

4. Recreate input test files and verify processing.

---

## ✅ Notes

* Timer-based routes produce output continuously (every 5–10 seconds)
* File routes require actual files placed in `input/`
* `noop=true` keeps original files in input; processed copies appear in `processed/`

---
