# Intelligent Student Information & Performance Tracking System

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20S3%20%7C%20ECR-FF9900?style=flat&logo=amazon-aws&logoColor=white)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=flat&logo=github-actions&logoColor=white)
![AI](https://img.shields.io/badge/AI-Spring_AI%20%7C%20Gemini-6DB33F?style=flat&logo=spring&logoColor=white)

A full-stack academic platform that enables teachers to record and manage student grades, generate reports, and gives students secure access to their own academic records. It also ships with a conversational AI assistant (Spring AI + Google Gemini) with per-conversation memory. Built with a production-grade CI/CD pipeline deploying to AWS.

**Live Demo:** [https://student-tracking-system.example.com](https://student-tracking-system.example.com) _(placeholder — I will update it when I finish it)_

---

## Architecture

### Project structure

```
student-tracking-system/
├── .github/
│   └── workflows/
│       ├── ci.yml                    # Continuous integration
│       └── deploy.yml                # AWS deployment
├── backend/                          # Spring Boot 3.4 / Java 21 API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gradems/
│   │   │   │   ├── controller/       # REST controllers (Auth, Student, Teacher, Grade, Chat)
│   │   │   │   ├── service/          # Business logic + AiChatClient / SpringAiChatClient
│   │   │   │   ├── repository/       # Spring Data JPA repositories
│   │   │   │   ├── entity/           # JPA entities
│   │   │   │   ├── dto/              # Request / response DTOs
│   │   │   │   ├── config/           # Security, Spring AI, OpenAPI config
│   │   │   │   ├── security/         # JWT filter & helpers
│   │   │   │   └── exception/        # Global exception handling
│   │   │   └── resources/
│   │   │       ├── application.yml   # (+ application-dev.yml / application-prod.yml)
│   │   │       └── db/migration/     # Flyway SQL migrations (V1__init, V2__seed, ...)
│   │   └── test/                     # JUnit / Spring Security tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                         # React 18 + TypeScript SPA
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   ├── api/                      # Axios API clients
│   │   ├── store/                    # Zustand state
│   │   ├── router/
│   │   ├── lib/
│   │   ├── types/
│   │   └── main.tsx
│   ├── Dockerfile
│   ├── nginx.conf
│   └── vite.config.ts
├── docker-compose.yml                # Local development stack
├── docker-compose.prod.yml           # Production overrides
├── .env.example                      # Environment variable template
└── README.md
```

### Backend request flow (layered Spring Boot)

```
  HTTP request
      │
      ▼
  Spring Security filter chain  ──  JwtAuthenticationFilter (stateless JWT)
      │                             + @PreAuthorize method-level RBAC
      ▼
  Controller  ──  Auth / Student / Teacher / Grade / Chat
      │
      ▼
  Service  ──  business logic, DTO mapping, validation
      │                                   │
      ▼                                   ▼
  Repository (Spring Data JPA)     ChatService ─► AiChatClient (interface)
      │                                   └─ SpringAiChatClient
      ▼                                        └─ Spring AI ChatClient
  PostgreSQL 16  (Flyway-managed schema)            ├─ MessageWindowChatMemory (per conversationId)
                                                    └─ Google Gemini API (OpenAI-compatible)
```

### Local development (Docker Compose)

```
  docker-compose.yml
  ┌───────────────────────────────────────────────┐
  │  ┌───────────┐  ┌────────────┐  ┌───────────┐  │
  │  │ Nginx:80  │  │ Spring:8080│  │  PG:5432  │  │
  │  │ (React)   │  │ (Backend)  │  │(Database) │  │
  │  └───────────┘  └────────────┘  └───────────┘  │
  └───────────────────────────────────────────────┘
```

### Deployment topology (AWS)

```
                    ┌─────────────────────────┐
                    │        Browser          │
                    │  React 18 + TypeScript  │
                    └───────────┬─────────────┘
                                │
          ┌─────────────────────┴──────────────────────┐
          │ 1) load SPA (HTTPS)      2) REST /api/** (HTTPS + JWT)
          ▼                                             ▼
   ╔═══════════════════════ AWS Cloud ═══════════════════════════════╗
   ║                                                                  ║
   ║   ┌────────────┐     ┌────────────┐     ┌───────────────────┐    ║
   ║   │ CloudFront │────►│ S3 Bucket  │     │   EC2  (Docker)   │    ║
   ║   │   (CDN)    │     │  SPA build │     │  Spring Boot API  │    ║
   ║   └────────────┘     └────────────┘     │       :8080       │    ║
   ║                                         └────┬─────────┬────┘    ║
   ║   ┌────────────┐                             │ JDBC    │         ║
   ║   │ Amazon ECR │  Docker image registry      ▼         │         ║
   ║   │            │                    ┌────────────────┐ │         ║
   ║   └────────────┘                    │  Amazon RDS    │ │         ║
   ║                                     │ PostgreSQL 16  │ │         ║
   ║                                     └────────────────┘ │         ║
   ╚═══════════════════════════════════════════════════════│═════════╝
                                                            │ HTTPS (Spring AI)
                                                            ▼
                                                ┌───────────────────────┐
                                                │   Google Gemini API   │
                                                │  external LLM service │
                                                │ (gemini-3.5-flash-lite)│
                                                └───────────────────────┘
```

---

## Tech Stack

| Layer      | Technology                                                            |
|------------|-----------------------------------------------------------------------|
| Frontend   | React 18, TypeScript, Vite, Tailwind CSS, React Query, Zustand, Nginx |
| Backend    | Spring Boot 3.4, Java 21, Maven                                       |
| Database   | PostgreSQL 16, Spring Data JPA / Hibernate, Flyway migrations         |
| Auth       | Spring Security, JWT (JJWT, HMAC-SHA256), BCrypt, role-based access    |
| API Docs   | SpringDoc OpenAPI (Swagger UI)                                        |
| AI         | Spring AI 1.0, Google Gemini (`gemini-3.5-flash-lite`)               |
| Container  | Docker, Docker Compose                                                |
| CI/CD      | GitHub Actions                                                        |
| Cloud      | AWS EC2, S3, CloudFront, ECR, RDS                                     |

---

## Quick Start (Local Development)

### Prerequisites

- Docker Desktop 24+
- Docker Compose v2

### Run the full stack in one command

```bash
git clone https://github.com/hudatou123/Intelligent-Student-Information-Performance-Tracking-System.git
cd Intelligent-Student-Information-Performance-Tracking-System
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

# Optional: enable the AI assistant (omit to use the placeholder stub)
export GOOGLE_API_KEY=your-google-ai-studio-key

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

Interactive docs are served by SpringDoc OpenAPI at `/swagger-ui.html` (spec at `/api-docs`).
All endpoints are stateless; protected routes require a `Authorization: Bearer <JWT>` header.
"Authenticated" below means any signed-in role (Admin / Teacher / Student).

### Authentication

| Method | Endpoint             | Description                              | Access  |
|--------|----------------------|------------------------------------------|---------|
| POST   | `/api/auth/login`    | Login, receive access + refresh JWT      | Public  |
| POST   | `/api/auth/refresh`  | Exchange a refresh token for a new access token | Public  |
| POST   | `/api/auth/register` | Register a new account                   | Admin   |

### Students

| Method | Endpoint                | Description                     | Access        |
|--------|-------------------------|---------------------------------|---------------|
| GET    | `/api/students`         | List students (paginated)       | Authenticated |
| GET    | `/api/students/{id}`    | Get student by ID               | Authenticated |
| GET    | `/api/students/count`   | Total student count             | Authenticated |
| POST   | `/api/students`         | Create a new student            | Admin         |
| PUT    | `/api/students/{id}`    | Update student info             | Admin         |
| DELETE | `/api/students/{id}`    | Remove a student                | Admin         |

### Teachers

| Method | Endpoint                | Description                     | Access        |
|--------|-------------------------|---------------------------------|---------------|
| GET    | `/api/teachers`         | List teachers (paginated)       | Authenticated |
| GET    | `/api/teachers/{id}`    | Get teacher by ID               | Authenticated |
| GET    | `/api/teachers/count`   | Total teacher count             | Authenticated |
| POST   | `/api/teachers`         | Create a new teacher            | Admin         |
| PUT    | `/api/teachers/{id}`    | Update teacher info             | Admin         |
| DELETE | `/api/teachers/{id}`    | Remove a teacher                | Admin         |

### Grades

| Method | Endpoint                          | Description                              | Access        |
|--------|-----------------------------------|------------------------------------------|---------------|
| GET    | `/api/grades`                     | List grades (paginated)                  | Authenticated |
| GET    | `/api/grades/{id}`                | Get a single grade entry                 | Authenticated |
| POST   | `/api/grades`                     | Record a new grade                       | Teacher/Admin |
| PUT    | `/api/grades/{id}`                | Update a grade                           | Teacher/Admin |
| DELETE | `/api/grades/{id}`                | Delete a grade entry                     | Admin         |
| GET    | `/api/grades/student/{studentId}` | Grades for a specific student (paginated)| Authenticated |
| GET    | `/api/grades/stats/student/{id}`  | Student average score + grade distribution | Authenticated |
| GET    | `/api/grades/stats/courses`       | Average score per course                 | Authenticated |

### AI Assistant

| Method | Endpoint     | Description                                                  | Access        |
|--------|--------------|--------------------------------------------------------------|---------------|
| POST   | `/api/chat`  | Send a message to the AI assistant (per-conversation memory) | Authenticated |

Request body: `{ "message": "...", "conversationId": "optional-id" }`. The response returns a
`conversationId`; pass it back on the next request to keep the conversation's context.

---

## AI Assistant

The backend ships with a conversational AI assistant (`POST /api/chat`) built on
[Spring AI](https://docs.spring.io/spring-ai/reference/) using Google's Gemini model, with
per-conversation chat memory. Spring AI's OpenAI client talks to the Gemini
[OpenAI-compatible endpoint](https://ai.google.dev/gemini-api/docs/openai), so only a Google AI
Studio API key is needed — no GCP project or service account.

It runs in one of two modes, selected automatically by whether a valid API key is present:

- **Placeholder mode (default):** with no real key configured, `/api/chat` returns a canned
  stub response, so the feature can be exercised end-to-end without any credentials.
- **Live mode:** set `GOOGLE_API_KEY` to a real key and restart — the assistant then calls
  Gemini. No code changes are required.

```bash
# Enable live mode locally
echo "GOOGLE_API_KEY=your-google-ai-studio-key" >> .env
docker compose up --build -d
```

Get a key from [Google AI Studio](https://aistudio.google.com/apikey). Never commit your real
key; `.env` is git-ignored.

### Framework Choice: Spring AI vs LangChain4j

The assistant uses **Spring AI** (the official Spring project, 1.0.0 GA) rather than LangChain4j.
For a Spring Boot codebase, Spring AI integrates natively — auto-configured `ChatClient`/`ChatModel`
beans, standard `spring.ai.*` properties, a first-class `VectorStore` abstraction (`PgVectorStore`)
for the planned RAG work, and `Advisor`-based chat memory and retrieval — keeping the whole stack on
one coherent, well-supported ecosystem. Swapping the model provider (here, Anthropic → Google
Gemini) is just a dependency and config change; the application code is unchanged.

LangChain4j was evaluated first and is equally capable; the original LangChain4j implementation of
this feature is preserved for comparison at commit
[`9dbf9f4`](../../tree/9dbf9f4). The framework is isolated behind a small `AiChatClient` interface,
so swapping providers touches a single implementation class.

---

## Environment Variables

Copy `.env.example` to `.env` and fill in real values before deploying.

| Variable                   | Description                                   | Required  |
|----------------------------|-----------------------------------------------|-----------|
| `DATABASE_URL`             | Full JDBC URL for PostgreSQL                  | Yes       |
| `DB_USERNAME`              | PostgreSQL username                           | Yes       |
| `DB_PASSWORD`              | PostgreSQL password                           | Yes       |
| `JWT_SECRET`               | HMAC secret, minimum 256 bits                 | Yes       |
| `GOOGLE_API_KEY`           | Google AI Studio key for the AI assistant; blank = placeholder stub | No |
| `GEMINI_MODEL`             | Gemini model id (default `gemini-3.5-flash-lite`) | No |
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

## License

MIT License — see [LICENSE](LICENSE) for details.
