# Automated Java Web App Deployment using CI/CD Pipeline

This project is a complete enterprise-grade Java Spring Boot Employee Management web application specifically architected to demonstrate DevOps best practices. It showcases a complete pipeline incorporating continuous integration, continuous deployment, containerization, orchestration, and service performance monitoring.

## 🚀 Key Features & Modules

### 1. Application Modules
*   **Authentication Module**: User Registration, Login, and Logout functionality with custom templates, and Spring Security Role-Based Access Control (RBAC) (roles: `USER` and `ADMIN`).
*   **Dashboard Module**: Consolidates statistics (employee counts, API metrics, cluster health status, and role info).
*   **Employee Management Module**: Complete CRUD (Create, Read, Update, Delete) capability, Search filters, and view profiles.
*   **Profile Module**: Allows users to update their metadata and modify passwords.

### 2. DevOps Architecture
*   **Maven Build Automation**: Unified POM file mapping compiler parameters, dependencies, and testing profiles.
*   **Dockerfile**: Dual-stage optimized container configurations.
*   **Jenkinsfile**: Pipeline defining Git pulling, linting, H2-based unit tests, Docker packaging, Docker Hub push, and Kubernetes deployment triggers.
*   **Kubernetes (K8s)**: Manifest files for Deployments, Persistent Volume Claims (PVC), and NodePort/ClusterIP services, designed to run zero-downtime rolling updates.

### 3. APM & Monitoring
*   **Spring Boot Actuator**: Exposes operational health, config parameters, and custom metrics.
*   **Prometheus**: Configured to scrape JVM performance statistics.
*   **Grafana**: Custom JSON dashboard import mapping CPU, Heap, GC, response times, and throughput counts.

---

## 🛠️ Technology Stack
*   **Backend**: Java 17, Spring Boot 3.2.5, Spring Data JPA, Spring Security
*   **Frontend**: HTML5, Thymeleaf, Custom CSS (Glassmorphism design), JS, Bootstrap 5
*   **Database**: MySQL 8.0 (Production/K8s), H2 In-Memory (Test execution)
*   **CI/CD**: Jenkins, Docker Engine
*   **Orchestration**: Kubernetes
*   **Monitoring**: Prometheus, Grafana

---

## 📂 Project Directory Structure
```
DevOps-project/
├── pom.xml                     # Maven Dependencies
├── Dockerfile                  # Container Blueprint
├── Jenkinsfile                 # CI/CD Declarative Pipeline
├── schema.sql                  # MySQL Schema & Seed Scripts
├── README.md                   # System Architecture Documentation
├── DEPLOYMENT_GUIDE.md         # Jenkins, Docker, K8s, Prometheus Guides
├── k8s/                        # Kubernetes Declarative YAMLs
│   ├── mysql-deployment.yaml   # MySQL PVC, Deployment, Service
│   ├── app-deployment.yaml     # Spring Boot App Deployment & Probes
│   └── app-service.yaml        # Spring Boot NodePort Service
├── monitoring/                 # Monitoring configurations
│   ├── prometheus.yml          # Prometheus Scraping Job
│   └── grafana-dashboard.json  # Grafana Dashboard Import JSON
└── src/                        # Source Directory
```

---

## 🗄️ Database Schema & Roles

The system uses four tables (`users`, `roles`, `user_roles`, `employees`) defined in `schema.sql`.

### Default Seed Credentials (BCrypt Hashed):
1.  **System Admin**:
    *   *Username*: `admin`
    *   *Password*: `admin123`
    *   *Roles*: `ROLE_USER`, `ROLE_ADMIN`
2.  **Regular User**:
    *   *Username*: `user`
    *   *Password*: `user123`
    *   *Roles*: `ROLE_USER`

---

## 🔌 REST API Documentation

The REST APIs are exposed under the `/api/employees` prefix. All endpoints support Basic Auth using the default database accounts.

| HTTP Method | Endpoint | Description | Payload Example | Success Code |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/employees` | Retrieve all employees | *None* | `200 OK` |
| **GET** | `/api/employees/{id}` | Retrieve employee by ID | *None* | `200 OK` / `404` |
| **POST** | `/api/employees` | Create a new employee | `{ "firstName": "John", "lastName": "Doe", "email": "john@company.com", "department": "QA", "designation": "Tester", "salary": 65000.00 }` | `211 Created` |
| **PUT** | `/api/employees/{id}` | Update existing record | `{ "firstName": "John", "lastName": "Doe", "email": "john@company.com", "department": "QA", "designation": "Lead", "salary": 75000.00 }` | `200 OK` |
| **DELETE** | `/api/employees/{id}` | Remove employee (Admin only) | *None* | `200 OK` |
| **GET** | `/api/employees/search` | Query employees by keyword | `?keyword=Engineering` | `200 OK` |

---

## 💻 Local Quick Start

To run the application locally on your machine:

1.  **Prerequisites**: JDK 17, Maven 3.x, MySQL Server running locally.
2.  **Database**: Setup a database named `devops_db` and verify your username/password matches `root/root` (or update environment parameters).
3.  **Run Application**:
    ```bash
    mvn clean spring-boot:run
    ```
4.  **Access App**:
    *   Open browser: `http://localhost:8080` (or `http://localhost:8081` if port 8080 is occupied, e.g., by Jenkins)
    *   Exposed Actuator Health Check: `http://localhost:8080/actuator/health` (or port 8081)
    *   Exposed Prometheus Metrics: `http://localhost:8080/actuator/prometheus` (or port 8081)
