# 🧪 Lab 14: Building a Scalable Camel Route in OpenShift (Camel K + HPA)

> **Track:** Red Hat Certified Specialist in Cloud-Native Integration (Exam)  
> **Lab Focus:** Build a scalable Camel route on OpenShift, configure autoscaling with HPA, add monitoring/observability, and validate scaling under load.  
> **Environment:** Ubuntu 24.04.1 LTS (Cloud Lab Environment)  
> **User:** toor  
> **Status:** ✅ Completed

---

## 🧱 Repository Structure (Lab Format)

```text id="7o9ra3"
lab14-building-a-scalable-camel-route-in-openshift/
├── README.md
├── commands.sh
├── output.txt
├── interview_qna.md
├── troubleshooting.md
├── routes/
│   └── scalable-route.java
├── manifests/
│   ├── service-monitor.yaml
│   ├── hpa-config.yaml
│   ├── pdb-config.yaml
│   └── grafana-dashboard.yaml
└── scripts/
    ├── load-test.sh
    ├── collect-metrics.sh
    └── response-time-test.sh
```

> ✅ Notes:
>
> * `routes/` contains the Camel K integration source deployed using `kamel run`.
> * `manifests/` contains OpenShift/Kubernetes YAML used for observability and autoscaling.
> * `scripts/` contains load testing and metric collection tooling used to validate autoscaling behavior.

---

## 🎯 Objectives

By the end of this lab, I was able to:

* Build and deploy a scalable Apache Camel route on OpenShift (Camel K integration)
* Configure **Horizontal Pod Autoscaler (HPA)** for dynamic scaling (CPU + memory utilization)
* Implement monitoring and observability using:

  * metrics server (`oc top`)
  * Prometheus scraping (`ServiceMonitor`)
  * Grafana dashboard (ConfigMap dashboard definition)
* Test and validate scaling behavior under different load patterns
* Collect scaling metrics during testing and analyze scale-up / scale-down behavior
* Apply cloud-native integration best practices (requests/limits, health endpoints, metrics endpoints)

---

## ✅ Prerequisites

Recommended knowledge before performing this lab:

* Apache Camel routing basics
* OpenShift/Kubernetes fundamentals (projects, services, routes, scaling)
* Containerization and resource requests/limits
* REST APIs and HTTP methods
* Linux CLI skills

---

## 🧰 Tools and Technologies Used

| Tool / Tech                 | Purpose                                       |
| --------------------------- | --------------------------------------------- |
| OpenShift 4.x (`oc`)        | Project setup, routes, HPA, monitoring, logs  |
| Camel K (`kamel`)           | Deploy the integration with resource settings |
| HPA (autoscaling/v2)        | Auto-scale based on CPU/memory utilization    |
| Prometheus ServiceMonitor   | Metrics scraping configuration                |
| Grafana Dashboard ConfigMap | Visualize scaling and CPU usage               |
| curl                        | Functional endpoint testing                   |
| watch                       | Real-time monitoring (pods/HPA/top)           |
| bash scripts                | Load testing and metric collection            |

---

## 🌥️ Lab Environment

This lab was performed in a **cloud-based training environment** with OpenShift cluster access.

* OpenShift cluster: admin access
* Camel K operator: already installed
* Monitoring stack: Prometheus/Grafana available
* OS: Ubuntu 24.04.1 LTS

> 🔒 **Security Note:** The login flow included a password prompt. No passwords are stored in this repo. Cluster URLs and internal addresses are documented as shown by the environment output.

---

## 🧩 What I Built

A Camel K integration (`ScalableRoute`) that exposes:

* **Health endpoint** for probes:

  * `GET /health`
* **Main API endpoint**:

  * `GET /api/v1/process/{id}` (simulates work, returns pod name, request count)
* **POST processing endpoint**:

  * `POST /api/v1/data` (heavier simulated work)
* **Metrics endpoint**:

  * `GET /metrics` (simple JSON metrics)

### Cloud-Native Features Added

* Resource requests and limits to support autoscaling
* Service exposure and OpenShift Route for external access
* HPA configured for CPU and memory
* Pod Disruption Budget for availability during disruptions
* Monitoring:

  * Metrics server checks (`oc top`)
  * ServiceMonitor for Prometheus
  * Grafana dashboard ConfigMap
* Load generation scripts to validate scaling behavior

---

## 🛠️ Tasks Overview (High-Level)

### ✅ Task 1: Create a Scalable Camel Route

* Logged into OpenShift CLI and created a dedicated project: `camel-scaling-lab`
* Verified Camel K operator was installed (CSV shows Succeeded)
* Initialized IntegrationPlatform (`kamel install --wait`)
* Created a scalable Camel K route file (`scalable-route.java`) using Undertow + JSON binding
* Deployed integration with resource settings and service enabled
* Exposed service via OpenShift Route and tested endpoints using curl

### ✅ Task 2: Set Up OpenShift Autoscaling

* Configured Prometheus scraping using ServiceMonitor (`service-monitor.yaml`)
* Verified metrics server availability and confirmed `oc top pods` works
* Created HPA (`hpa-config.yaml`) targeting the Camel Integration object:

  * minReplicas: 2, maxReplicas: 10
  * CPU target: 70%, Memory target: 80%
  * scaleUp / scaleDown behavior tuning
* Created PodDisruptionBudget (`pdb-config.yaml`) to maintain availability

### ✅ Task 3: Monitor and Test Scaling Behavior

* Created a Grafana dashboard ConfigMap (`grafana-dashboard.yaml`)
* Built a load testing script (`load-test.sh`) with concurrent workers and mixed GET/POST traffic
* Monitored scaling in real time using `watch` (pods, HPA, and resource usage)
* Verified scale-up events and increased replica count under load
* Collected metrics using a background script (`collect-metrics.sh`)
* Validated different load profiles: gradual, spike, sustained load
* Verified scale-down behavior after load reduced (observed via watch)
* Tested response times using `response-time-test.sh` and calculated averages
* Reviewed logs for errors and performance signals; confirmed no errors found
* Patched HPA behavior for faster scale-up response when required

---

## ✅ Verification Checklist

* ✅ Integration deployed and Running
* ✅ Service created and route exposed for external access
* ✅ Health endpoint returns expected JSON
* ✅ GET/POST endpoints return responses including pod identity and request counters
* ✅ Metrics endpoint returns JSON metrics
* ✅ ServiceMonitor created successfully
* ✅ Metrics server is running and `oc top pods` returns metrics
* ✅ HPA created and immediately scales to `minReplicas=2`
* ✅ Load test triggers scale-up events and increases pod count (up to 7 observed)
* ✅ Scaling events visible via `oc get events ... | grep -i scale`
* ✅ PodDisruptionBudget created and shows allowed disruptions
* ✅ Response time averages captured for GET and POST
* ✅ No error logs found during testing

---

## 📈 Result

This lab successfully demonstrated a scalable, cloud-native Camel K integration on OpenShift:

* Scalable REST service deployed using Undertow + JSON binding
* HPA configured and actively scaling based on CPU/memory utilization
* Observability configured using ServiceMonitor and dashboard ConfigMap
* Load testing validated scale-up and scale-down behavior
* Metrics collection and response time tests provided performance insight

---

## 💡 What I Learned

* How to build Camel K services designed for Kubernetes scaling
* Why resource requests/limits are essential for HPA to work correctly
* How HPA behaves with `minReplicas` (immediate scale to baseline replicas)
* How to generate realistic load and monitor scaling decisions in real time
* How to capture scaling metrics during tests for evidence and analysis
* How to tune HPA scaling behavior windows and policies
* Best practices for cloud-native integrations:

  * health endpoints
  * metrics endpoints
  * PDB for availability
  * monitoring and dashboards

---

## 🌍 Why This Matters (Real-World Relevance)

Scalable integration services are core in enterprise environments:

* Microservices that must handle variable load (seasonal spikes, batch workloads)
* Integration APIs that need automatic scaling without manual intervention
* Production reliability depends on:

  * observability (metrics)
  * controlled scaling policies
  * resiliency controls (PDB)
  * capacity planning using load testing results

This lab demonstrates how to combine **Camel integration logic** with **OpenShift autoscaling and observability** patterns for production-grade cloud-native services.

---

## ✅ Conclusion

In this lab, I:

* Built a scalable Camel K REST service on OpenShift
* Enabled external access via OpenShift Route
* Configured HPA for CPU/memory-driven autoscaling
* Added monitoring using ServiceMonitor and Grafana dashboard ConfigMap
* Validated scale-up and scale-down through multiple load patterns
* Measured performance under load and tuned scaling settings

