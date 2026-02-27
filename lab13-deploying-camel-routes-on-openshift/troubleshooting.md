# 🛠️ Lab 13 — Troubleshooting Guide (Camel K on OpenShift)

> This guide lists realistic issues that can occur while installing Camel K, deploying integrations, exposing routes, and scaling workloads on OpenShift.

---

## ✅ Issue 1: `oc whoami` fails / Not logged in

### 🔥 Symptoms
- `oc whoami` returns an error
- Cluster commands fail with authentication-related messages

### 🧠 Cause
- Session expired
- Not logged into the OpenShift cluster
- Incorrect kubeconfig context

### ✅ Fix
Confirm current context (if using kubeconfig):
```bash
oc config current-context
````

Re-login (method depends on lab cluster setup):

```bash
oc login <api-server-url> -u <username> -p <password>
```

Verify again:

```bash
oc whoami
oc cluster-info
```

---

## ✅ Issue 2: Camel K operator subscription created but operator never installs

### 🔥 Symptoms

* `oc get csv` never reaches `Succeeded`
* CSV stays in `Pending` / `Installing`
* Operator pod not created

### 🧠 Causes

* OperatorHub source misconfigured
* Marketplace namespace unavailable
* Insufficient permissions
* Cluster resource constraints

### ✅ Fix Steps

Check CSV status:

```bash
oc get csv
```

Describe the CSV for details:

```bash id="0w4jbm"
oc describe csv -n camel-k-lab
```

Check events:

```bash id="e8p2v0"
oc get events --sort-by='.lastTimestamp'
```

Check marketplace availability:

```bash id="e1w7e2"
oc get pods -n openshift-marketplace
```

---

## ✅ Issue 3: IntegrationPlatform not `Ready`

### 🔥 Symptoms

* `oc get integrationplatform` shows `Error` or stuck
* `kamel run` fails or integrations remain stuck in Building/Deploying

### 🧠 Cause

Camel K platform not initialized or missing cluster dependencies.

### ✅ Fix

Re-run installation:

```bash
kamel install --wait
```

Check platform status:

```bash id="m0b3i4"
oc get integrationplatform
```

Describe platform for details:

```bash id="7c5w8z"
oc describe integrationplatform camel-k
```

---

## ✅ Issue 4: Integration stuck in `Building`

### 🔥 Symptoms

* `kamel get` shows PHASE: `Building`
* Builder pod stuck in Pending/ContainerCreating
* Build pod fails

### 🧠 Causes

* Image build failures
* Registry permissions/issues
* Resource shortage
* Network access to Maven repos blocked
* Incorrect route source file syntax

### ✅ Fix Steps

Watch integration status:

```bash
kamel get <integration-name> -w
```

Check pods:

```bash id="wweo0q"
oc get pods
```

Check builder pod logs:

```bash id="i8u3ae"
oc logs <builder-pod-name>
```

Describe builder pod:

```bash id="ibvl7c"
oc describe pod <builder-pod-name>
```

Check Integration details:

```bash id="r5gf4d"
oc describe integration <integration-name>
```

---

## ✅ Issue 5: Integration runs but logs show no activity

### 🔥 Symptoms

* Pod is Running, but no log output
* Timer routes not producing expected messages

### 🧠 Causes

* Timer period too long
* Route not started due to error
* Logging level or component misconfigured

### ✅ Fix

Follow logs in real time:

```bash
oc logs -f deployment/<integration-name>
```

Check route startup messages:

```bash id="o0j2mz"
oc logs deployment/<integration-name> | head -n 50
```

---

## ✅ Issue 6: REST API route not accessible externally

### 🔥 Symptoms

* curl to route host fails
* 404/503 errors
* route exists but endpoint not reachable

### 🧠 Causes

* Service not created (trait not enabled)
* Route not exposed
* Pod not ready
* Wrong port mapping

### ✅ Fix Steps

Check service exists:

```bash
oc get svc
```

Expose service (if missing route):

```bash id="k0c7yn"
oc expose service rest-api
```

Check route:

```bash id="c1m0c6"
oc get route rest-api
```

Verify pod is running:

```bash id="k4m2ee"
oc get pods -l camel.apache.org/integration=rest-api
```

Test endpoint again:

```bash id="qyoq6r"
ROUTE_URL=$(oc get route rest-api -o jsonpath='{.spec.host}')
curl http://$ROUTE_URL/api/hello
curl http://$ROUTE_URL/api/status
```

---

## ✅ Issue 7: Scaling does not create new replicas

### 🔥 Symptoms

* `kamel scale` reports success, but pods don’t increase
* Replica count remains unchanged

### 🧠 Causes

* Quota limits
* Scheduling constraints
* Cluster capacity shortage
* Deployment not updated

### ✅ Fix Steps

Verify integration status:

```bash
kamel get file-processor
```

Check pods by label:

```bash id="xhhshf"
oc get pods -l camel.apache.org/integration=file-processor
```

Check events:

```bash id="4vpdzy"
oc get events --sort-by='.lastTimestamp'
```

Check resource usage:

```bash id="8opxcu"
oc top pods
```

---

## ✅ Issue 8: `oc top pods` fails

### 🔥 Symptoms

* `oc top pods` returns error like:

  * metrics not available
  * Metrics API not installed

### 🧠 Cause

OpenShift metrics/monitoring might be restricted or not enabled for the lab cluster.

### ✅ Fix

Use alternative observability:

* OpenShift Web Console → Observe
* Pod resource requests/limits:

```bash
oc describe pod <pod-name>
```

---

## ✅ Issue 9: Cleanup fails (namespace stuck in Terminating)

### 🔥 Symptoms

* `oc delete project camel-k-lab` hangs
* Namespace stuck in Terminating

### 🧠 Cause

Finalizers or lingering resources inside the namespace.

### ✅ Fix

First delete integrations (best practice):

```bash
kamel delete --all
```

Then retry project deletion:

```bash
oc delete project camel-k-lab
```

If still stuck, inspect:

```bash id="t5u6wq"
oc get namespace camel-k-lab -o yaml
```

---

## ✅ Practical Notes / Best Practices

* Always verify:

  * `oc get integrationplatform` is **Ready**
* Use `kamel get -w` to watch lifecycle changes
* Use label selectors for multi-replica logs:

  * `oc logs -l camel.apache.org/integration=<name> --tail=20`
* Keep lab environments clean:

  * delete integrations
  * delete namespace/project

---
