# 🧠 Lab 13 — Interview Q&A (Deploying Camel Routes on OpenShift with Camel K)

> These questions reinforce Camel K + OpenShift deployment skills that commonly appear in cloud-native integration scenarios and exams.

---

## 1) What is Camel K and how is it different from “classic” Apache Camel deployments?
Camel K is a Kubernetes-native integration runtime that lets you run Camel routes directly on Kubernetes/OpenShift as **Integrations**.  
Instead of packaging and deploying a full app manually, you can deploy a route source file (`.java`, `.yaml`, etc.) and Camel K builds and runs it automatically using an Operator.

---

## 2) What is the role of the Camel K Operator?
The Operator manages the full lifecycle of integrations:
- building integration images
- deploying pods/deployments
- scaling
- applying traits
- monitoring integration status  
It reconciles Integration resources in the cluster.

---

## 3) In this lab, how was the Camel K Operator installed?
It was installed via OperatorHub using a **Subscription** manifest (`camel-k-subscription.yaml`) applied with:
- `oc apply -f camel-k-subscription.yaml`
Then verified using CSV status transitions until `Succeeded`.

---

## 4) What does `oc get csv -w` help you verify?
It watches the ClusterServiceVersion phase changes during operator installation, such as:
- Pending → InstallReady → Installing → Succeeded  
This confirms the operator installed correctly.

---

## 5) What is an IntegrationPlatform in Camel K?
IntegrationPlatform is a cluster resource that configures and represents the Camel K runtime environment within a namespace.  
When it is `Ready`, Camel K can build and run integrations successfully.

---

## 6) Why do we run `kamel install --wait`?
It initializes Camel K resources in the namespace and waits until the IntegrationPlatform is ready, ensuring the environment is prepared to deploy integrations.

---

## 7) What happens when you run `kamel run <RouteFile.java>`?
Camel K:
1. creates an Integration resource
2. builds the runtime kit if needed
3. creates a builder pod
4. produces an image
5. deploys a running pod/deployment for the integration  
The integration moves through phases like Building → Deploying → Running.

---

## 8) What is a “kit” in Camel K (`KIT` column in `kamel get`)?
A kit represents a build/runtime artifact (dependencies + base image) used to run integrations.  
Multiple integrations can reuse the same kit if compatible.

---

## 9) How was the REST API exposed in OpenShift in this lab?
Steps:
- Enabled service trait during deployment:
  - `kamel run RestApiRoute.java --name rest-api --trait service.enabled=true`
- Exposed the service using OpenShift Route:
  - `oc expose service rest-api`
- Tested endpoints using the route hostname.

---

## 10) What Camel component was used to serve HTTP in the Camel K REST route?
The route used:
- `platform-http`  
This is commonly used in Camel K on Kubernetes/OpenShift to expose HTTP services.

---

## 11) How did you verify the integrations were running?
Using:
- `kamel get` (integration phase + replicas + status)
- `oc get pods` (pod status Running)
- `oc get integrations` (CR status)
- logs via `oc logs`

---

## 12) How did you scale an integration in this lab?
Using:
```bash
kamel scale file-processor --replicas=2
````

Then verified replicas/pods using:

* `oc get pods -l camel.apache.org/integration=file-processor`
* `kamel get file-processor`

---

## 13) How can you debug an integration stuck in “Building” phase?

Common checks:

* `kamel get <integration> -w`
* `oc get pods` (builder pod status)
* `oc logs` for builder/integration pods
* `oc describe integration <name>`
* verify IntegrationPlatform is Ready:

  * `oc get integrationplatform`

---

## 14) What OpenShift commands help you troubleshoot runtime issues?

Examples:

* `oc describe pod <pod>`
* `oc get events --sort-by='.lastTimestamp'`
* `oc logs -f deployment/<name>`
* `oc top pods` (resource pressure)

---

## 15) Why is cleanup important after cluster labs?

Because resources left behind can:

* consume quotas (CPU/memory/storage)
* clutter namespaces
* cause confusion in later labs
* increase cost in real environments
  Cleanup ensures the cluster remains stable and reusable.

---

