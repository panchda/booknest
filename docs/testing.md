# BookNest Testing Guide

## Automated backend tests

From the repository root, configure `.env` and run:

```bash
./run-tests.sh
```

The script:

1. Moves to the project root.
2. Requires `.env` and exports its variables for the test processes.
3. Verifies Docker Compose is available.
4. Starts PostgreSQL with `docker compose up -d --wait postgres`.
5. Runs `./mvnw test` sequentially for Book Service, Review Service, and Auth Service.
6. Stops on any failure and prints `All backend tests passed!` on success.

The most recent confirmed run contained **31 passing tests**: Book Service 16, Review Service 6, Auth Service 9. These include unit tests and application-context tests.

### Unit-test focus

- **Book Service:** Listing, searching, retrieving, and book operations.
- **Review Service:** Listing reviews, trimming submitted fields, validating that books exist, and deletion behavior.
- **Auth Service:** Registration, email normalization, duplicate-email handling, login, password checks, and JWT generation.

Unit tests use JUnit 5 and Mockito to isolate service logic. Application-context tests also check that Spring Boot can initialize with the required configuration.

## Clean installation verification

Use a fresh GitHub ZIP or a new clone in a separate directory. **Do not delete existing application volumes just to test installation.**

### Preparation

- [ ] Confirm Docker and Docker Compose are installed and running.
- [ ] Confirm ports 3000, 8080, 8761, and 5433 are free.
- [ ] Extract the project and open a terminal in its root directory.
- [ ] Copy `.env.example` to `.env`.
- [ ] Set `DB_PASSWORD` and generate `JWT_SECRET` using `openssl rand -base64 32`.

### Application startup

- [ ] Run `docker compose up -d --build`.
- [ ] Run `docker compose ps` and confirm all seven containers are running.
- [ ] Confirm PostgreSQL reports `healthy`.
- [ ] Open `http://localhost:8761` and inspect service registrations.
- [ ] Open `http://localhost:3000` and confirm the catalog displays five sample books.
- [ ] Run `curl -i http://localhost:8080/api/v1/books` and confirm HTTP 200.

### Functional verification

- [ ] Search for a book by title or author.
- [ ] Open a book details page and read its reviews.
- [ ] Confirm an anonymous visitor cannot submit a review through the UI.
- [ ] Register a new account.
- [ ] Sign in with the new account.
- [ ] Submit a review and confirm it appears in the list.
- [ ] Sign out and confirm the protected review form is hidden.
- [ ] Confirm missing or invalid JWTs are rejected on protected API endpoints.
- [ ] Confirm USER and ADMIN permissions behave as configured.
- [ ] Verify administrative book CRUD and review deletion using a preconfigured ADMIN account (public registration does not create one).

### Automated verification

- [ ] Run `./run-tests.sh` (or `bash run-tests.sh` if archive permissions were lost).
- [ ] Confirm all three service test suites succeed.
- [ ] Confirm the script ends with `All backend tests passed!`.

### Shutdown

```bash
docker compose down
```

This retains the PostgreSQL volume. `docker compose down -v` deletes persistent data and should be used only when intentionally resetting a disposable environment.

## Common failures

| Symptom | What to check |
| --- | --- |
| Docker Compose unavailable | `docker compose version`, Docker Desktop status |
| PostgreSQL not healthy | `docker compose logs postgres`, `.env` credentials |
| Gateway errors immediately after startup | Wait for Eureka registration; inspect service logs |
| JWT validation fails | Same generated Base64 secret in all relevant services |
| Maven context test fails | `.env` loaded and PostgreSQL available |
| Seed books missing | Fresh database volume, Book Service logs, initializer class |
| Admin requests return 403 | Account role is `USER`; an ADMIN account is required |

## Evidence for assessment

Useful screenshots for the university submission include the book catalog, search results, book details and reviews, login/register screens, Eureka registrations, Docker container status, and the final `All backend tests passed!` output.
