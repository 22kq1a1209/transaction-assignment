# Customer Transactions API

A Spring Boot REST API for creating, retrieving, and managing customer transactions. It uses an embedded H2 database, so no separate database installation is required.

## Features

- Create payment and refund transactions
- Retrieve a transaction by its ID
- Update a transaction's status
- List all transactions for a customer
- Validate input, prevent duplicate IDs, and return clear errors

## Technology

- Java 17
- Spring Boot 3.5.5
- Spring Web and Spring Data JPA
- H2 in-memory database
- Maven Wrapper

## Prerequisites

Install **Java 17**. Maven is included through the Maven Wrapper.

```powershell
java -version
```

## Run the application

From the project folder, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`.

Run the test suite with:

```powershell
.\mvnw.cmd clean test
```

## API reference

### Create a transaction

`POST /api/transactions`

New transactions always start with the `PENDING` status.

```json
{
  "transactionId": "TXN-1001",
  "customerId": "CUST-2001",
  "amount": 1250.50,
  "currency": "INR",
  "transactionType": "PAYMENT"
}
```

```powershell
$body = @{
  transactionId = "TXN-1001"
  customerId = "CUST-2001"
  amount = 1250.50
  currency = "INR"
  transactionType = "PAYMENT"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/transactions" -ContentType "application/json" -Body $body
```

Returns `201 Created`.

### Get a transaction

`GET /api/transactions/{transactionId}`

```powershell
Invoke-RestMethod "http://localhost:8080/api/transactions/TXN-1001"
```

Returns `200 OK`.

### Update a transaction status

`PATCH /api/transactions/{transactionId}/status`

```json
{
  "status": "COMPLETED"
}
```

```powershell
Invoke-RestMethod -Method Patch -Uri "http://localhost:8080/api/transactions/TXN-1001/status" -ContentType "application/json" -Body '{"status":"COMPLETED"}'
```

Returns `200 OK`.

### Get a customer's transactions

`GET /api/customers/{customerId}/transactions`

```powershell
Invoke-RestMethod "http://localhost:8080/api/customers/CUST-2001/transactions"
```

Returns `200 OK`; an unknown customer returns an empty list (`[]`).

## Allowed values

| Field | Values |
| --- | --- |
| `transactionType` | `PAYMENT`, `REFUND` |
| `status` | `PENDING`, `COMPLETED`, `FAILED`, `CANCELLED` |

## Validation and business rules

### Creating a transaction

- `transactionId` must be present and unique.
- `customerId` must be present.
- `amount` must be greater than zero.
- `currency` must be present.
- `transactionType` is required.
- The application assigns the initial `PENDING` status.

### Changing status

- Only a `PENDING` transaction can be updated.
- It may move to `COMPLETED`, `FAILED`, or `CANCELLED`.
- A transaction in a final status cannot be changed again.

## Error responses

Errors use this format:

```json
{
  "timestamp": "2026-09-01T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be greater than zero"
}
```

| Situation | Status |
| --- | --- |
| Invalid or missing transaction data | `400 Bad Request` |
| Duplicate transaction ID | `409 Conflict` |
| Transaction not found | `404 Not Found` |

## H2 database console

When the application is running, visit `http://localhost:8080/h2-console`.

| Setting | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:transactions` |
| User Name | `sa` |
| Password | Leave empty |

The database is in memory, so its data is cleared when the application stops.

## Project structure

```text
src/main/java/com/example/transactionstarter/
├── controller/  # REST endpoints
├── service/     # Business rules and validation
├── repository/  # Database access
├── entity/      # Transaction model and enums
├── DTO/         # Request and response models
└── exception/   # Error handling
```

 
