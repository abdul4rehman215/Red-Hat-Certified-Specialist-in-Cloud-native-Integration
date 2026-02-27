# 🛠️ Troubleshooting Guide — Lab 18: Using Camel Components for File Handling

> This guide captures the most common issues when building file-based integrations using Camel File + FTP/SFTP components, and the exact fixes used during this lab.

---

## ✅ Issue 1: Files are not being processed from the input folder

### ❗ Problem
- Files remain in `input/` (or other watched directories)
- No logs show “Processing file …”
- Output folders remain empty

### ✅ Likely Causes
- Camel app is not running
- Wrong folder path in route URI
- Permissions prevent file access
- Route is not started due to startup failure

### ✅ Fix
1) Confirm the application is running and routes are started (watch logs):
- Look for:
```text
Started file-processor (file://input)
````

2. Check directory permissions:

```bash
ls -la input/
```

3. Ensure readable file permissions (example):

```bash
chmod 644 input/*.txt
```

4. Restart cleanly:

```bash
mvn clean compile
mvn camel:run
```

---

## ✅ Issue 2: Files get reprocessed repeatedly

### ❗ Problem

* Same files processed multiple times

### ✅ Likely Cause

* Using `noop=true` or not moving/deleting files after consumption

### ✅ Fix

Use move strategy (what we used in this lab):

```java
from("file:input?noop=false&move=processed")
```

This ensures the file is moved to `processed/` after success.

---

## ✅ Issue 3: Output files are created but content is not transformed

### ❗ Problem

* File appears in `output/`
* Content is unchanged (no "PROCESSED:" prefix, no uppercase)

### ✅ Likely Cause

* Processor not executed or reading wrong body type

### ✅ Fix

Confirm processor reads and writes string:

```java
String originalContent = exchange.getIn().getBody(String.class);
String processedContent = "PROCESSED: " + originalContent.toUpperCase();
exchange.getIn().setBody(processedContent);
```

Also confirm the route includes:

```java
.process(new FileContentProcessor())
```

---

## ✅ Issue 4: FTP Connection Refused / Upload doesn’t work

### ❗ Problem

* FTP uploads fail
* Errors indicate connection refused or cannot connect to port 21

### ✅ Likely Causes

* vsftpd not installed or not running
* Port blocked by firewall (not always enabled in lab images)
* Wrong endpoint URI or credentials

### ✅ Fix

1. Confirm FTP server running:

```bash
sudo systemctl status vsftpd --no-pager
```

2. Restart service:

```bash
sudo systemctl restart vsftpd
```

3. If firewall is enabled:

```bash
sudo ufw allow 21/tcp
```

4. Verify login manually:

```bash
ftp localhost
```

---

## ✅ Issue 5: FTP login fails (Authentication issues)

### ❗ Problem

* `530 Login incorrect`
* Cannot authenticate with ftpuser

### ✅ Likely Causes

* Password not set correctly
* User disabled / incorrect shell restrictions
* vsftpd config disallowing local users

### ✅ Fix

1. Reset password:

```bash
echo "ftpuser:ftppass123" | sudo chpasswd
```

2. Confirm user exists:

```bash
id ftpuser
```

3. Re-check vsftpd config for:

* local users enabled
* write enabled
* correct root directory behavior

Then restart:

```bash
sudo systemctl restart vsftpd
```

---

## ✅ Issue 6: SFTP Permission Denied / Upload fails

### ❗ Problem

* SFTP upload errors
* Permission denied when writing to `/upload` or `/download`

### ✅ Likely Causes

* Wrong ownership/permissions
* Chroot-style setup requires strict directory ownership rules
* `/home/sftpuser` must be owned by root when chrooting

### ✅ Fix

Set correct ownership pattern:

```bash
sudo chown root:root /home/sftpuser
sudo chmod 755 /home/sftpuser
sudo chown sftpuser:sftpuser /home/sftpuser/upload /home/sftpuser/download
```

Restart SSH:

```bash
sudo systemctl restart ssh
```

Test manually:

```bash
sftp sftpuser@localhost
```

---

## ✅ Issue 7: First SFTP connection asks for host key confirmation

### ❗ Problem

You see:

```text
The authenticity of host 'localhost (127.0.0.1)' can't be established.
Are you sure you want to continue connecting (yes/no/[fingerprint])?
```

### ✅ Cause

SSH host key is not yet trusted on first connect.

### ✅ Fix

Type:

```text
yes
```

This adds the host key to:

```text
~/.ssh/known_hosts
```

---

## ✅ Issue 8: FTP/SFTP downloads do not happen

### ❗ Problem

* File placed in remote `/download` but Camel never downloads it

### ✅ Likely Causes

* Wrong remote path
* Poll interval might require waiting
* Permissions prevent remote listing
* App not running or route not started

### ✅ Fix

1. Confirm route startup:

```text
Started ftp-download-route (ftp://ftpuser@localhost:21/download)
Started sftp-download-route (sftp://sftpuser@localhost:22/download)
```

2. Wait 10–15 seconds (polling interval behavior)

3. Verify remote file exists:

* FTP:

```bash
ftp localhost
# cd download
# ls
```

* SFTP:

```bash
sftp sftpuser@localhost
# cd download
# ls
```

---

## ✅ Issue 9: Advanced content-based routing doesn’t send to FTP `/urgent` or SFTP `/archive`

### ❗ Problem

* Local urgent-output/archive-output files appear
* But remote destinations are missing

### ✅ Likely Causes

* Remote directories do not exist (`/urgent` or `/archive`)
* Permissions prevent writing

### ✅ Fix

Create remote directories (what was done during this lab):

```bash
sudo mkdir -p /home/ftpuser/ftp/urgent
sudo chown -R ftpuser:ftpuser /home/ftpuser/ftp/urgent

sudo mkdir -p /home/sftpuser/archive
sudo chown sftpuser:sftpuser /home/sftpuser/archive
```

---

## ✅ Issue 10: Monitoring route doesn’t move invalid files to DLQ

### ❗ Problem

Invalid files remain in monitored input or disappear unexpectedly.

### ✅ Likely Causes

* Exceptions not handled properly
* No redelivery or DLQ configured
* Validation logic not throwing exceptions

### ✅ Fix

Ensure global error handling exists:

```java
onException(Exception.class)
 .maximumRedeliveries(3)
 .redeliveryDelay(2000)
 .handled(true)
 .to("file:dead-letter-queue");
```

And validation throws errors:

```java
if (content == null || content.trim().isEmpty()) {
  throw new IllegalArgumentException("File is empty: " + filename);
}
if (filename.contains("invalid")) {
  throw new IllegalArgumentException("Invalid filename pattern: " + filename);
}
```

---

## ✅ Quick Verification Checklist

### 1) Verify routes started

Check logs for route start messages like:

* `Started file-processor (file://input)`
* `Started ftp-upload-route ...`
* `Started sftp-download-route ...`

### 2) Verify local outputs

```bash
ls -la output/ processed/
ls -la ftp-processed/ ftp-downloaded/
ls -la sftp-processed/ sftp-downloaded/
```

### 3) Verify FTP server health

```bash
sudo systemctl status vsftpd --no-pager
```

### 4) Verify remote files

FTP:

```bash
ftp localhost
# cd upload
# ls
# quit
```

SFTP:

```bash
sftp sftpuser@localhost
# cd upload
# ls
# quit
```

---
