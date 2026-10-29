# Visitor Management System

Student: NIYOMUGABO Fabrice  
Student ID: 27115  
Repository name: visitor-management-system-backend

A Spring Boot backend using Spring Data JPA and PostgreSQL. Parts A and B are implemented. Parts C to F and the bonus features are pending.

## Requirements

- JDK 17 or newer
- PostgreSQL running locally or remotely
- Maven 3.6.3 or newer, or the included Maven wrapper
- Postman for reviewing the API

## Run the API

Create a PostgreSQL database named `visitor_management_system` using pgAdmin or:

```sql
CREATE DATABASE visitor_management_system;
```

Set the database credentials in the terminal where you will run the application. PowerShell example:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/visitor_management_system'
$env:DB_USERNAME = 'postgres'
$env:DB_PASSWORD = 'your-local-password'
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`. Hibernate updates the database schema automatically and prints SQL statements. Credentials are read from environment variables; do not commit passwords.

To build an executable JAR:

```powershell
.\mvnw.cmd clean package
java -jar target/visitor-management-system-0.0.1-SNAPSHOT.jar
```

## Part A endpoints

Each resource supports these operations:

| Method | Path | Success |
| --- | --- | --- |
| POST | `/api/{resource}/save` | 201, plain text success message |
| GET | `/api/{resource}/all` | 200, JSON array |
| GET | `/api/{resource}/{id}` | 200, JSON entity |
| PUT | `/api/{resource}/update/{id}` | 200, plain text success message |
| DELETE | `/api/{resource}/delete/{id}` | 200, plain text success message |

Resources: `visit-purposes`, `workstations`, `host-employees`, `visitors`, `visits`.

Missing records return 404. Missing required fields return 400. Duplicate visitor ID numbers and deletion of referenced records return 409. Messages not explicitly supplied in the specification follow the same entity-based wording as the supplied Visit examples.

Entities have JPA-generated UUIDs. Related records must exist before they are linked. Supply relationships using their IDs:

```json
{
  "firstName": "Alice",
  "lastName": "Uwase",
  "email": "alice@example.com",
  "workstation": { "id": "existing-workstation-uuid" },
  "visitPurposes": [{ "id": "existing-purpose-uuid" }]
}
```

Workstation is optional for a host employee. A visit purpose never serializes its host employee list. An ordinary Visit update preserves its stored status. The dedicated state transition and scheduling rules will be added in Part D.

## Part B endpoints

All three queries use Spring Data repository method names without `@Query` or JPQL. They return HTTP 200 and a JSON list, including an empty list when there are no matches.

| ID | Endpoint | Behavior |
| --- | --- | --- |
| B1 | `GET /api/visitors/by-company?company=Example` | Exact company match ignoring case, ordered by last name ascending |
| B2 | `GET /api/visits/by-status?status=EXPECTED` | Matches the status enum, ordered by visit date ascending |
| B3 | `GET /api/visits/between?start=2026-10-29&end=2026-11-05` | Includes both boundary dates, ordered by visit date ascending |

The B3 controller converts the two String parameters to `LocalDate`. For input errors not explicitly specified in the paper, reversed ranges return HTTP 400 with `End date must not be before start date`, and invalid dates return HTTP 400 with `Dates must use yyyy-MM-dd format`. Missing required parameters and invalid status values also return HTTP 400 through Spring's default handling.

## Postman review

Import `visitor-management-system-midterm.postman_collection.json`. Run the `A2 - CRUD` folder in order against a disposable database. It creates related records, captures generated IDs through list requests, verifies reads and updates, tests validation and deletion protection, then deletes its records in dependency order.

Run the `Part B - Derived queries` folder in order to create its own fixtures, verify B1 to B3 and remove those fixtures. It checks company case handling, ordering, status filtering, inclusive date boundaries, single-day ranges and invalid inputs. Its empty-range check assumes no visit exists on 1900-01-01 in the disposable database.

The collection uses `baseUrl`, defaulting to `http://localhost:8080`. Test data contains a per-run identifier. The collection covers Parts A and B; later requirement requests will be added with their respective parts.

Review PostgreSQL tables for A1: `visit_purpose`, `workstation`, `host_employee`, `host_employee_visit_purpose`, `visitor`, and `visit`. The `visit_note` table belongs to Part E.

## Structure

- `models`: entity fields and JPA relationships
- `repositories`: persistence and relationship checks
- `services`: CRUD logic and validation
- `controllers`: HTTP routes, response messages and statuses

Entities are used directly in request and response bodies. There are no DTOs or explicit try/catch blocks.

## Part A verification

Verified on 29 October 2026 using JDK 23 with Java 17 compilation and a disposable PostgreSQL 17 database:

- Maven package build passed.
- All 51 collection requests and 103 assertions passed through a local Node runner executing the collection scripts. The Postman desktop application was not used for this run.
- PostgreSQL schema inspection confirmed all six Part A tables, UUID primary keys, five foreign keys, the unique visitor ID number constraint, and string enum storage.

Import and run the collection in Postman for your review before committing Part A.

## Part B verification

Verified on 29 October 2026 against the disposable PostgreSQL 17 database:

- Maven package build passed.
- All 35 Part B requests and 78 assertions passed, including fixture setup and cleanup.
- The complete Parts A and B collection passed: 86 requests and 181 assertions through the local Node runner. The Postman desktop application was not used for this run.
- Repository inspection confirmed B1, B2 and B3 use derived method names only, without `@Query`.

Import the updated collection and run `Part B - Derived queries` in Postman for your review before committing Part B.
