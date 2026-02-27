#!/bin/bash
# Lab 20 - End-to-End Integration with Camel Routes
# Commands Executed During Lab (sequential, no explanations)

mkdir -p ~/camel-integration-lab
cd ~/camel-integration-lab

mkdir -p src/main/java/com/alnafi/integration
mkdir -p src/main/resources
mkdir -p src/test/java
mkdir -p data/input
mkdir -p data/output
mkdir -p data/processed
mkdir -p data/error

nano pom.xml

sudo systemctl start postgresql
sudo systemctl enable postgresql

sudo -u postgres psql << EOF
CREATE DATABASE integration_lab;
CREATE USER camel_user WITH PASSWORD 'camel_pass';
GRANT ALL PRIVILEGES ON DATABASE integration_lab TO camel_user;
\q
EOF

PGPASSWORD=camel_pass psql -h localhost -U camel_user -d integration_lab << EOF
-- Customers table
CREATE TABLE customers (
 customer_id SERIAL PRIMARY KEY,
 customer_code VARCHAR(20) UNIQUE NOT NULL,
 customer_name VARCHAR(100) NOT NULL,
 email VARCHAR(100),
 phone VARCHAR(20),
 address TEXT,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
 product_id SERIAL PRIMARY KEY,
 product_code VARCHAR(20) UNIQUE NOT NULL,
 product_name VARCHAR(100) NOT NULL,
 unit_price DECIMAL(10,2) NOT NULL,
 category VARCHAR(50),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
 order_id SERIAL PRIMARY KEY,
 order_number VARCHAR(50) UNIQUE NOT NULL,
 customer_id INTEGER REFERENCES customers(customer_id),
 order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 total_amount DECIMAL(12,2),
 status VARCHAR(20) DEFAULT 'PENDING',
 processed_at TIMESTAMP
);

-- Order items table
CREATE TABLE order_items (
 item_id SERIAL PRIMARY KEY,
 order_id INTEGER REFERENCES orders(order_id),
 product_id INTEGER REFERENCES products(product_id),
 quantity INTEGER NOT NULL,
 unit_price DECIMAL(10,2) NOT NULL,
 line_total DECIMAL(12,2) NOT NULL
);

-- Insert sample data
INSERT INTO customers (customer_code, customer_name, email, phone, address) VALUES
('CUST001', 'Tech Solutions Inc', 'orders@techsolutions.com', '555-0101', '123 Tech Street, Silicon Valley, CA'),
('CUST002', 'Global Retail Corp', 'purchasing@globalretail.com', '555-0102', '456 Commerce Ave, New York, NY'),
('CUST003', 'Manufacturing Plus', 'orders@mfgplus.com', '555-0103', '789 Industrial Blvd, Detroit, MI');

INSERT INTO products (product_code, product_name, unit_price, category) VALUES
('PROD001', 'Wireless Mouse', 29.99, 'Electronics'),
('PROD002', 'USB Keyboard', 49.99, 'Electronics'),
('PROD003', 'Monitor Stand', 79.99, 'Accessories'),
('PROD004', 'Webcam HD', 89.99, 'Electronics'),
('PROD005', 'Desk Lamp', 39.99, 'Office Supplies');

\q
EOF

nano src/main/resources/database.properties

mkdir -p src/main/java/com/alnafi/integration/model
mkdir -p src/main/java/com/alnafi/integration/processor
mkdir -p src/main/java/com/alnafi/integration/service
mkdir -p src/main/java/com/alnafi/integration/route

nano src/main/java/com/alnafi/integration/model/Order.java
nano src/main/java/com/alnafi/integration/model/OrderItem.java

nano src/main/java/com/alnafi/integration/processor/CsvOrderProcessor.java
nano src/main/java/com/alnafi/integration/processor/CustomerEnrichmentProcessor.java

nano src/main/java/com/alnafi/integration/service/InventoryServiceSimulator.java

nano src/main/java/com/alnafi/integration/route/OrderProcessingRoute.java

nano src/main/java/com/alnafi/integration/IntegrationApplication.java
nano src/main/java/com/alnafi/integration/route/NotificationConsumerRoute.java

mvn clean compile

mvn exec:java

cd ~/camel-integration-lab
nano data/input/order_1001.csv
sleep 8

ls -la data/output/
cat data/output/order_1001_confirmation.txt
ls -la data/processed/

nano data/input/order_bad.csv
sleep 8
ls -la data/error/

^C
