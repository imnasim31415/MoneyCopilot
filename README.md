# MoneyCopilot — AI-Powered Personal Expense & Tax Tracker (Bangladesh)

A backend system for personal finance management with Bangladesh-specific tax calculation,
AI-powered insights, and bank statement import.

Built as a learning project following industry-standard practices:
hexagonal architecture, comprehensive testing, CI/CD, and production deployment.

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.x
- **Database:** PostgreSQL 16
- **Cache:** Redis 7
- **Build:** Gradle (Kotlin DSL)
- **Architecture:** Hexagonal (Ports & Adapters) — from Iteration 4
- **Testing:** JUnit 5, Mockito, Testcontainers, ArchUnit
- **CI/CD:** GitHub Actions
- **Deployment:** Docker Compose on Oracle Cloud Free Tier
- **API Docs:** SpringDoc OpenAPI 3

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

## Local Development

### Prerequisites

- Java 21 (recommend [Eclipse Temurin](https://adoptium.net/))
- Docker & Docker Compose (for PostgreSQL)
- Git

### Setup

```bash
git clone https://github.com/yourusername/fintrack.git
cd fintrack
cp .env.example .env          # Edit with your local settings
docker compose up -d db       # Start PostgreSQL
./gradlew bootRun             # Start the application
```

API docs available at: http://localhost:8080/docs

## Architecture

See [docs/architecture.md](docs/architecture.md) for detailed architecture documentation.

## License

MIT
