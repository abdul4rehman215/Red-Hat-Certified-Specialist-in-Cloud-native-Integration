# 🛠️ Troubleshooting — Lab 20: End-to-End Integration with Camel Routes

> This guide covers the most common failures when running an end-to-end Camel pipeline that uses:
> **File intake + CSV parsing + PostgreSQL enrichment + REST calls + JMS messaging + output reports**.

---

## ✅ Issue 1: PostgreSQL service not running

### ❗ Symptoms
- DB connection errors in Camel logs
- `psql` commands fail

### ✅ Fix
```bash id="g4p2t6"
sudo systemctl start postgresql
sudo systemctl status postgresql --no-pager
````

Enable at boot:

```bash id="g7q1d3"
sudo systemctl enable postgresql
```

---

## ✅ Issue 2: `psql` prompts for password / script hangs

### ❗ Symptoms

* `psql` asks for password interactively
* your schema import stops mid-way

### ✅ Fix (non-interactive)

```bash id="v6k1m9"
PGPASSWORD=camel_pass psql -h localhost -U camel_user -d integration_lab
```

For scripts:

```bash id="s2h7k5"
PGPASSWORD=camel_pass psql -h localhost -U camel_user -d integration_lab << EOF
-- SQL HERE
EOF
```

---

## ✅ Issue 3: Camel cannot find `database.properties`

### ❗ Symptoms

* Error: `database.properties not found in classpath`

### ✅ Fix

Confirm file exists:

```bash id="c7z5v2"
ls -la src/main/resources/database.properties
```

Confirm it is under:

```text id="m4r2d8"
src/main/resources/
```

Rebuild:

```bash id="p3n8x1"
mvn clean compile
```

---

## ✅ Issue 4: Database authentication fails

### ❗ Symptoms

* “password authentication failed for user camel_user”
* “permission denied” on tables

### ✅ Fix

Re-check DB + user creation:

```bash id="w8p3j6"
sudo -u postgres psql
```

Inside psql:

```sql id="q2m7v4"
\du
\l
```

Grant privileges again if needed:

```sql id="z1x6b3"
GRANT ALL PRIVILEGES ON DATABASE integration_lab TO camel_user;
```

---

## ✅ Issue 5: CSV file not processed / stays in `data/input`

### ❗ Symptoms

* File remains in `data/input/`
* No output report created

### ✅ Causes

* App not running
* Wrong folder path
* CSV format mismatch
* polling delay not waited

### ✅ Fix

1. Confirm app running:

```bash id="r5k2p1"
ps aux | grep IntegrationApplication
```

2. Confirm route watches correct folder:

```text id="a2d9n7"
file:data/input
```

3. Wait a few seconds after dropping file:

```bash id="d9k1x3"
sleep 8
```

4. Validate the CSV header EXACTLY:

```csv id="h1r8p2"
order_number,customer_code,product_code,quantity,unit_price
```

---

## ✅ Issue 6: CSV parsing errors / missing columns

### ❗ Symptoms

* `NullPointerException`
* parsing errors in logs
* processor fails because map keys are missing

### ✅ Fix

Make sure CSV includes:

* header row
* correct column names
* values for each row

Example:

```csv id="q6p2m8"
order_number,customer_code,product_code,quantity,unit_price
1001,CUST001,PROD001,2,29.99
```

---

## ✅ Issue 7: Customer not found (DB enrichment fails)

### ❗ Symptoms

* `Customer not found: CUSTxxx`
* file moves to `data/error`

### ✅ Fix

Check DB:

```bash id="k7m2v1"
PGPASSWORD=camel_pass psql -h localhost -U camel_user -d integration_lab -c "SELECT customer_code, customer_name FROM customers;"
```

Fix the CSV customer code OR insert new customer:

```sql id="t1v8c4"
INSERT INTO customers (customer_code, customer_name) VALUES ('CUST999', 'New Customer');
```

---

## ✅ Issue 8: Product not found (enrichment fails)

### ❗ Symptoms

* `Product not found: PROD999`
* retries happen, then file goes to `data/error`

### ✅ Fix

Check products:

```bash id="p4x2n7"
PGPASSWORD=camel_pass psql -h localhost -U camel_user -d integration_lab -c "SELECT product_code, product_name FROM products;"
```

Fix CSV product code OR insert product:

```sql id="b2k6m9"
INSERT INTO products (product_code, product_name, unit_price, category)
VALUES ('PROD999', 'New Item', 10.00, 'Test');
```

---

## ✅ Issue 9: Inventory API not reachable / port 8080 already in use

### ❗ Symptoms

* HTTP connection refused
* inventory calls failing
* startup error “Address already in use”

### ✅ Fix

Check who uses port 8080:

```bash id="v8p3c2"
sudo lsof -i :8080
```

Stop conflicting process OR change simulator port.

---

## ✅ Issue 10: Inventory validation fails even for real products

### ❗ Symptoms

* order fails at inventory step
* logs show “Product not found” from REST

### ✅ Fix

Verify the inventory endpoint manually:

```bash id="m1q7d4"
curl -s "http://localhost:8080/inventory/check?productCode=PROD001"
```

Expected:

```json id="z4n1p8"
{"productCode":"PROD001","availableQuantity":100,"status":"AVAILABLE"}
```

---

## ✅ Issue 11: JMS notifications not created / MQ files missing

### ❗ Symptoms

* `mq_standard_*.txt` not generated
* consumer routes not receiving anything

### ✅ Fix

1. Ensure embedded broker starts (logs):

* broker started
* connector `tcp://localhost:61616`

2. Ensure consumer routes are loaded:

* `priority-notification-consumer`
* `standard-notification-consumer`

3. Confirm output folder:

```bash id="r7c2m8"
ls -la data/output/
```

---

## ✅ Issue 12: Duplicate DB insert errors (re-running same order)

### ❗ Symptoms

* Order number unique constraint failure:

  * `orders.order_number` must be unique

### ✅ Fix

Use a new order number each run (e.g., 1002, 1003)
OR delete the old record:

```sql id="x2k7p5"
DELETE FROM orders WHERE order_number='1001';
```

(If foreign keys exist, remove order_items first.)

---

## ✅ Quick Health Checklist

### ✅ App running?

```bash id="k1m4v7"
ps aux | grep IntegrationApplication
```

### ✅ Inventory API responds?

```bash id="m9x1c3"
curl -s "http://localhost:8080/inventory/check?productCode=PROD001"
```

### ✅ DB reachable?

```bash id="p7v2n1"
PGPASSWORD=camel_pass psql -h localhost -U camel_user -d integration_lab -c "SELECT 1;"
```

### ✅ Output created?

```bash id="f1p8m2"
ls -la data/output/
```

### ✅ Failed orders collected?

```bash id="d8k2q9"
ls -la data/error/
```

---

✅ **Lab 20 troubleshooting documented successfully.**
