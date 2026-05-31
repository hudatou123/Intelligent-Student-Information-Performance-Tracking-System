# Intelligent Student Information & Performance Tracking System

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20S3%20%7C%20ECR-FF9900?style=flat&logo=amazon-aws&logoColor=white)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=flat&logo=github-actions&logoColor=white)

A full-stack academic grade management platform that enables teachers to record and manage student grades, generate reports, and gives students secure access to their own academic records. Built with a production-grade CI/CD pipeline deploying to AWS.

**Live Demo:** [https://student-tracking-system.example.com](https://student-tracking-system.example.com) _(placeholder — replace with your deployed URL)_

---

## Architecture

```
                        ┌─────────────────────────────────────┐
                        │            AWS Cloud                │
                        │                                     │
   Users (Browser)      │  ┌──────────────┐                  │
         │              │  │  CloudFront  │                  │
         │ HTTPS        │  │     CDN      │                  │
         └─────────────►│  └──────┬───────┘                  │
                        │         │                           │
                        │  ┌──────▼───────┐                  │
                        │  │   S3 Bucket  │  React SPA       │
                        │  │  (Frontend)  │  (static assets) │
                        │  └─────────────-┘                  │
                        │                                     │
         API calls      │  ┌──────────────┐                  │
         ───────────────►  │   EC2 Instance│                  │
         (port 8080)    │  │  Spring Boot  │                  │
                        │  │   (Docker)   │                  │
                        │  └──────┬───────┘                  │
                        │         │ JDBC                      │
                        │  ┌──────▼───────┐                  │
                        │  │  Amazon RDS  │                  │
                        │  │ PostgreSQL 16│                  │
                        │  └─────────────-┘                  │
                        │                                     │
                        │  ┌──────────────┐                  │
                        │  │  Amazon ECR  │  Docker images   │
                        │  └─────────────-┘                  │
                        └─────────────────────────────────────┘

  Local Development:
  ┌──────────────────────────────────────────────┐
  │  docker-compose.yml                          │
  │  ┌───────────┐  ┌───────────┐  ┌──────────┐ │
  │  │ Nginx:80  │  │Spring:8080│  │ PG:5432  │ │
  │  │ (React)   │  │ (Backend) │  │(Database)│ │
  │  └───────────┘  └───────────┘  └──────────┘ │
  └──────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer      | Technology                             |
|------------|----------------------------------------|
| Frontend   | React 18, Vite, Nginx                  |
| Backend    | Spring Boot 3, Java 21, Maven          |
| Database   | PostgreSQL 16                          |
| Auth       | JWT (HMAC-SHA256)                      |
| Container  | Docker, Docker Compose                 |
| CI/CD      | GitHub Actions                         |
| Cloud      | AWS EC2, S3, CloudFront, ECR, RDS      |

---

## Quick Start (Local Development)

### Prerequisites

- Docker Desktop 24+
- Docker Compose v2

### Run the full stack in one command

```bash
git clone https://github.com/your-username/student-tracking-system.git
cd student-tracking-system
docker compose up --build
```

| Service  | URL                        |
|----------|----------------------------|
| Frontend | http://localhost           |
| Backend  | http://localhost:8080      |
| Database | localhost:5432             |

To stop and remove containers:

```bash
docker compose down
```

To also wipe the database volume:

```bash
docker compose down -v
```

---

## Local Development (Without Docker)

### Backend

```bash
cd backend

# Requires Java 21 and a running PostgreSQL instance
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/student_tracking_dev
export SPRING_DATASOURCE_USERNAME=student_tracking
export SPRING_DATASOURCE_PASSWORD=student_tracking_dev_password
export JWT_SECRET=dev-secret-key-at-least-256-bits-long-for-hmac

./mvnw spring-boot:run
# API available at http://localhost:8080
```

### Frontend

```bash
cd frontend

cp .env.example .env.local
# Edit .env.local and set VITE_API_BASE_URL=http://localhost:8080/api

npm install
npm run dev
# App available at http://localhost:3000
```

### Run backend tests only

```bash
cd backend
./mvnw clean test
```

---

## API Endpoints

### Authentication

| Method | Endpoint             | Description              | Access  |
|--------|----------------------|--------------------------|---------|
| POST   | `/api/auth/register` | Register a new account   | Public  |
| POST   | `/api/auth/login`    | Login and receive JWT    | Public  |
| POST   | `/api/auth/refresh`  | Refresh access token     | Public  |

### Students

| Method | Endpoint                    | Description                  | Access        |
|--------|-----------------------------|------------------------------|---------------|
| GET    | `/api/students`             | List all students            | Teacher/Admin |
| GET    | `/api/students/{id}`        | Get student by ID            | Teacher/Admin |
| POST   | `/api/students`             | Create a new student         | Admin         |
| PUT    | `/api/students/{id}`        | Update student info          | Admin         |
| DELETE | `/api/students/{id}`        | Remove a student             | Admin         |
| GET    | `/api/students/{id}/grades` | Get a student's grade report | Teacher/Admin |

### Teachers

| Method | Endpoint              | Description           | Access  |
|--------|-----------------------|-----------------------|---------|
| GET    | `/api/teachers`       | List all teachers     | Admin   |
| GET    | `/api/teachers/{id}`  | Get teacher by ID     | Admin   |
| POST   | `/api/teachers`       | Create a new teacher  | Admin   |
| PUT    | `/api/teachers/{id}`  | Update teacher info   | Admin   |
| DELETE | `/api/teachers/{id}`  | Remove a teacher      | Admin   |

### Grades

| Method | Endpoint                  | Description                  | Access        |
|--------|---------------------------|------------------------------|---------------|
| GET    | `/api/grades`             | List grades (filterable)     | Teacher/Admin |
| GET    | `/api/grades/{id}`        | Get a single grade entry     | Teacher/Admin |
| POST   | `/api/grades`             | Record a new grade           | Teacher       |
| PUT    | `/api/grades/{id}`        | Update a grade               | Teacher       |
| DELETE | `/api/grades/{id}`        | Delete a grade entry         | Teacher/Admin |
| GET    | `/api/grades/my`          | Student views own grades     | Student       |
| GET    | `/api/grades/summary`     | Aggregate stats by course    | Teacher/Admin |

---

## Environment Variables

Copy `.env.example` to `.env` and fill in real values before deploying.

| Variable                   | Description                                   | Required  |
|----------------------------|-----------------------------------------------|-----------|
| `DATABASE_URL`             | Full JDBC URL for PostgreSQL                  | Yes       |
| `DB_USERNAME`              | PostgreSQL username                           | Yes       |
| `DB_PASSWORD`              | PostgreSQL password                           | Yes       |
| `JWT_SECRET`               | HMAC secret, minimum 256 bits                 | Yes       |
| `AWS_REGION`               | AWS region (e.g. `us-east-1`)                 | Yes (AWS) |
| `AWS_ACCESS_KEY_ID`        | AWS access key for CLI operations             | Yes (AWS) |
| `AWS_SECRET_ACCESS_KEY`    | AWS secret access key                         | Yes (AWS) |
| `VITE_API_BASE_URL`        | Backend API base URL for the React app        | Yes       |
| `S3_BUCKET_NAME`           | S3 bucket hosting the frontend build          | Yes (AWS) |
| `CLOUDFRONT_DISTRIBUTION_ID` | CloudFront distribution to invalidate      | Yes (AWS) |
| `EC2_HOST`                 | Public IP or DNS of your EC2 instance         | Yes (AWS) |

---

## CI/CD Pipeline

Managed by GitHub Actions. Two workflow files live in `.github/workflows/`.

### `ci.yml` — Continuous Integration

Triggered on every push to `main`/`develop` and on pull requests to `main`.

```
push / PR
    │
    ├── backend-test        (JDK 21 + ephemeral PostgreSQL service container)
    │       └── mvn clean test
    │
    ├── frontend-build      (Node 20)
    │       ├── npm ci
    │       ├── npm run build
    │       └── upload dist/ as artifact
    │
    └── docker-build        (needs: backend-test, frontend-build)
            ├── docker build ./backend
            └── docker build ./frontend
```

### `deploy.yml` — Continuous Deployment

Triggered automatically when CI passes on `main`, or manually via `workflow_dispatch`.

```
CI passes on main (or manual trigger)
    │
    └── deploy
            ├── Configure AWS credentials
            ├── Push backend image → Amazon ECR
            ├── SSH into EC2 → pull & restart container
            └── Build frontend → sync to S3 → invalidate CloudFront
```

### Required GitHub Secrets

Set these in **Settings > Secrets and variables > Actions**:

| Secret                       | Description                              |
|------------------------------|------------------------------------------|
| `AWS_ACCESS_KEY_ID`          | IAM user access key                      |
| `AWS_SECRET_ACCESS_KEY`      | IAM user secret key                      |
| `AWS_REGION`                 | AWS region                               |
| `EC2_HOST`                   | EC2 public IP or hostname                |
| `EC2_SSH_KEY`                | Private key for SSH (PEM contents)       |
| `DATABASE_URL`               | RDS JDBC connection string               |
| `DB_USERNAME`                | RDS username                             |
| `DB_PASSWORD`                | RDS password                             |
| `JWT_SECRET`                 | Production JWT signing secret            |
| `S3_BUCKET_NAME`             | Frontend S3 bucket name                  |
| `CLOUDFRONT_DISTRIBUTION_ID` | CloudFront distribution ID               |

---

## AWS Deployment (Manual First-Time Setup)

1. **ECR** — Create two repositories: `student-tracking-system-backend` and `student-tracking-system-frontend`.
2. **RDS** — Launch a PostgreSQL 16 instance; note the endpoint and credentials.
3. **EC2** — Launch an Amazon Linux 2023 instance; install Docker; open port 8080.
4. **S3** — Create a bucket with static website hosting enabled.
5. **CloudFront** — Create a distribution pointing to the S3 bucket; configure the default root object as `index.html` and add a custom error page (403/404 → `index.html`, 200) for SPA routing.
6. **GitHub Secrets** — Populate all secrets listed above.
7. Push to `main` — the deploy workflow runs automatically.

---

## Project Structure

```
student-tracking-system/
├── .github/
│   └── workflows/
│       ├── ci.yml              # Continuous integration
│       └── deploy.yml          # AWS deployment
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/           # Spring Boot source
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── application-dev.yml
│   │   └── test/               # JUnit / Testcontainers tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/           # Axios API calls
│   │   └── main.jsx
│   ├── Dockerfile
│   ├── nginx.conf
│   └── vite.config.js
├── docker-compose.yml          # Local development stack
├── docker-compose.prod.yml     # Production overrides
├── .env.example                # Environment variable template
├── .gitignore
└── README.md
```

---

## License

MIT License — see [LICENSE](LICENSE) for details.
