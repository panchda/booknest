# BookNest REST API

**Base URL:** `http://localhost:8080`

The API Gateway is the public entry point for all requests. Send JSON requests with `Content-Type: application/json`. Protected endpoints require `Authorization: Bearer <token>`.

## Authentication

### Register

`POST /api/v1/auth/register` — expected success: **201 Created**.

```json
{"email":"reader@example.com","password":"ChooseAStrongPassword123!"}
```

Registration normalizes the email (trims whitespace and converts it to lowercase), hashes the password, and assigns role `USER`. A duplicate email results in **409 Conflict**. Request validation may reject invalid input with **400 Bad Request**.

Example response shape:

```json
{"id":1,"email":"reader@example.com","role":"USER"}
```

### Login

`POST /api/v1/auth/login` — expected success: **200 OK**.

```json
{"email":"reader@example.com","password":"ChooseAStrongPassword123!"}
```

Example response shape:

```json
{"accessToken":"<jwt>","tokenType":"Bearer","expiresIn":3600}
```

The response fields are defined by `AuthResponse.java`: `accessToken` (string), `tokenType` (string), and `expiresIn` (long, seconds). With the current Auth Service implementation, `expiresIn` is `3600` seconds (one hour). Invalid credentials result in **401 Unauthorized**.

### Demo administrator

For local testing, set `DEMO_ADMIN_ENABLED=true`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD` in `.env` and pass these variables to Auth Service through Docker Compose. The initializer creates an `ADMIN` account if the email does not already exist. Sign in through the same `/api/v1/auth/login` endpoint as any other user. Registration always creates a `USER`, not an `ADMIN`. Do not publish real demo passwords in the repository.

## Books

| Method | Path | Purpose | Expected success |
| --- | --- | --- | --- |
| GET | `/api/v1/books` | List all books | 200 |
| GET | `/api/v1/books?search=Tolkien` | Search title or author | 200 |
| GET | `/api/v1/books/{id}` | Retrieve one book | 200; 404 if missing |
| POST | `/api/v1/books` | Create a book | 201 + `Location` header |
| PUT | `/api/v1/books/{id}` | Update a book | 200; 404 if missing |
| DELETE | `/api/v1/books/{id}` | Delete a book | 204; 404 if missing |

The read endpoints are public. `POST`, `PUT`, and `DELETE` book operations require a valid JWT with the `ADMIN` role, as enforced by `JwtSecurityConfig.java`. The React frontend exposes book creation, editing, and deletion controls to signed-in administrators; server-side authorization remains authoritative.

Example book request for create/update:

```json
{
  "title": "Example Book",
  "author": "Example Author",
  "genre": "Fiction",
  "description": "A sample book for API testing."
}
```

The exact input validation rules are defined in `BookRequest.java`.

Example response:

```json
{
  "id": 1,
  "title": "1984",
  "author": "George Orwell",
  "genre": "Dystopian",
  "description": "A dystopian novel about a totalitarian society."
}
```

## Reviews

| Method | Path | Purpose | Access | Expected success |
| --- | --- | --- | --- | --- |
| GET | `/api/v1/reviews?bookId=2` | List reviews for a book | Public | 200 |
| POST | `/api/v1/reviews` | Create a review | USER / ADMIN | 201 |
| DELETE | `/api/v1/reviews/{id}` | Delete a review | ADMIN | 204; 404 if missing |

Example create request:

```json
{
  "bookId": 2,
  "reviewerName": "Reader",
  "rating": 5,
  "comment": "A great read!"
}
```

Review Service checks the referenced book through Book Service before saving. A nonexistent book ID causes **400 Bad Request**. Input validation also applies; the `bookId` query parameter and review ID path parameter must be positive.

Example response:

```json
{
  "id": 1,
  "bookId": 2,
  "reviewerName": "Reader",
  "rating": 5,
  "comment": "A great read!"
}
```

## Admin book management example

After signing in with the configured demo administrator, copy the `accessToken` from the login response into a shell variable and use it to create a book:

```bash
TOKEN="paste-admin-access-token-here"
curl -i -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Demo Book","author":"Demo Author","genre":"Testing","description":"Created by an administrator."}'
```

A successful creation returns **201 Created** with a `Location` header. `PUT /api/v1/books/{id}` and `DELETE /api/v1/books/{id}` use the same admin authorization header. Ordinary `USER` tokens must not be able to modify books.

## Quick curl examples

List books:

```bash
curl -i http://localhost:8080/api/v1/books
```

Search books:

```bash
curl -G http://localhost:8080/api/v1/books --data-urlencode 'search=Tolkien'
```

List reviews:

```bash
curl 'http://localhost:8080/api/v1/reviews?bookId=2'
```

Register:

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"reader@example.com","password":"ChooseAStrongPassword123!"}'
```

Login:

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"reader@example.com","password":"ChooseAStrongPassword123!"}'
```

Create a review (replace `<token>`):

```bash
curl -i -X POST http://localhost:8080/api/v1/reviews \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"bookId":2,"reviewerName":"Reader","rating":5,"comment":"A great read!"}'
```

## Authorization outcomes

- **401 Unauthorized:** Missing, invalid, or expired token on a protected endpoint.
- **403 Forbidden:** Authenticated user lacks the required role.
- **404 Not Found:** Requested book or review does not exist for supported lookups/deletions.
- **400 Bad Request:** Invalid input or a review referencing a nonexistent book.
- **409 Conflict:** Attempt to register an email already in use.

Error bodies depend on Spring Boot's exception handling and may differ from the examples above.

**Validation note:** Exact input validation constraints are defined in the request DTOs; consult those classes for field-level requirements.
