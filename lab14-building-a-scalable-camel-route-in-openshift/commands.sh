#!/bin/bash
# Lab 14 - Building a Scalable Camel Route in OpenShift (Camel K + HPA)
# Commands Executed During Lab (Sequential, Copy-Paste Ready)

# ------------------------------------------------------------
# Task 1.1: Set Up the Project Environment
# ------------------------------------------------------------

# Login to OpenShift cluster (prompts for password)
oc login --server=https://api.your-cluster.com:6443 --username=admin

# Create a new project
oc new-project camel-scaling-lab

# Switch to the project
oc project camel-scaling-lab

# Verify Camel K operator is installed
oc get csv -n openshift-operators | grep camel

# Initialize Camel K Integration Platform
kamel install --wait

# Verify platform is ready
kamel get platform

# ------------------------------------------------------------
# Task 1.2: Create the Scalable Camel Route
# ------------------------------------------------------------

# Create integration source file
nano scalable-route.java

# Deploy integration with resource requests/limits and service enabled
kamel run scalable-route.java \
  --resource-limit cpu=500m \
  --resource-limit memory=512Mi \
  --resource-request cpu=100m \
  --resource-request memory=128Mi \
  --trait container.port=8080 \
  --trait service.enabled=true \
  --trait service.port=8080 \
  --name scalable-camel-service

# Watch integration readiness
kamel get integrations -w
# (Ctrl+C after Running)

# Verify integration, pod, and service
oc get integrations
oc get pods -l camel.apache.org/integration=scalable-camel-service
oc get svc scalable-camel-service

# ------------------------------------------------------------
# Task 1.3: Expose Service with OpenShift Route and Test Endpoints
# ------------------------------------------------------------

# Expose service
oc expose service scalable-camel-service --name=camel-api-route

# Get route host and print service URL
ROUTE_URL=$(oc get route camel-api-route -o jsonpath='{.spec.host}')
echo "Service URL: http://$ROUTE_URL"

# Test health endpoint
curl -X GET http://$ROUTE_URL/health

# Test GET endpoint
curl -X GET http://$ROUTE_URL/api/v1/process/12345

# Test POST endpoint
curl -X POST http://$ROUTE_URL/api/v1/data \
  -H "Content-Type: application/json" \
  -d '{"test": "data", "value": 123}'

# Test metrics endpoint
curl -X GET http://$ROUTE_URL/metrics

# ------------------------------------------------------------
# Task 2.1: Configure Resource Monitoring (ServiceMonitor + Metrics Server)
# ------------------------------------------------------------

nano service-monitor.yaml
oc apply -f service-monitor.yaml

# Verify metrics server pods
oc get pods -n openshift-monitoring | grep metrics-server

# Confirm metrics output
oc top pods

# ------------------------------------------------------------
# Task 2.2: Create Horizontal Pod Autoscaler (HPA)
# ------------------------------------------------------------

nano hpa-config.yaml
oc apply -f hpa-config.yaml

# Verify HPA status
oc get hpa camel-service-hpa

# Monitor resource usage for pods in the integration
oc top pods -l camel.apache.org/integration=scalable-camel-service

# Get detailed HPA information
oc describe hpa camel-service-hpa

# ------------------------------------------------------------
# Task 2.3: Configure Pod Disruption Budget (PDB)
# ------------------------------------------------------------

nano pdb-config.yaml
oc apply -f pdb-config.yaml
oc get pdb camel-service-pdb

# ------------------------------------------------------------
# Task 3.1: Monitoring Dashboard + Load Testing
# ------------------------------------------------------------

# Grafana dashboard ConfigMap
nano grafana-dashboard.yaml
oc apply -f grafana-dashboard.yaml

# Load testing script
nano load-test.sh
chmod +x load-test.sh

# ------------------------------------------------------------
# Task 3.2: Execute Load Testing and Monitor Scaling
# ------------------------------------------------------------

# Monitor in separate terminals (examples):
# watch 'oc get pods -l camel.apache.org/integration=scalable-camel-service'
# watch 'oc get hpa camel-service-hpa'
# watch 'oc top pods -l camel.apache.org/integration=scalable-camel-service'

# Run load test (5 minutes, 15 concurrent workers)
./load-test.sh 300 15

# Check scale events
oc get events --sort-by=.metadata.creationTimestamp | grep -i scale

# Verify pods after scaling
oc get pods -l camel.apache.org/integration=scalable-camel-service

# ------------------------------------------------------------
# Task 3.3: Collect Metrics During Testing
# ------------------------------------------------------------

nano collect-metrics.sh
chmod +x collect-metrics.sh

# Run metrics collection in background and store PID
./collect-metrics.sh &
METRICS_PID=$!

# Different load patterns
echo "Test 1: Gradual load increase"
./load-test.sh 180 5
sleep 60
./load-test.sh 180 10
sleep 60
./load-test.sh 180 20

echo "Test 2: Spike load test"
./load-test.sh 120 25

echo "Test 3: Sustained high load"
./load-test.sh 600 15

# Verify scale-down behavior (watch command)
echo "Monitoring scale-down behavior..."
watch 'echo "Pods: $(oc get pods -l camel.apache.org/integration=scalable-camel-service --no-headers | wc -l)"; oc get hpa camel-service-hpa'
# (Ctrl+C after observing scale-down)

# Stop metrics collection
kill $METRICS_PID

# ------------------------------------------------------------
# Task 3.4: Response Time Testing + Log Review
# ------------------------------------------------------------

nano response-time-test.sh
chmod +x response-time-test.sh

# Run response time tests
./response-time-test.sh 50

# View logs from all pods (tail)
oc logs -l camel.apache.org/integration=scalable-camel-service --tail=10

# Check for errors
oc logs -l camel.apache.org/integration=scalable-camel-service | grep -i error

# Follow logs live (Ctrl+C to stop)
oc logs -f -l camel.apache.org/integration=scalable-camel-service

# ------------------------------------------------------------
# Troubleshooting: HPA Behavior Tuning (Patch)
# ------------------------------------------------------------

oc patch hpa camel-service-hpa --type='merge' -p='
{
  "spec": {
    "behavior": {
      "scaleUp": {
        "stabilizationWindowSeconds": 30
      }
    }
  }
}'
