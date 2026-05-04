# MoneyCopilot — AI-Powered Personal Expense & Tax Tracker (Bangladesh)

A backend REST API for personal finance management with Bangladesh-specific tax calculation,
AI-powered insights, and bank statement import.

Built as a learning project following industry-standard practices:
layered architecture evolving to hexagonal, comprehensive testing, CI/CD, and production deployment on Oracle Cloud.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 4.x |
| Database | PostgreSQL 16 |
| Schema migrations | Flyway |
| Cache | Redis 7 |
| Build | Gradle (Kotlin DSL) |
| Security | Spring Security + JWT |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI 3 |
| Testing | JUnit 5, Mockito, Testcontainers, ArchUnit |
| CI/CD | GitHub Actions |
| Deployment | Docker Compose on Oracle Cloud Free Tier |
| Code style | Google Java Style Guide (Checkstyle) |

## Architecture

Iterations 1–3 use a standard **layered architecture** (Controller → Service → Repository).
Iteration 4 refactors to **Hexagonal Architecture** (Ports & Adapters) once the domain is well understood.

See [docs/architecture.md](docs/architecture.md) for detailed decisions and ADRs.

## Development Status

| Iteration | Description | Status |
|-----------|-------------|--------|
| 0 | Project Bootstrap | ✅ Complete |
| 1 | Baremetal MVP (Auth + Expenses) | 🚧 In Progress |
| 2 | Financial Awareness (Income + Dashboard) | ⬜ Planned |
| 3 | Bangladesh Tax Engine | ⬜ Planned |
| 4 | Hexagonal Architecture Refactor | ⬜ Planned |
| 5 | AI & Intelligence Layer | ⬜ Planned |
| 6 | Data Import & Background Processing | ⬜ Planned |
| 7 | DevOps & Production Readiness | ⬜ Planned |
| 8 | Advanced Financial Intelligence | ⬜ Planned |

## Iteration 0 — What was set up

- Spring Boot project with Gradle Kotlin DSL
- PostgreSQL + Redis via Docker Compose
- Flyway for database schema migrations
- Spring Security (JWT auth comes in Iteration 1)
- Spring Boot Actuator (`/actuator/health`)
- Checkstyle enforcing Google Java Style Guide
- EditorConfig for consistent formatting across editors
- Pre-commit hooks with Conventional Commits enforcement
- GitHub Actions CI — builds and runs tests on every push to `main` and `dev`
- Project documentation structure (`docs/`, `CHANGELOG.md`)

## Iteration 1 — What's coming

- User registration and JWT-based login + token refresh
- Full expense CRUD with pagination, filtering, and soft-delete
- Predefined expense categories (`FOOD`, `TRANSPORT`, `BILLS`, etc.)
- Global exception handler returning RFC 7807 Problem Details
- Swagger UI at `/docs`
- Testcontainers-based integration tests

## Branch Strategy

```
main        — production-ready code
dev         — integration branch (all features merge here first)
feature/*   — new features
fix/*       — bug fixes
```

## Local Development

### Prerequisites

- Java 21 ([Eclipse Temurin](https://adoptium.net/) recommended)
- Docker & Docker Compose
- Git

### Setup

```bash
git clone https://github.com/imnasim31415/MoneyCopilot.git
cd MoneyCopilot
cp .env.example .env          # fill in your local values
docker compose up -d db       # start PostgreSQL
./gradlew bootRun             # start the application
```

App runs at `http://localhost:8080`

| Endpoint | Description |
|----------|-------------|
| `GET /actuator/health` | Health check |
| `GET /docs` | Swagger UI (available from Iteration 1) |

### Running Tests

```bash
./gradlew test
```

Integration tests use Testcontainers — Docker must be running.

### Code Style

Checkstyle enforces the Google Java Style Guide. Run it manually:

```bash
./gradlew checkstyleMain checkstyleTest
```

## License

MIT
