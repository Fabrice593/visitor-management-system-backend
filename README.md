# Visitor Management System

Student: NIYOMUGABO Fabrice  
Student ID: 27115  
Repository name: visitor-management-system-backend

A Spring Boot backend using Spring Data JPA and PostgreSQL. Parts A to F and both bonus features are implemented.

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

Missing visits, hosts and purposes return their existing 404 messages. Where the supplied specification does not define wording, the implementation uses HTTP 200 with `Visit status updated successfully`, `Visit purpose added to the host employee` or `Workstation changed successfully`. An unknown station number returns HTTP 404 with `The workstation with that station number does not exist`. Because the model does not require station numbers to be unique, an ambiguous number returns HTTP 409 with `More than one workstation has that station number` and leaves the host unchanged.

Ordinary Visit PUT requests cannot change status. When a non-DENIED visit changes host or date, its new schedule is checked for past dates and conflicts, excluding its own record. An unchanged schedule does not conflict with itself. Host creation and ordinary HostEmployee updates also check workstation occupancy so they cannot bypass the move rule.

## Part E visit notes

`VisitNote` is stored in `visit_note` with a generated UUID, `badgeType`, `remarks`, `durationMinutes`, `loggedDate` and a many-to-one `visit` relationship using `visit_id`.

| ID | Endpoint | Behavior |
| --- | --- | --- |
| E1 | `POST /api/visit-notes/save?visitId=...` | Saves a note only when the visit is CHECKED_OUT. The service generates the ID, sets today's logged date and links the visit supplied by the query parameter. |
| E2 | `GET /api/visit-notes/visitor/{idNumber}` | Returns notes across all visits of that visitor, newest logged date first, using JPQL path navigation and `ORDER BY`. |
| E3 | `DELETE /api/visit-notes/delete/{id}` | Deletes a note. Visit deletion checks the derived `existsByVisit` method and rejects visits that still have notes. |

E1 returns 201 with `Visit note saved successfully`, 404 with `The visit with that id does not exist`, or 400 with `Visit notes are only for CHECKED_OUT visits`. Client-supplied note IDs, dates and visit relationships are overwritten by the service.

E2 returns HTTP 200 with an empty list when no notes match, including unknown visitor ID numbers. The JSON includes the note's visit, visitor and host employee without adding a note list to Visit. Notes with the same logged date have no specified order relative to each other.

E3 returns 200 with `Visit note deleted successfully` or 404 with `The visit note with that id does not exist`. A visit with notes returns 409 with `That visit has visit notes, delete them first`. Deleting one note does not remove its visit or other notes. Once the final note is deleted, the visit can be deleted.

## Part F reports and bulk updates

| ID | Endpoint | Behavior |
| --- | --- | --- |
| F1 | `GET /api/visits/stats/by-status` | Returns a `List<Object[]>` containing one `[status, count]` row for each status that has visits, using JPQL `GROUP BY` and `COUNT`. |
| F2 | `PATCH /api/visits/cancel-day?hostEmployeeId=...&date=2026-10-20` | Uses one JPQL UPDATE to set the host's visits on that date to DENIED, excluding CHECKED_OUT. |

Both endpoints return HTTP 200. F1 returns an empty list when there are no visits, omits statuses with zero visits and does not specify row order.

F2 uses `@Modifying` on the repository method and `@Transactional` on the service method. It returns the actual affected-row count as plain text, for example `3 visits cancelled` or `0 visits cancelled`. Since CHECKED_OUT is the only excluded status, already-DENIED visits also count as matched rows, including on repeated calls. Other hosts and dates remain unchanged. An unknown host UUID returns `0 visits cancelled`. Invalid UUIDs, invalid dates and missing parameters return HTTP 400 through Spring's default handling.

## Bonus features

`GET /api/dashboard/summary` returns HTTP 200 with a `Map<String, Long>` built using `LinkedHashMap` in DashboardService. Its keys appear in this order:

```json
{
  "visitors": 5,
  "hostEmployees": 3,
  "freeWorkstations": 1,
  "visitsToday": 0,
  "expected": 4,
  "visitNotes": 6
}
```

Counts use repository `count()` methods, derived `countByVisitDate` and `countByStatus`, and the existing C2 query for free workstations. `visitsToday` counts all visits dated today regardless of status. `expected` counts EXPECTED visits across all dates. An empty database returns all six keys with zero values.

`GET /api/visits/page?page=0&size=5&sort=visitDate,desc` returns HTTP 200 with a `Page<Visit>`, including `content`, `totalPages`, `totalElements`, `number` and `size`. The controller constructs `Pageable`; the service calls `findAll(pageable)`.

The defaults are page 0, size 5 and sort `visitDate,desc`. Page numbers start at zero. Sort accepts `id`, `visitDate`, `status` or `purposeNote` followed by `asc` or `desc`, ignoring the direction's case. Equal sort values use UUID ascending as a stable secondary order. A page beyond the end has empty content and retains the overall totals.

For pagination inputs not specified in the paper, negative pages and sizes below 1 return HTTP 400 with `Page must be 0 or more and size at least 1`. A sort without exactly two comma-separated parts returns `Sort must look like visitDate,desc`. Unsupported properties or directions return `Sort must use id, visitDate, status or purposeNote with asc or desc`. Non-numeric page or size values return HTTP 400 through Spring's default handling.

## Postman review

Import `visitor-management-system-midterm.postman_collection.json`. Run the `A2 - CRUD` folder in order against a disposable database. It creates related records, captures generated IDs through list requests, verifies reads and updates, tests validation and deletion protection, then deletes its records in dependency order.

Run the `Part B - Derived queries` folder in order to create its own fixtures, verify B1 to B3 and remove those fixtures. It checks company case handling, ordering, status filtering, inclusive date boundaries, single-day ranges and invalid inputs. Its empty-range check assumes no visit exists on 1900-01-01 in the disposable database.

Run the `Part C - JPQL queries` folder in order to create its fixtures, verify C1 to C4 and clean up. It checks partial case-insensitive matching, shared purposes, free workstation ordering, unused purposes, distinct visitors, host filtering and empty results. It also verifies that deleting hosts releases workstations and makes their remaining unlinked purposes unused.

Run the `Part D - Business rules` folder in order to test all 16 status transition pairs, forced initial status, past dates, host booking conflicts, ordinary update protection, purpose linking and workstation moves. It creates its own fixtures and removes them afterward. Use a disposable database with station numbers 901 to 903 available. The Part B fixture now changes its denied visit through the D2 endpoint instead of supplying the initial status.

Run the `Part E - Visit notes` folder in order to test all visit statuses, server-owned note fields, history across visits, visitor isolation and deletion protection while multiple notes remain. It creates its fixtures and removes them afterward. Notes created through the API on the same day share their logged date.

Run the `Part F - Reports and bulk updates` folder in order against a disposable database with no visits. It checks all four status groups, zero-group omission, the real cancellation count, repeated calls, checkout protection, host/date filtering and invalid inputs, then removes its fixtures.

Run the `Bonus - Dashboard and pagination` folder in order against an empty disposable database. It checks the six dashboard counts and their order, changes to those counts, default and explicit pagination, page boundaries, sorting and invalid parameters. It creates its own fixtures using station numbers 1001 to 1003 and removes them afterward.

The collection uses `baseUrl`, defaulting to `http://localhost:8080`. Test data contains a per-run identifier. The collection covers Parts A to F, both bonus features and the final-review edge cases. Each of the 18 required IDs has an exactly named request. A1 also requires the database inspection described below.

Review PostgreSQL tables for A1: `visit_purpose`, `workstation`, `host_employee`, `host_employee_visit_purpose`, `visitor`, and `visit`. Part E adds the `visit_note` table and its foreign key to `visit`.

## Structure

- `models`: entity fields and JPA relationships
- `repositories`: persistence and relationship checks
- `services`: CRUD logic and validation
- `controllers`: HTTP routes, response messages and statuses

Entities are used directly in request and response bodies. There are no DTOs or explicit try/catch blocks.

Controllers map service messages to an `HttpStatus` using if/else, then build one response for that operation. `VisitService` shares scheduling validation between creation and rescheduling. `HostEmployeeService` separates required fields, workstation resolution and visit-purpose resolution into focused methods. Validation order, API messages and repository query types remain unchanged.

## Requirement review

| ID | Implementation and verification |
| --- | --- |
| A1 | Five original entities plus VisitNote, generated UUIDs, exact fields, table and join mappings, string enum and JSON loop protection; checked in PostgreSQL and API responses |
| A2 | All 25 CRUD endpoints, success messages, missing-record handling and relationship validation |
| B1 | Derived `findByCompanyIgnoreCaseOrderByLastNameAsc` |
| B2 | Derived `findByStatusOrderByVisitDateAsc` |
| B3 | Derived `findByVisitDateBetweenOrderByVisitDateAsc`; String dates parsed in the controller |
| C1 | JPQL JOIN with case-insensitive partial purpose-name matching |
| C2 | JPQL NOT EXISTS with workstation-number ordering |
| C3 | JPQL IS EMPTY for unused purposes |
| C4 | JPQL DISTINCT for visitors across repeat visits to a host |
| D1 | Past-date rejection, derived non-DENIED conflict checks and forced EXPECTED status |
| D2 | All 16 status transition pairs and ordinary PUT status protection |
| D3 | Owning-side purpose append, preservation of existing links and duplicate rejection |
| D4 | Workstation lookup and occupancy checks, including safe handling of ambiguous numbers |
| E1 | CHECKED_OUT-only notes with server-generated ID, today's date and resolved visit |
| E2 | JPQL visitor path and descending logged-date order across visits |
| E3 | Note deletion and derived `existsByVisit` deletion protection |
| F1 | JPQL GROUP BY and COUNT returning `List<Object[]>` |
| F2 | One JPQL UPDATE with `@Modifying`, a transactional service and the real affected-row count |
| Bonus dashboard | DashboardService and DashboardController, ordered six-count LinkedHashMap, count methods and reused C2 query |
| Bonus pagination | Controller-built Pageable, Page<Visit> metadata, sorting, stable ties and input validation |

## Repeat the final checks

Use a separate empty PostgreSQL database, configure `DB_URL` to point to it and start the API. Import the collection into Postman and run the whole collection in folder order. The suite creates and removes its fixtures; it does not clear an existing database. The `Final review - Edge cases` folder checks ambiguous workstation numbers and pagination across equal visit dates.

If Node.js is installed, the same exported collection can be run with Postman's Newman runtime:

```powershell
npx --yes newman@6.2.3 run visitor-management-system-midterm.postman_collection.json --env-var baseUrl=http://localhost:8080
```

The application itself does not require Node.js. Maven uses the JDK selected by `JAVA_HOME`. On macOS or Linux, run the wrapper with `bash ./mvnw clean package` and supply the same database environment variables before starting the JAR.

Inspect PostgreSQL for seven tables: the six entity tables and `host_employee_visit_purpose`. Check six UUID primary keys, six foreign keys, the visitor ID number unique constraint and the Visit status enum check. Keep `ddl-auto=update` and `show-sql=true` enabled for the demo.

## Final verification

Reviewed against all 18 requirements and both bonuses on 29 October 2026:

- The documented Maven wrapper command, `mvnw.cmd -o clean package`, passed using JDK 23 and the cached dependencies. Application bytecode targets Java 17.
- On a newly created PostgreSQL 17 database, Newman 6.2.3 passed all 450 requests and 964 assertions with zero failures. This uses Postman's runtime; the Postman desktop application was not used for this verification.
- Schema inspection confirmed the exact seven tables and their fields, six UUID primary keys, six foreign keys, unique visitor ID numbers, string enum values and the nullable host workstation relationship.
- A separate E2 check verified descending order with three different logged dates across two visits.
- A separate F2 check confirmed one SQL UPDATE with no preceding SELECT, the actual affected-row count, and preservation of a checked-out visit and its note.
- New regression cases verified controlled conflicts for ambiguous station numbers and stable pagination across equal visit dates. All test fixtures were removed after verification.
- Application sources contain no comments, DTOs, explicit try/catch blocks or clinic-domain entity names. Derived and JPQL methods were checked against their required query types.

The local project is ready for final review. Public GitHub publication remains outstanding; no remote is configured in this checkout. No commits or pushes were performed during the final review.

## Submission steps after review

1. Review and commit the bonus features and final-review fixes yourself. Existing history already contains incremental commits for the earlier parts.
2. Create a public GitHub repository named `visitor-management-system-backend`, configure its remote and push the complete history. No Git remote is configured in this checkout yet; publication has not been performed or verified.
3. Confirm the public repository includes this README, the Maven wrapper, all source files and `visitor-management-system-midterm.postman_collection.json` at its root. The README identity above matches the supplied specification.
4. Run the collection in Postman for your own review and rehearse three random endpoints, including success and failure responses. Be ready to explain relationships, derived queries, JPQL, status rules and transaction boundaries.
