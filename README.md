# Visitor Management System

Student: NIYOMUGABO Fabrice  
Student ID: 27115  
Repository name: visitor-management-system-backend

A Spring Boot backend using Spring Data JPA and PostgreSQL. Parts A to D are implemented. Parts E, F and the bonus features are pending.

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

Workstation is optional for a host employee. A visit purpose never serializes its host employee list. An ordinary Visit update preserves its stored status. Visit creation, status changes, purpose links and workstation assignments follow the Part D rules below.

## Part B endpoints

All three queries use Spring Data repository method names without `@Query` or JPQL. They return HTTP 200 and a JSON list, including an empty list when there are no matches.

| ID | Endpoint | Behavior |
| --- | --- | --- |
| B1 | `GET /api/visitors/by-company?company=Example` | Exact company match ignoring case, ordered by last name ascending |
| B2 | `GET /api/visits/by-status?status=EXPECTED` | Matches the status enum, ordered by visit date ascending |
| B3 | `GET /api/visits/between?start=2026-10-29&end=2026-11-05` | Includes both boundary dates, ordered by visit date ascending |

The B3 controller converts the two String parameters to `LocalDate`. For input errors not explicitly specified in the paper, reversed ranges return HTTP 400 with `End date must not be before start date`, and invalid dates return HTTP 400 with `Dates must use yyyy-MM-dd format`. Missing required parameters and invalid status values also return HTTP 400 through Spring's default handling.

## Part C endpoints

Each query uses JPQL with `@Query` and returns HTTP 200 with a JSON list.

| ID | Endpoint | JPQL behavior |
| --- | --- | --- |
| C1 | `GET /api/host-employees/by-visit-purpose?name=meeting` | Joins visit purposes and matches a case-insensitive part of the purpose name |
| C2 | `GET /api/workstations/free` | Uses `NOT EXISTS` to find unassigned workstations, ordered by station number ascending |
| C3 | `GET /api/visit-purposes/unused` | Uses `IS EMPTY` to find purposes with no host employees |
| C4 | `GET /api/visitors/of-host-employee/{hostEmployeeId}` | Uses `DISTINCT` to return each visitor once across repeat visits to the host |

Queries with no matches return an empty list. C4 also returns an empty list for an unknown host UUID because no visit matches it. Invalid UUIDs and a missing C1 name parameter return HTTP 400 through Spring's default handling.

## Part D business rules

| ID | Endpoint | Behavior |
| --- | --- | --- |
| D1 | `POST /api/visits/save` | Rejects past dates with 400 and an existing non-DENIED visit for the host on that date with 409. Always stores EXPECTED. Today is allowed. |
| D2 | `PATCH /api/visits/{id}/status?status=CHECKED_IN` | Applies the status transitions below. Invalid transitions return 400 with `Cannot change status from X to Y`. |
| D3 | `PUT /api/host-employees/{id}/visit-purposes/{purposeId}` | Adds the purpose without removing existing links. A duplicate returns 409. |
| D4 | `PATCH /api/host-employees/{id}/workstation?stationNumber=204` | Moves the host to an available workstation. A workstation occupied by another host returns 409. The same host may retain its current workstation. |

Allowed transitions:

- EXPECTED to CHECKED_IN or DENIED
- CHECKED_IN to CHECKED_OUT or DENIED
- CHECKED_OUT and DENIED have no outgoing transitions, including transitions to themselves

D1 errors are exactly `Visit date cannot be in the past` and `Host employee already receives a visitor on that date`. CHECKED_OUT visits still occupy their date; DENIED visits do not.

D3 duplicates return `The host employee already has that visit purpose`. D4 conflicts return `That workstation is taken by another host employee`.

Missing visits, hosts and purposes return their existing 404 messages. Where the supplied specification does not define wording, the implementation uses HTTP 200 with `Visit status updated successfully`, `Visit purpose added to the host employee` or `Workstation changed successfully`. An unknown station number returns HTTP 404 with `The workstation with that station number does not exist`.

Ordinary Visit PUT requests cannot change status. When a non-DENIED visit changes host or date, its new schedule is checked for past dates and conflicts, excluding its own record. An unchanged schedule does not conflict with itself. Host creation and ordinary HostEmployee updates also check workstation occupancy so they cannot bypass the move rule.

## Postman review

Import `visitor-management-system-midterm.postman_collection.json`. Run the `A2 - CRUD` folder in order against a disposable database. It creates related records, captures generated IDs through list requests, verifies reads and updates, tests validation and deletion protection, then deletes its records in dependency order.

Run the `Part B - Derived queries` folder in order to create its own fixtures, verify B1 to B3 and remove those fixtures. It checks company case handling, ordering, status filtering, inclusive date boundaries, single-day ranges and invalid inputs. Its empty-range check assumes no visit exists on 1900-01-01 in the disposable database.

Run the `Part C - JPQL queries` folder in order to create its fixtures, verify C1 to C4 and clean up. It checks partial case-insensitive matching, shared purposes, free workstation ordering, unused purposes, distinct visitors, host filtering and empty results. It also verifies that deleting hosts releases workstations and makes their remaining unlinked purposes unused.

Run the `Part D - Business rules` folder in order to test all 16 status transition pairs, forced initial status, past dates, host booking conflicts, ordinary update protection, purpose linking and workstation moves. It creates its own fixtures and removes them afterward. Use a disposable database with station numbers 901 to 903 available. The Part B fixture now changes its denied visit through the D2 endpoint instead of supplying the initial status.

The collection uses `baseUrl`, defaulting to `http://localhost:8080`. Test data contains a per-run identifier. The collection covers Parts A to D; later requirement requests will be added with their respective parts.

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

## Part C verification

Verified on 29 October 2026 against the disposable PostgreSQL 17 database:

- Maven package build passed.
- All 48 Part C requests and 107 assertions passed, including fixture setup and cleanup.
- The complete Parts A, B and C collection passed: 134 requests and 288 assertions through the local Node runner. The Postman desktop application was not used for this run.
- Repository inspection confirmed all four Part C methods use `@Query` with the required JPQL mechanisms: `JOIN`, `NOT EXISTS`, `IS EMPTY` and `DISTINCT`.

Import the updated collection and run `Part C - JPQL queries` in Postman for your review before committing Part C.

## Part D verification

Verified on 29 October 2026 against the disposable PostgreSQL 17 database:

- Maven package build passed.
- All 110 Part D requests and 221 assertions passed, including fixture setup and cleanup.
- The full Parts A to D collection passed: 245 requests and 511 assertions through the local Node runner. The Postman desktop application was not used for this run.
- All 16 combinations of current and requested status were tested, including persistence checks after rejected transitions.
- Checked forced EXPECTED status, today's date, past dates, all non-DENIED booking conflicts, replacement of a DENIED visit, purpose preservation, duplicate links, occupied workstations, failed update preservation and reuse of a released workstation.
- The Part B folder now has 36 requests because its DENIED fixture uses the new status endpoint.

Import the updated collection and run `Part D - Business rules` in Postman for your review before committing Part D.
