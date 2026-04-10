# Architecture Documentation

## Current Architecture (Iterations 1–3): Layered
Controller → Service → Repository → Database

The initial iterations use a standard layered architecture.
This is intentional — we start simple and refactor with purpose in Iteration 4.

## Target Architecture (Iteration 4+): Hexagonal

See Iteration 4 in the SRS for the full migration plan.

## Architecture Decision Records

### ADR-001: Layered before Hexagonal
- **Decision:** Start with layered architecture, migrate to hexagonal in Iteration 4
- **Rationale:** Starting with hexagonal on day one is premature — the domain isn't
  clear yet. By Iteration 4, we understand the domain well enough to model it properly.
- **Trade-off:** Some rework in Iteration 4, but a more motivated and correct refactor.

### ADR-002: Monolith over Microservices
- **Decision:** Single deployable Spring Boot application
- **Rationale:** One domain, one developer, one VM. Microservices solve organizational
  problems (team autonomy), not technical problems at this scale.

### ADR-003: PostgreSQL over MySQL/MongoDB
- **Decision:** PostgreSQL as the sole data store
- **Rationale:** Superior support for complex aggregation queries (window functions, CTEs),
  JSON columns for flexible data, and better standards compliance.
