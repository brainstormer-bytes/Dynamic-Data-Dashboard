# Repo Rules
Experienced (E): Roles 1, 2, 3. Solo (S): Roles 4, 5, 6.

## Branch naming: type/area/short-description
e.g. feature/query-engine/group-by-sum, feature/frontend/chart-widget, feature/schema/workspace-entity

## PR rules
1. Max 400 changed lines (500 for auto-generated files or DB setup scripts)
2. One purpose per PR
3. Roles 1, 2, 3 review: 1 approval (2 for security/API changes), no self-approval
4. Respond to reviews within 48 hours
5. No direct pushes to main
6. Draft PRs for early feedback (all roles)
7. Flag new secrets, DB updates, frontend-backend contract changes

## Conflicts
- Resolve your own: pull latest main first
- Shared files need the owner's approval
- Hard conflicts: call after ~15 minutes
- Owners: openapi.yaml = Role 1 | query engine, shared DTOs = Role 2 | migrations, entities, cache = Role 3 | security config, @ControllerAdvice = Role 4 (pair with Role 2 or 1) | pom.xml, application.yml = Role 1
