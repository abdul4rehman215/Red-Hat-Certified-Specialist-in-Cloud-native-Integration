# 🧠 Lab 14 — Interview Q&A (Scalable Camel K Route + HPA on OpenShift)

> These questions focus on scalable cloud-native integration patterns: Camel K service design, HPA behavior, monitoring, and load testing.

---

## 1) Why are resource requests required for HPA to work properly?
HPA calculates utilization as a percentage of **resource requests**, not limits.  
If CPU/memory requests are missing or too low/high, HPA decisions become inaccurate or scaling may not work at all.

---

## 2) What was the scale target in the HPA configuration in this lab?
The HPA targeted the **Camel K Integration** object:

- `apiVersion: camel.apache.org/v1`
- `kind: Integration`
- `name: scalable-camel-service`

So HPA scaled the integration’s replicas rather than manually scaling a Deployment.

---

## 3) Why did the integration immediately scale to 2 replicas after applying HPA?
Because HPA had:
- `minReplicas: 2`  
Even without load, HPA enforces the minimum number of replicas to maintain baseline availability.

---

## 4) What endpoints did the scalable Camel service expose and why?
- `GET /health` → Kubernetes readiness/liveness style check
- `GET /api/v1/process/{id}` → processing endpoint (simulated work)
- `POST /api/v1/data` → heavier processing endpoint
- `GET /metrics` → lightweight JSON metrics for visibility

These patterns are common in production services: health + API + metrics.

---

## 5) Why is adding `processed_by` (pod hostname) useful in a scalable service?
It proves load distribution across replicas and helps debugging:
- which pod handled a request
- verify scaling actually spreads traffic
- confirm route behavior during autoscaling

---

## 6) What load testing approach was used in this lab?
A bash load generator (`load-test.sh`) created concurrent workers that:
- randomly mixed GET and POST traffic
- ran for a fixed duration
- sent requests with small delays  
This produced CPU load that triggered HPA scale-up.

---

## 7) What evidence showed that HPA scaled up under load?
Scaling evidence included:
- `oc get events ... | grep -i scale` showing SuccessfulRescale events
- increased pod count via:
  - `oc get pods -l camel.apache.org/integration=scalable-camel-service`
Examples showed scaling up to **7 replicas**.

---

## 8) What metrics were used by HPA for scaling decisions?
The HPA used:
- CPU utilization target: **70%**
- Memory utilization target: **80%**
Both were specified using autoscaling/v2 resource metrics.

---

## 9) What is the purpose of HPA behavior settings (scaleUp/scaleDown)?
Behavior settings control how fast scaling happens and help avoid thrashing:
- `stabilizationWindowSeconds` prevents rapid oscillations
- policies define how much scaling can occur within a time interval
This improves stability in production.

---

## 10) Why was a Pod Disruption Budget (PDB) added?
PDB ensures availability during disruptions (node drain, updates):
- `minAvailable: 1` ensures at least one pod remains running
This prevents total downtime when pods are evicted.

---

## 11) What is the purpose of a ServiceMonitor in OpenShift monitoring?
ServiceMonitor tells Prometheus what to scrape:
- which service labels to match
- which endpoint/port/path to scrape
- scrape interval  
It enables observability of the app through Prometheus/Grafana.

---

## 12) Why is monitoring important for autoscaling systems?
Autoscaling without monitoring is risky because:
- you can’t confirm if scaling matches real demand
- you can’t spot bottlenecks (CPU/memory pressure)
- you can’t validate performance impacts under load  
Monitoring provides feedback loops for tuning HPA thresholds.

---

## 13) How was response time measured in this lab?
A script (`response-time-test.sh`) used curl timing:
- `curl -w "%{time_total}" ...`
Then computed average response time using awk.

Observed averages:
- GET avg: ~0.215s
- POST avg: ~0.355s

---

## 14) What troubleshooting step was taken to speed up scaling responsiveness?
The HPA was patched to reduce scaleUp stabilization window:
- from 60 seconds → **30 seconds**

Command:
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
````

---

## 15) What are real-world improvements you would make to this design?

Examples:

* expose Prometheus metrics in proper format instead of JSON
* add retries/timeouts and circuit breaker for downstream calls
* add structured logging and tracing (OpenTelemetry)
* enforce authentication/authorization (JWT/OAuth2)
* set proper readiness/liveness probes tied to real dependencies
* create SLOs and alerts (latency, error rate, saturation)
* tune HPA thresholds based on performance testing results

---
