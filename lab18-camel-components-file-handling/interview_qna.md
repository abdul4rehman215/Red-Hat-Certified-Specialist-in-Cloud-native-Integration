# 📌 Interview Q&A — Lab 18: Using Camel Components for File Handling

## 1) What is Apache Camel’s File component used for?
The File component is used to **consume files from directories** and/or **produce files to directories**. It’s commonly used for batch integrations, legacy file workflows, and ETL-style pipelines.

---

## 2) What does `noop=false&move=processed` mean in a file endpoint?
- `noop=false` means Camel will **not keep the original file in place** (it will move/delete based on options).
- `move=processed` means the consumed file will be **moved into the `processed/` directory** after successful processing.  
This prevents reprocessing and creates an audit trail.

---

## 3) Why did we implement `onException(Exception.class)` in file workflows?
To ensure failures are handled safely and consistently.  
In this lab, when an exception occurs, the file is routed to an error location (like `error/` or `dead-letter-queue/`) so processing doesn’t silently fail.

---

## 4) How does Camel FTP component support both FTP and SFTP?
The `camel-ftp` component provides endpoints for:
- `ftp://...`
- `sftp://...`  
So one dependency covers both protocols, as long as credentials and ports are configured correctly.

---

## 5) Why is SFTP typically preferred over FTP in enterprise environments?
Because:
- SFTP runs over SSH and encrypts authentication + data
- FTP sends credentials and data unencrypted (unless FTPS is used)
SFTP is safer for compliance and secure integrations.

---

## 6) What did the FTP upload route do in this lab?
It watched a local folder:
- `file:ftp-upload?move=ftp-processed`
and uploaded files to the FTP server:
- `ftp://ftpuser@localhost:21/upload?...`
Then moved local source files into `ftp-processed/`.

---

## 7) How did the FTP download route work?
It polled the remote FTP directory:
- `/download`
and saved remote files locally to:
- `ftp-downloaded/`  
Using `delete=true`, files were removed from the FTP server after download.

---

## 8) How did we verify FTP and SFTP transfers worked?
We validated by:
- checking local folders (`ftp-processed`, `ftp-downloaded`, `sftp-processed`, `sftp-downloaded`)
- logging into servers using `ftp` and `sftp` clients and listing directories (`ls`)

---

## 9) What is multicast and how was it used for file sync?
`multicast()` sends the same message/file to **multiple endpoints**.  
In this lab, one file from `sync-source/` was sent to:
- FTP upload
- SFTP upload
- local folder `sync-completed/`  
This simulates multi-destination replication.

---

## 10) What is content-based routing in file processing?
It means routing a file to different outputs based on its **content**.  
Example in this lab:
- if body contains `URGENT` → urgent output + FTP urgent folder
- if contains `ARCHIVE` → archive output + SFTP archive folder
- otherwise → regular output

---

## 11) How was file size-based routing implemented?
We used the header:
- `CamelFileLength`  
Then routed:
- large files (>1000 bytes) → `large-files/`
- small files → `small-files/`

---

## 12) How was extension-based routing implemented?
We inspected filename extension using `CamelFileNameOnly` and routed:
- `.txt` → `text-files/`
- `.csv` → `csv-files/`
- `.xml` → `xml-files/`
- others → `other-files/`

---

## 13) What is a Dead Letter Queue (DLQ) pattern in Camel?
A DLQ is a safe destination for failed messages/files after retries.  
In this lab, invalid or empty monitored files were redirected to:
- `file:dead-letter-queue`

---

## 14) Why did we implement retries with redelivery?
To handle temporary failures automatically.  
We used:
- `maximumRedeliveries(3)`
- `redeliveryDelay(2000)`  
This is a resilience best practice for real-world integrations.

---

## 15) What is the key real-world takeaway from this lab?
Enterprise integrations often rely on file transfers and batch workflows.  
This lab demonstrates how to build:
- reliable file pipelines (move/processed/error)
- secure transfers (SFTP)
- routing logic (content/size/extension)
- operational resilience (retries + DLQ + monitoring)
All of which are critical for scalable cloud-native integration solutions.
