# PFIE Backend

## Prerequisites

- Java 21
- Docker (for Postgres/MinIO)

## Local setup (quick)

- Copy `.env.example` to `.env` and set any required values.
  - `.env.example` is the source of truth for required env vars.
- Start dependencies:

```sh
docker compose up -d
```

## Run migrations explicitly

Flyway is disabled in app config; run migrations with the migrate entrypoint:

```sh
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.main-class=com.kay.pfie.PfieMigrate
```

## Run the app

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Run tests

```sh
./mvnw test
```

## API base

All app endpoints are under `/api/v1`. Health endpoints are handled by Actuator.

## Swagger / OpenAPI

Swagger UI is available at `/swagger-ui/index.html`.
Use the **Authorize** button and paste a JWT (`Bearer <token>`) to call protected endpoints.

## Docs

See `docs/runbook.md` for deployment notes and migration flow.
