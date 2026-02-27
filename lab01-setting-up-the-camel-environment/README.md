# 🧪 Lab 01: Setting Up the Camel Environment

---

## 🎯 Objective

This lab focuses on building a complete **Apache Camel integration development environment** on Linux by installing and validating:

- ✅ Apache Camel (integration framework)
- ✅ Apache Karaf (OSGi runtime container for Camel)
- ✅ Apache ActiveMQ (message broker for JMS-based routes)

By the end of this lab, I successfully deployed and executed a **File → JMS → File** Camel route inside Karaf and verified message flow using practical testing.

---

## 🧠 What I Achieved (Learning Goals)

By completing this lab, I was able to:

- Install Apache Camel and required runtime components (Karaf + ActiveMQ)
- Configure environment variables (`JAVA_HOME`, `KARAF_HOME`, `ACTIVEMQ_HOME`, `CAMEL_HOME`)
- Build a clean workspace directory structure for integration labs
- Install Camel features inside Karaf (`camel-core`, `camel-jms`, `camel-activemq`, `camel-blueprint`)
- Deploy a Blueprint XML route into Karaf’s `deploy/` directory
- Validate route execution using test input files and output verification
- Monitor route health and performance stats using Karaf Camel commands

---

## 📌 Prerequisites

Before starting this lab, the following knowledge helped:

- Basic Linux command-line usage
- Familiarity with Java terminology
- Basic understanding of XML configuration files
- Basic networking knowledge (localhost, ports)
- Enterprise Integration Patterns (helpful but not required)

---

## 🧰 Technical Requirements

- Java 11+ (OpenJDK recommended)
- Minimum 2GB RAM
- Minimum 2GB free disk space
- Internet access (downloads)

---

## 🖥️ Lab Environment

This lab was completed on a **cloud-based training Linux environment**.

| Component | Details |
|----------|---------|
| OS | Ubuntu 20.04 LTS |
| Java | OpenJDK 11 |
| Tools Installed | Apache Karaf 4.4.3, ActiveMQ 5.17.2, Apache Camel 3.20.7 |
| Network | localhost ports used (8161, 61616) |

> ⚠️ Note: Hostnames/usernames shown in terminal output are from the lab VM session logs and are kept as-is in `output.txt` for authenticity.

---

## ✅ Lab Tasks Overview

### ✅ Task 1: Install Apache Camel and Required Components

- Verified Java runtime and compiler availability
- Configured `JAVA_HOME` (persisted via `~/.bashrc`)
- Created a working directory structure for clean organization
- Downloaded and installed:
  - Apache Karaf (OSGi runtime)
  - Apache ActiveMQ (JMS broker)
  - Apache Camel distribution
- Configured and persisted:
  - `KARAF_HOME`
  - `ACTIVEMQ_HOME`
  - `CAMEL_HOME`

---

### ✅ Task 2: Set Up a Basic Camel Context (Karaf + Camel Features)

- Started ActiveMQ broker and confirmed it was running
- Started Karaf container
- Added Camel feature repository into Karaf
- Installed required Camel features:
  - `camel-core`
  - `camel-blueprint`
  - `camel-jms`
  - `camel-activemq`
- Verified Camel features were installed and started

---

### ✅ Task 3: Verify Setup by Running a Simple Camel Route

- Created Blueprint XML (`camel-context.xml`) defining:
  - Route 1: **File → JMS Queue**
  - Route 2: **JMS Queue → File**
- Deployed Blueprint into Karaf `deploy/`
- Verified bundle deployment and route status via Karaf console
- Created test files and validated successful message transfer:
  - Input files placed in `~/camel-lab/input/`
  - Output files created in `~/camel-lab/output/`
- Verified route statistics using `camel:route-info`
- Tested multiple file processing and confirmed output growth

---

## ✅ Verification & Validation

The setup was validated using these checks:

- Java version 11+ confirmed
- `JAVA_HOME` set and persisted
- ActiveMQ started successfully and status confirmed
- Karaf console launched successfully
- Camel features installed and running inside Karaf
- Blueprint bundle deployed and shown as **Active**
- Routes listed and status shown as **Started**
- Messages successfully moved from `input/` → `output/`
- Route statistics showed successful exchanges with zero failures

---

## 📂 Repository Structure (Lab Folder)

```text
lab01-setting-up-the-camel-environment/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
└── scripts/
    └── camel-context.xml
````

---

## 🧾 Result

✅ Apache Camel environment installed successfully
✅ ActiveMQ broker running and verified
✅ Karaf container running with Camel features installed
✅ Camel Blueprint deployed successfully
✅ File → JMS → File message routing validated with multiple files
✅ Route stats showed successful exchanges with no failures

---

## 🌍 Why This Matters

A working Camel environment is foundational for enterprise integration because it enables:

* Reliable messaging and decoupled communication
* Integration between microservices and legacy systems
* Event-driven workflows using brokers (JMS)
* Cloud-native integration patterns used in real production environments

This lab builds the base required for advanced certification labs involving:

* REST integrations
* Transformations and EIPs
* Error handling + retries
* Messaging patterns + routing strategies

---

## 🧩 Real-World Applications

Skills developed here apply directly to:

* Cloud-native integration engineering
* Enterprise middleware and integration platforms
* Message-driven microservices
* Building routing pipelines for distributed systems
* Systems interoperability using brokers (ActiveMQ/JMS)

---

## ✅ Conclusion

In this lab, I successfully built an Apache Camel integration environment by installing and configuring Karaf and ActiveMQ, then validating the setup using a working Camel route deployed via Blueprint XML.

This establishes a strong foundation for upcoming labs involving Camel routes, processors, enterprise integration patterns, and production-grade integration workflows.

---
