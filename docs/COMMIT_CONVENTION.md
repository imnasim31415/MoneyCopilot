# Commit Convention

This repository follows a conventional commit style to make history readable, consistent, and useful for portfolio review.

## Format

`<type>(<scope>): <subject>`

- `type` describes the purpose of the change
- `scope` is optional and refers to the affected module or area
- `subject` is a short description in imperative mood

Example:

`feat(api): add tax calculation endpoint`

## Types

- `feat`: a new feature
- `fix`: a bug fix
- `chore`: maintenance tasks, tooling, build changes, or repo configuration
- `docs`: documentation only changes
- `style`: formatting, linting, or whitespace changes without affecting logic
- `refactor`: code changes that neither fix bugs nor add features
- `perf`: performance improvements
- `test`: adding or updating tests
- `ci`: CI/CD configuration and pipeline changes

## Scope

Scopes are optional but encouraged when the change affects a specific area. Examples:

- `backend`
- `frontend`
- `api`
- `docs`
- `build`
- `config`

Example with scope:

`fix(backend): correct quarterly tax calculation`

## Subject

- Use imperative mood: `add`, `update`, `remove`
- Keep it concise and meaningful
- Do not capitalize the first word unless it is a proper noun
- Do not end with a period

## Body

Use the body to explain the motivation for the change and any additional context.

Example:

```
fix(api): correct missing income bracket handling

The previous calculation skipped the final bracket when income exactly matched the threshold.
This caused incorrect tax totals for edge-case inputs.
```

## Footer

Use the footer for related issue tracking or breaking changes.

Example:

`BREAKING CHANGE: user event schema now includes accountId`

## Branch naming

Use descriptive branch names tied to the work:

- `feature/<short-description>`
- `fix/<short-description>`
- `chore/<short-description>`
- `docs/<short-description>`

Example:

`feature/tax-engine-refactor`
