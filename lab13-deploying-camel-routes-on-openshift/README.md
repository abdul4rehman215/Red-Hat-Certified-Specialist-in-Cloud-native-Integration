# 🧪 Lab 13: Deploying Camel Routes on OpenShift (Camel K)

> **Track:** Red Hat Certified Specialist in Cloud-Native Integration (Exam)  
> **Lab Focus:** Install Camel K on OpenShift, deploy integrations (timer, REST API, transformation), monitor/scale, troubleshoot, and cleanup.  
> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** toor  
> **Status:** ✅ Completed

---

## 🧱 Repository Structure (Lab Format)

```text
lab13-deploying-camel-routes-on-openshift/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── manifests/
│   └── camel-k-subscription.yaml
└── routes/
    ├── FileProcessor.java
    ├── RestApiRoute.java
    └── MessageTransformer.java
```

> ✅ Notes:
>
> * `manifests/` contains Kubernetes/OpenShift YAML used in the lab.
> * `routes/` contains Camel K Java route sources deployed via `kamel run`.
> * Any cluster URLs/hosts shown in outputs are kept realistic, but public-facing repos should avoid leaking internal cluster names (use placeholders if needed).

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Understand Apache **Camel K** and how it integrates with OpenShift
* Install and configure the **Camel K Operator** on OpenShift using OperatorHub (Subscription + CSV)
* Install the **Camel K CLI** (`kamel`) and initialize the Integration Platform
* Create and deploy simple Camel routes as Kubernetes-native integrations:

  * Timer-based log route
  * REST API route (platform-http)
  * Message transformation route (JSON marshal/unmarshal)
* Monitor and verify deployments using OpenShift + Camel K tools:

  * `kamel get`, `oc get pods`, `oc logs`, `oc describe`
* Expose and test REST endpoints using OpenShift **Route** + `curl`
* Scale an integration horizontally using `kamel scale`
* Troubleshoot common deployment and build issues (Building, Deploying, Error states)
* Clean up resources safely at the end of the lab

---

## ✅ Prerequisites

Recommended knowledge before performing this lab:

* Apache Camel routing basics + Enterprise Integration Patterns (EIP)
* OpenShift/Kubernetes fundamentals (projects/namespaces, pods, services, routes)
* YAML basics (operator subscriptions)
* CLI usage (oc, curl)
* Containerization concepts

---

## 🧰 Tools Used

| Tool                  | Purpose                                                              |
| --------------------- | -------------------------------------------------------------------- |
| `oc` (OpenShift CLI)  | Cluster access, project management, logs, metrics                    |
| `kamel` (Camel K CLI) | Install Camel K, run routes, scale integrations, manage integrations |
| `nano`                | Editing YAML and Java route files                                    |
| `curl`                | Testing OpenShift route endpoints                                    |
| OpenShift Web Console | Topology view + logs + events                                        |

---

## 🌥️ Lab Environment

This lab was performed in a **cloud-based training environment** with OpenShift cluster access.

* OpenShift cluster: pre-configured + admin access
* Networking/storage: pre-configured
* OS: Ubuntu 24.04.1 LTS

> 🔒 **Security Note:** Internal cluster endpoints and hostnames should be treated carefully in public repos. If needed, replace them with placeholders like `https://api.<cluster>:6443`.

---

## 🧩 What I Deployed (Integrations)

### 1) `file-processor` (Timer → Log)

* A simple integration that logs a message every 10 seconds.
* Used Camel timer endpoint and log output.

### 2) `rest-api` (platform-http REST service)

* A REST API exposed via OpenShift Route:

  * `GET /api/hello`
  * `GET /api/status`
* Service exposure enabled using Camel K trait:

  * `--trait service.enabled=true`

### 3) `message-transformer` (JSON transform)

* Generates JSON payload, unmarshals it, enriches it, then marshals back to JSON.
* Logs both original and transformed messages.

---

## 🛠️ Tasks Overview (High-Level)

### ✅ Task 1: Set up Camel K on OpenShift

* Verified cluster access (`oc whoami`, `oc cluster-info`)
* Created a dedicated project/namespace: `camel-k-lab`
* Installed Camel K operator using subscription YAML
* Verified operator install with CSV phase transitions → `Succeeded`
* Installed Camel K CLI (`kamel`) and initialized the Integration Platform (`kamel install --wait`)
* Verified `integrationplatform` status is **Ready**

### ✅ Task 2: Deploy Camel Routes (as integrations)

* Created and deployed:

  * Timer-based route (`FileProcessor.java`)
  * REST API route (`RestApiRoute.java`)
  * JSON transformation route (`MessageTransformer.java`)
* Monitored build → deploy → running transitions using:

  * `kamel get`
  * `oc get pods -w`

### ✅ Task 3: Verify, Monitor, Scale, and Test

* Verified pods, integrations, and status
* Followed logs and validated runtime behavior
* Exposed REST API using OpenShift `Route` and tested with `curl`
* Checked resource usage via `oc top pods`
* Scaled integration replicas using `kamel scale`
* Reviewed multi-replica logs using label selectors
* Verified operator logs show expected integration transitions

### 🧹 Cleanup

* Deleted all integrations: `kamel delete --all`
* Deleted namespace/project: `oc delete project camel-k-lab`

---

## ✅ Verification Checklist

* ✅ Camel K Operator installed and running in project
* ✅ IntegrationPlatform phase shows **Ready**
* ✅ Integrations show **Running** with replicas available
* ✅ Pods in project are in **Running** state
* ✅ Logs show timer messages and transformed JSON output
* ✅ REST API accessible via OpenShift route:

  * `/api/hello` returns JSON
  * `/api/status` returns health JSON
* ✅ Scaling creates additional replica pods
* ✅ `oc top pods` displays expected CPU/memory usage

---

## 📈 Result

This lab successfully demonstrated deploying Camel routes as cloud-native integrations on OpenShift:

* Camel K installed, platform ready
* 3 integrations deployed and running
* REST route exposed and validated via OpenShift Route
* Integrations monitored via CLI + console
* Scaling completed successfully
* Clean cleanup performed

---

## 💡 What I Learned

* Installing OpenShift operators using Subscription + CSV monitoring
* How Camel K turns a single route file into a running integration on Kubernetes
* The Camel K lifecycle: **Building → Deploying → Running**
* How to expose REST endpoints using `platform-http` + OpenShift routes
* How to monitor integrations using:

  * `oc logs`, `oc describe`, `oc get events`, `oc top`
* How to scale integrations using `kamel scale`
* How to troubleshoot build/runtime failures using operator logs and integration status
* Why cleanup is important to avoid resource leakage in shared clusters

---

## 🌍 Why This Matters (Real-World Relevance)

Camel K on OpenShift enables teams to build and deploy integrations in a Kubernetes-native way:

* **Enterprise Integration:** lightweight, scalable integrations
* **Microservices:** focused services exposing APIs and transformation logic
* **DevOps/Platform Engineering:** GitOps-friendly deployments using CLI/YAML
* **Serverless-style integration:** “just run the route file” and let the platform handle runtime packaging
* **Cloud-native observability:** logs/events/metrics available through OpenShift tooling

---

## ✅ Conclusion

Lab 13 provided hands-on practice for deploying Camel routes on OpenShift using Camel K:

* Installed and configured Camel K operator + CLI
* Created multiple integration types (timer, REST, transformation)
* Verified behavior using logs and API tests
* Scaled and monitored integrations using OpenShift-native tools
* Performed proper cleanup

---

