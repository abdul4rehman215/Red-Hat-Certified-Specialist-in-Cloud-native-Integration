# 🛠️ Lab 14 — Troubleshooting Guide (Scalable Camel K + HPA on OpenShift)

> This guide captures realistic problems that can occur while deploying a scalable Camel K service, exposing it via OpenShift Route, configuring HPA, and validating scale behavior under load.

---

## ✅ Issue 1: `oc login` prompts repeatedly / login fails

### 🔥 Symptoms
- `oc login` keeps prompting for credentials
- Commands return unauthorized / forbidden

### 🧠 Causes
- Wrong API server URL
- Expired session/token
- Insufficient permissions for the user

### ✅ Fix
Verify the server URL is correct and login again:
```bash
oc login --server=https://api.your-cluster.com:6443 --username=admin
````

Confirm:

```bash
oc whoami
oc project
```

---

## ✅ Issue 2: Camel K platform not Ready

### 🔥 Symptoms

* `kamel install --wait` hangs
* `kamel get platform` shows non-Ready state

### 🧠 Causes

* IntegrationPlatform resources not created correctly
* Operator issues or namespace issues
* Cluster is resource constrained

### ✅ Fix

Re-run install:

```bash
kamel install --wait
```

Check platform:

```bash id="1w2qf5"
kamel get platform
```

Check IntegrationPlatform directly:

```bash id="x8m5zt"
oc get integrationplatform
oc describe integrationplatform camel-k
```

Check operator health:

```bash id="xx5v7s"
oc get pods -n openshift-operators | grep camel
```

---

## ✅ Issue 3: Integration stuck in `Building` phase

### 🔥 Symptoms

* `kamel get integrations` shows Building for a long time
* Builder pod is Pending/ContainerCreating
* Build fails due to dependencies or registry

### 🧠 Causes

* Maven dependencies download blocked
* Image registry permissions issues
* Incorrect Java route syntax
* Cluster capacity issues

### ✅ Fix

Watch integration status:

```bash
kamel get integrations -w
```

Check pods:

```bash id="qg3x9p"
oc get pods
```

Find builder pod and view logs:

```bash id="r4w9x4"
oc logs <builder-pod-name>
```

Describe builder pod:

```bash id="rlz2q0"
oc describe pod <builder-pod-name>
```

Describe integration:

```bash id="e3l9bq"
oc describe integration scalable-camel-service
```

---

## ✅ Issue 4: Service exists but Route not reachable (503/404)

### 🔥 Symptoms

* `curl http://$ROUTE_URL/...` returns 503
* Route exists but no response
* 404 due to wrong path

### 🧠 Causes

* Pod not ready
* Wrong service port mapping
* Route not created properly
* Endpoint path mismatch

### ✅ Fix Checklist

Check route:

```bash
oc get route camel-api-route
```

Confirm service:

```bash id="w7f9sx"
oc get svc scalable-camel-service
```

Confirm pods:

```bash id="cv6v2u"
oc get pods -l camel.apache.org/integration=scalable-camel-service
```

Test inside cluster (optional):

```bash id="o7c7xv"
oc rsh deployment/scalable-camel-service curl -s http://localhost:8080/health
```

Verify endpoints:

* `/health`
* `/api/v1/process/<id>`
* `/api/v1/data`
* `/metrics`

---

## ✅ Issue 5: HPA created but does not scale

### 🔥 Symptoms

* HPA exists but replicas stay constant under load
* `TARGETS` show `<unknown>` or no metrics
* No rescale events appear

### 🧠 Causes

* Metrics server not working
* Missing CPU/memory requests in pods
* Wrong scale target (wrong API version/kind/name)
* Load not high enough to exceed targets

### ✅ Fix Steps

Check metrics server:

```bash
oc get pods -n openshift-monitoring | grep metrics-server
```

Confirm metrics are available:

```bash id="mk5g0j"
oc top pods -l camel.apache.org/integration=scalable-camel-service
```

Describe HPA conditions:

```bash id="8v3k2p"
oc describe hpa camel-service-hpa
```

Verify requests/limits are applied to pods:

```bash id="d1m9mc"
oc describe pod -l camel.apache.org/integration=scalable-camel-service | grep -A 20 -E "Requests|Limits"
```

---

## ✅ Issue 6: HPA scales too slowly (delayed reaction)

### 🔥 Symptoms

* Load increases but replicas take time to increase
* Replica count changes slowly even during spikes

### 🧠 Causes

* Stabilization window prevents rapid scaling
* Conservative scaling policies
* Cluster scheduling delays

### ✅ Fix (Tune behavior)

Reduce scaleUp stabilization window:

```bash
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
```

---

## ✅ Issue 7: Scale-down doesn’t happen quickly

### 🔥 Symptoms

* After load ends, pods stay high for a long time

### 🧠 Cause

This is expected when:

* `scaleDown.stabilizationWindowSeconds` is large (e.g., 300s)
  This prevents rapid scale-down thrashing.

### ✅ Fix (If Needed)

Lower scaleDown stabilization window (carefully):

```bash id="mbxkz7"
oc patch hpa camel-service-hpa --type='merge' -p='
{
  "spec": {
    "behavior": {
      "scaleDown": {
        "stabilizationWindowSeconds": 120
      }
    }
  }
}'
```

---

## ✅ Issue 8: Load scripts fail or produce errors

### 🔥 Symptoms

* `load-test.sh` exits early
* curl errors like could not resolve host
* scripts run but produce no scaling

### 🧠 Causes

* Route URL not set properly
* Route doesn’t exist / wrong namespace
* DNS issues from the lab VM
* Too low concurrency

### ✅ Fix

Re-fetch route host:

```bash
ROUTE_URL=$(oc get route camel-api-route -o jsonpath='{.spec.host}')
echo "http://$ROUTE_URL"
```

Increase load:

```bash id="4b6yjt"
./load-test.sh 300 25
```

Verify scale events:

```bash id="r33e1m"
oc get events --sort-by=.metadata.creationTimestamp | grep -i scale
```

---

## ✅ Issue 9: PDB prevents evictions during maintenance

### 🔥 Symptoms

* Drain/eviction attempts fail
* Disruptions blocked

### 🧠 Cause

PDB enforces `minAvailable` and can block eviction if replicas are too low.

### ✅ Fix

Ensure enough replicas exist (scale up) or adjust PDB carefully.

---

## ✅ Operational Monitoring Tips

Real-time monitoring commands used in this lab:

```bash
watch 'oc get pods -l camel.apache.org/integration=scalable-camel-service'
watch 'oc get hpa camel-service-hpa'
watch 'oc top pods -l camel.apache.org/integration=scalable-camel-service'
```

Log checks:

```bash
oc logs -l camel.apache.org/integration=scalable-camel-service --tail=100
oc logs -l camel.apache.org/integration=scalable-camel-service | grep -i error
```

---

## ✅ Next Hardening Steps (Production Ideas)

* Implement Prometheus-format metrics instead of JSON on `/metrics`
* Add authentication (JWT/OAuth2) for `/api/*`
* Add structured logs + distributed tracing (OpenTelemetry)
* Add circuit breakers/retries for downstream dependencies
* Define SLOs, alerts, and dashboards (latency/error rate/saturation)
* Tune HPA thresholds based on measured load test results


