# Mini ATS

Applicant Tracking System to manage candidates, hiring stages, search and dashboard metrics.

**Stack:** Spring Boot 3 (Java 17) · PostgreSQL 16 · React 18 (Vite) · Docker Compose

## 🚀 Live Demo

**Try Mini ATS here:** [https://mini-ats-frontend-7fiz.onrender.com/](https://mini-ats-frontend-7fiz.onrender.com/)

## Run it

```bash
docker compose up --build
```
- UI: http://localhost:3000
- API: http://localhost:8080/api  (health: `/actuator/health`)

### Local dev (without Docker)
```bash
# DB
docker run -d --name ats-db -e POSTGRES_DB=ats -e POSTGRES_USER=ats -e POSTGRES_PASSWORD=ats -p 5432:5432 postgres:16-alpine
# Backend
cd backend && mvn spring-boot:run
# Frontend (proxies /api -> :8080)
cd frontend && npm install && npm run dev      # http://localhost:5173
```
Tests: `cd backend && mvn test`

## Features
- **Candidate CRUD** with validation, duplicate-email protection
- **Hiring pipeline:** `APPLIED → SCREENING → INTERVIEW → OFFER → HIRED`, with `REJECTED` allowed from any active stage. Illegal jumps (e.g. APPLIED → OFFER) return `422`.
- **Stage history / audit trail** for every move, with optional comment
- **Search & filter:** free text over name/email/position/skills, stage, min experience; pagination + whitelisted sorting
- **Dashboard:** totals, per-stage pipeline, new in last 7 days, hire rate, avg days-to-hire, top positions

## API

| Method | Path | Description |
|---|---|---|
| POST | `/api/candidates` | Create (starts at APPLIED) |
| GET | `/api/candidates?q=&stage=&position=&minExperience=&page=&size=&sortBy=&dir=` | Search / list (paged) |
| GET | `/api/candidates/{id}` | Get one |
| PUT | `/api/candidates/{id}` | Update details |
| DELETE | `/api/candidates/{id}` | Delete |
| PATCH | `/api/candidates/{id}/stage` | `{ "stage": "SCREENING", "comment": "..." }` |
| GET | `/api/candidates/{id}/history` | Stage timeline |
| GET | `/api/dashboard/metrics` | Dashboard numbers |

Errors use one shape: `{ "status": 400, "message": "...", "errors": { "field": "reason" } }`
(400 validation · 404 not found · 409 duplicate email · 422 invalid stage transition).

Quick test:
```bash
curl -X POST localhost:8080/api/candidates -H 'Content-Type: application/json' \
 -d '{"fullName":"Asha Rao","email":"asha@x.com","position":"Backend Engineer","skills":"Java,Spring","experienceYears":3}'
curl -X PATCH localhost:8080/api/candidates/1/stage -H 'Content-Type: application/json' -d '{"stage":"SCREENING"}'
```

## Design & scalability notes

```
React (nginx) ──► Spring Boot (stateless, N replicas behind LB) ──► PostgreSQL
```
- **Layered architecture:** controller → service → repository; DTOs separate from entities; one global exception handler.
- **Stateless backend** (no sessions) → scale horizontally behind a load balancer (`docker compose --scale`, Kubernetes HPA).
- **Indexes** on `stage`, `position`, `createdAt`, unique on `email`; dashboard uses `GROUP BY` queries, not in-memory counting.
- **Pagination everywhere**, page size capped at 100, sort fields whitelisted (no arbitrary column injection).
- **Transactions:** a stage change and its history row commit atomically.
- **Why PostgreSQL:** relational data with strong consistency, constraints and aggregation fit an ATS well.

### Next steps for production scale
1. Replace `ddl-auto=update` with **Flyway** migrations.
2. **Cache** `/dashboard/metrics` (Caffeine locally, Redis across replicas, ~30s TTL).
3. Search at scale: Postgres `pg_trgm`/full-text GIN index, then Elasticsearch/OpenSearch if needed.
4. Resumes in **S3/object storage** (store only the URL in DB).
5. **Auth** (Spring Security + JWT/OIDC, roles: recruiter, hiring manager) and rate limiting.
6. Async side effects (emails, notifications) via **Kafka/RabbitMQ** on stage-change events.
7. Postgres read replicas for dashboard/reporting queries; add observability (Micrometer + Prometheus).
