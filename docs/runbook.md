# PFIE Backend Runbook (Skeleton)

## Environments

- Dev: `.env` sourced via `application-dev.yml`
- Prod: env vars provided by platform (no defaults)

## Migrations

- Manual (dev/test):

```sh
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.main-class=com.kay.pfie.PfieMigrate
```

- Prod: run on startup (planned). Keep `PfieMigrate` as escape hatch.

## Health checks

- `/actuator/health` (liveness)
- `/actuator/health/readiness` (readiness when enabled)

## Troubleshooting

- If boot fails, verify required env vars in `.env.example`.
- For auth failures, confirm `JWT_SECRET` length >= 32 and `GOOGLE_CLIENT_ID` is set.
