# Deployment & Setup Guide

This document provides step-by-step instructions to configure, run, and manage the "Automated Java Web App Deployment using CI/CD Pipeline" project.

---

## 1. Local Database Setup (MySQL)
Before running the Spring Boot application locally, create the database and seed it.
1. Log in to your MySQL server:
   ```bash
   mysql -u root -p
   ```
2. Run the schema and seed scripts:
   ```sql
   source /path/to/schema.sql;
   ```
3. Verify tables were created:
   ```sql
   USE devops_db;
   SHOW TABLES;
   SELECT * FROM users;
   ```

---

## 2. Docker Containerization
To manually containerize the application:
1. Build the Docker image locally:
   ```bash
   docker build -t yourusername/devops-java-app:1.0.0 .
   docker tag yourusername/devops-java-app:1.0.0 yourusername/devops-java-app:latest
   ```
2. Run MySQL in a Docker network:
   ```bash
   docker network create devops-net
   
   docker run -d --name mysql-container --network devops-net \
     -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=devops_db \
     -p 3306:3306 mysql:8.0
   ```
3. Run the Spring Boot application container in the same network:
   ```bash
   docker run -d --name app-container --network devops-net \
     -e DB_HOST=mysql-container -e DB_PORT=3306 -e DB_NAME=devops_db \
     -e DB_USER=root -e DB_PASSWORD=root \
     -p 8080:8080 yourusername/devops-java-app:latest
   ```
4. Verify by accessing `http://localhost:8080`.

---

## 3. Jenkins CI/CD Pipeline Setup
To implement continuous integration and deployment with Jenkins:

### Step 1: Install Required Plugins
Ensure the following plugins are installed in Jenkins:
*   Maven Integration
*   Pipeline
*   Docker Pipeline
*   Kubernetes CLI

### Step 2: Configure Credentials
In Jenkins Dashboard -> **Manage Jenkins** -> **Credentials**:
1.  **Docker Hub Credentials**:
    *   *Kind*: Username with password
    *   *ID*: `docker-hub-credentials-id`
    *   *Username*: Your Docker Hub username
    *   *Password*: Your Docker Hub token or password
2.  **Kubeconfig Credentials**:
    *   *Kind*: Secret file
    *   *ID*: `kubeconfig-credentials-id`
    *   *File*: Upload your cluster configuration file (typically found at `~/.kube/config`).

### Step 3: Create Jenkins Pipeline Job
1.  Create a **New Item** -> Select **Pipeline** -> Name it `employee-app-pipeline`.
2.  Under **Build Triggers**, select **GitHub hook trigger for GITScm polling** (enables deployment on code push).
3.  Under **Pipeline**, select **Pipeline script from SCM**.
4.  Set SCM to **Git**, paste your repository URL, and set the branch (e.g. `*/main`).
5.  Set script path to `Jenkinsfile` -> Save and run **Build Now**.

---

## 4. Kubernetes Deployment
The K8s manifests are stored in the `/k8s` directory.

### Step 1: Start Cluster (Minikube / Kind)
```bash
minikube start --driver=docker
```

### Step 2: Deploy MySQL Storage & Container
```bash
kubectl apply -f k8s/mysql-deployment.yaml
# Verify MySQL status
kubectl get pods -l app=mysql
```

### Step 3: Deploy Application & Services
```bash
kubectl apply -f k8s/app-service.yaml
kubectl apply -f k8s/app-deployment.yaml
# Verify application status
kubectl get pods -l app=employee-app
```

### Step 4: Access Application
If using Minikube:
```bash
minikube service employee-app-service --url
```
Or forward the port:
```bash
kubectl port-forward svc/employee-app-service 8080:8080
```

### Step 5: Rolling Updates Demonstration
During code updates, Jenkins updates the deployment image tag. You can monitor zero-downtime rolling updates with:
```bash
kubectl rollout status deployment/employee-app-deployment
kubectl rollout history deployment/employee-app-deployment
```

---

## 5. Monitoring Setup (Prometheus & Grafana)

### Step 1: Configure Prometheus
1. Run Prometheus in your cluster or as a Docker container using the configuration in `monitoring/prometheus.yml`.
2. To run via Docker:
   ```bash
   docker run -d --name prometheus -p 9090:9090 \
     -v $(pwd)/monitoring/prometheus.yml:/etc/prometheus/prometheus.yml \
     prom/prometheus
   ```
3. Open `http://localhost:9090` and verify the `spring-boot-actuator` target is active.

### Step 2: Configure Grafana
1. Run Grafana:
   ```bash
   docker run -d --name grafana -p 3000:3000 grafana/grafana
   ```
2. Log in at `http://localhost:3000` (default: `admin/admin`).
3. Add **Prometheus** as a Data Source:
   * URL: `http://prometheus:9090` (or `http://host.docker.internal:9090`).
4. Import the Dashboard:
   * Go to **Dashboards** -> **New** -> **Import**.
   * Upload the `monitoring/grafana-dashboard.json` file.
   * Select your Prometheus data source -> Click **Import**.
5. The dashboard will populate with graphs tracking:
   * System CPU and memory load.
   * JVM Heap usage details.
   * HTTP Server request throughput rates.
   * Request response time latencies.
