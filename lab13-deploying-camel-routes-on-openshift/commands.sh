#!/bin/bash
# Lab 13 - Deploying Camel Routes on OpenShift (Camel K)
# Commands Executed During Lab (Sequential, Copy-Paste Ready)

# ------------------------------------------------------------
# Task 1.1: Verify OpenShift Cluster Access
# ------------------------------------------------------------

oc whoami
oc cluster-info
oc projects

# Create a new project for the lab
oc new-project camel-k-lab

# Switch to the project
oc project camel-k-lab

# ------------------------------------------------------------
# Task 1.2: Install Camel K Operator
# ------------------------------------------------------------

# Verify Camel K operator is available in OperatorHub
oc get packagemanifests | grep camel

# Create subscription manifest (edited with nano)
nano camel-k-subscription.yaml

# Apply the subscription
oc apply -f camel-k-subscription.yaml

# Watch ClusterServiceVersion (CSV) installation status
oc get csv -w
# (Ctrl+C after PHASE shows Succeeded)

# Verify operator pod is running
oc get pods -n camel-k-lab

# ------------------------------------------------------------
# Task 1.3: Install Camel K CLI
# ------------------------------------------------------------

# Download latest Camel K client tarball
curl -L https://github.com/apache/camel-k/releases/latest/download/camel-k-client-linux-64bit.tar.gz -o kamel.tar.gz

# Extract and install CLI
tar -xzf kamel.tar.gz
sudo mv kamel /usr/local/bin/
chmod +x /usr/local/bin/kamel

# Verify CLI version
kamel version

# ------------------------------------------------------------
# Task 1.4: Initialize Camel K Integration Platform
# ------------------------------------------------------------

kamel install --wait
kamel get
oc get integrationplatform

# ------------------------------------------------------------
# Task 2.1: Deploy Simple Timer/Log Route (FileProcessor)
# ------------------------------------------------------------

nano FileProcessor.java
kamel run FileProcessor.java --name file-processor

# Monitor build/deploy status
kamel get
oc get pods -w
# (Ctrl+C after pod shows Running)

# ------------------------------------------------------------
# Task 2.2: Deploy REST API Route
# ------------------------------------------------------------

nano RestApiRoute.java

# Deploy REST integration and enable service trait
kamel run RestApiRoute.java --name rest-api --trait service.enabled=true

# Watch integration until Running
kamel get rest-api -w
# (Ctrl+C after Running)

# ------------------------------------------------------------
# Task 2.3: Deploy Message Transformation Route
# ------------------------------------------------------------

nano MessageTransformer.java
kamel run MessageTransformer.java --name message-transformer

# Verify all integrations
kamel get

# ------------------------------------------------------------
# Task 3.1: Verify Deployments + Logs
# ------------------------------------------------------------

oc get pods
oc get integrations
oc describe integration file-processor

# Follow logs of file processor (deployment)
oc logs -f deployment/file-processor
# (Ctrl+C to stop)

# Get OpenShift web console URL
oc whoami --show-console

# ------------------------------------------------------------
# Task 3.3: Expose and Test REST API Route
# ------------------------------------------------------------

oc expose service rest-api
oc get route rest-api

ROUTE_URL=$(oc get route rest-api -o jsonpath='{.spec.host}')

curl http://$ROUTE_URL/api/hello
curl http://$ROUTE_URL/api/status

# ------------------------------------------------------------
# Task 3.4: Monitor Resource Usage + Events
# ------------------------------------------------------------

oc top pods

# Pod details by label selector
oc describe pod -l camel.apache.org/integration=file-processor

# Events sorted by timestamp
oc get events --sort-by='.lastTimestamp'

# ------------------------------------------------------------
# Task 3.5: Scale Integration
# ------------------------------------------------------------

kamel scale file-processor --replicas=2

oc get pods -l camel.apache.org/integration=file-processor
kamel get file-processor

# ------------------------------------------------------------
# Task 3.6: Logs Across Replicas + Operator Logs
# ------------------------------------------------------------

oc logs -l camel.apache.org/integration=file-processor --tail=20

oc logs -f -l camel.apache.org/integration=message-transformer
# (Ctrl+C to stop)

oc logs -l name=camel-k-operator

# ------------------------------------------------------------
# Lab Cleanup
# ------------------------------------------------------------

kamel delete --all
oc delete project camel-k-lab
