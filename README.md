# PFIE Backend

## Local setup (quick)

- Copy `.env.example` to `.env` and set any required values.
- Start dependencies:

```sh
docker compose up
```

## Run migrations explicitly

Flyway is disabled in app config; run migrations with the migrate entrypoint:

```sh
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.main-class=com.kay.pfie.PfieMigrate
```

## Run the app

```sh
./mvnw spring-boot:run
```
