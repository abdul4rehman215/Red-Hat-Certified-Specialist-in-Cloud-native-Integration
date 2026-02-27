# 🧪 Lab 18: Using Camel Components for File Handling

---

## 🧱 Repository Structure

```text
lab18-camel-components-file-handling/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── pom.xml
├── input/
├── output/
├── processed/
├── error/
├── ftp-upload/
├── ftp-downloaded/
├── ftp-processed/
├── ftp-sftp-errors/
├── sftp-upload/
├── sftp-downloaded/
├── sftp-processed/
├── sync-source/
├── sync-completed/
├── sync-processed/
├── advanced-input/
├── advanced-processed/
├── urgent-output/
├── archive-output/
├── regular-output/
├── size-input/
├── size-processed/
├── large-files/
├── small-files/
├── extension-input/
├── extension-processed/
├── text-files/
├── csv-files/
├── xml-files/
├── other-files/
├── monitored-input/
├── monitored-processed/
├── validated-output/
├── dead-letter-queue/
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── camel/
                        ├── FileHandlingApplication.java
                        ├── FileProcessingRoute.java
                        ├── FtpSftpRoute.java
                        ├── AdvancedFileRoute.java
                        └── MonitoringRoute.java
````

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Configure and use Apache Camel **File** component to consume/produce files
* Set up **FTP and SFTP** transfer operations using Camel **FTP** component
* Implement file-based integration workflows with **error handling**
* Test and validate file processing in realistic enterprise integration scenarios
* Apply file handling best practices for cloud-native integration solutions

---

## 📌 Prerequisites

* Apache Camel routing fundamentals
* Java + Maven basics
* File system operations + FTP/SFTP protocol basics
* Enterprise Integration Patterns (EIPs) understanding
* Linux CLI experience

---

## 🧰 Lab Environment

* **Platform:** Cloud-based Linux lab environment
* **Java:** 11+
* **Maven:** 3.6+
* **Camel:** 3.x (used: 3.20.0)
* **Services/Tools:** OpenSSH server/client, vsftpd, nano/vim

> ✅ Documentation here is written in a platform-neutral way (no vendor/training-platform branding).

---

## ✅ What I Built

This lab builds a complete **file-handling integration pipeline** using Camel components and patterns.

### 1) 📂 Basic File Processing (Local File Component)

A route that:

* Watches `input/`
* Processes file content (adds prefix + uppercases content)
* Writes results to `output/`
* Moves originals to `processed/`
* Sends failures to `error/` using `onException(...)`

### 2) 🌐 FTP + SFTP Transfer Automation (Camel FTP Component)

A set of routes that:

* Uploads local files to **FTP** (`ftp-upload/` → FTP server `/upload`)
* Downloads files from **FTP** (`/download` → `ftp-downloaded/`)
* Uploads local files to **SFTP** (`sftp-upload/` → SFTP `/upload`)
* Downloads files from **SFTP** (`/download` → `sftp-downloaded/`)
* Syncs one file to multiple destinations using **multicast**:

  * local → FTP upload + SFTP upload + local `sync-completed/`

### 3) 🧠 Advanced File Routing (Content / Size / Extension Based)

Implemented advanced integration routes:

* **Content-based routing**

  * If file body contains `URGENT` → `urgent-output/` and FTP `/urgent`
  * If contains `ARCHIVE` → `archive-output/` and SFTP `/archive`
  * Otherwise → `regular-output/`

* **File size-based routing**

  * `> 1000 bytes` → `large-files/`
  * else → `small-files/`

* **Extension-based routing**

  * `.txt` → `text-files/`
  * `.csv` → `csv-files/`
  * `.xml` → `xml-files/`
  * otherwise → `other-files/`

### 4) 🧯 Monitoring + Resilience (Retries + Dead Letter Queue + Stats)

Added a monitored file route that:

* Validates incoming files from `monitored-input/`
* Retries failures with:

  * `maximumRedeliveries(3)`
  * `redeliveryDelay(2000)`
* Sends poison messages to `dead-letter-queue/`
* Emits periodic stats using a timer route (operational visibility)

---

## ✅ Task Overview (High-Level)

### ✅ Task 1 — File Component (Consume → Process → Produce)

* Built a local file processing workflow
* Implemented transformation with a custom processor
* Added error handling + file move strategy

### ✅ Task 2 — FTP/SFTP Integration

* Installed and configured FTP server (vsftpd)
* Configured SFTP access via OpenSSH with a chroot-style setup
* Implemented FTP/SFTP upload + download flows in Camel
* Built multi-destination sync with multicast

### ✅ Task 3 — Enterprise Scenarios + Validation

* Validated end-to-end processing with test files
* Verified:

  * output files created correctly
  * source files moved to processed folders
  * FTP/SFTP destinations updated
* Implemented advanced routing rules (content/size/extension)
* Added monitoring + DLQ behavior with realistic failure test cases

---

## 🧪 Result

* ✅ Local file processing successfully transformed and moved files
* ✅ FTP uploads and downloads succeeded with verified server-side files
* ✅ SFTP uploads and downloads succeeded with verified remote directory listing
* ✅ Sync workflow successfully delivered the same file to FTP + SFTP + local completion folder
* ✅ Advanced routing correctly classified files by:

  * content keywords (`URGENT`, `ARCHIVE`)
  * file size threshold
  * file extension
* ✅ Monitoring route validated files, retried failures, and sent invalid inputs to DLQ

---

## 💡 Why This Matters

File-based integration is still extremely common in enterprises:

* legacy batch workflows (CSV, TXT, XML feeds)
* scheduled exports from ERP/finance systems
* regulated environments where file exchange is mandatory
* multi-system fan-out (file replication across systems)

This lab demonstrates:

* safe file consumption patterns (`move=processed`, `noop=false`)
* resilience (retries, DLQ)
* protocol integration (FTP/SFTP)
* operational readiness (stats + monitoring routes)
* scalable routing strategies (content/size/extension)

---

## 🌍 Real-World Applications

* Nightly batch ingestion pipelines (vendors drop files → processing → archival)
* Automatic routing of “urgent” files to priority queues/destinations
* Secure transfer pipelines using SFTP for compliance-driven integrations
* Multi-destination replication: one file triggers distribution to multiple systems
* Dead-letter queues for invalid inputs with clear operational traceability

---

## ✅ Conclusion

In this lab, I implemented end-to-end file handling with Apache Camel using:

* **File component** for local consume/process/produce workflows
* **FTP/SFTP integration** for bidirectional file transfers and sync automation
* **Content/size/extension-based routing** for enterprise classification and dispatch
* **Resilience patterns** (retries + dead-letter queue)
* **Monitoring** with periodic stats and validation logic

✅ Lab completed successfully in a cloud Linux environment with verified file movements, remote transfers, and routing outcomes.

---
