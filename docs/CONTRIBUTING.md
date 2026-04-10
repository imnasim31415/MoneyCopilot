# Contributing

## Development Workflow

1. Create a feature branch from `develop`:
```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
```

2. Make changes and commit using Conventional Commits.

3. Push and create a Pull Request to `develop`.

4. Ensure CI passes.

5. Merge the PR (squash merge preferred for clean history).

6. Periodically merge `develop` into `main` for releases.

## Code Standards

- Follow Google Java Style Guide
- All public methods must have Javadoc
- Constructor injection only (no `@Autowired` on fields)
- Use `record` for DTOs and value objects
- Write tests for all new functionality

## Branch Naming

- `feature/description` — New features
- `fix/description` — Bug fixes
- `refactor/description` — Structural changes
- `test/description` — Test additions
- `docs/description` — Documentation
- `chore/description` — Maintenance tasks
