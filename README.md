# Surgical Day

## About the project

Surgical Day replaces a shared night-before spreadsheet with a planning and case-lifecycle system for a hospital group that runs four operating theatres across two buildings.

The system starts when a coordinator books a case for a surgery date and ends when the patient is discharged and the final bill is available. It is designed to prevent the operational failures that commonly occur in a manual schedule:

- two operations booked for the same surgeon at overlapping times;
- a procedure assigned to a theatre without the required equipment; and
- recovery capacity being exceeded when several patients leave theatre together.

The application includes seeded reference data, a React scheduling interface, a Spring Boot API, recovery occupancy projections, case status tracking, and discharge billing.

Project structure:

```text
Surgical Day/
  BE/                 Spring Boot backend and REST API
  FE/                 React and Vite frontend
  docker-compose.yml  PostgreSQL, backend, and frontend services
  README.md           Project documentation
```

## Tech stack

- **Backend:** Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA, Bean Validation, Lombok
- **Database:** PostgreSQL 16
- **Frontend:** React 19 and Vite
- **API documentation:** springdoc OpenAPI and Swagger UI
- **Containers:** Docker and Docker Compose
- **Build tools:** Maven for the backend and npm for the frontend

## Prerequisites

For the recommended Docker setup:

- Docker Desktop with Docker Compose
- At least 4 GB of available memory for the containers

For the local setup:

- JDK 17 or later
- Maven 3.8 or later, or use the Maven wrapper if one is added later
- Node.js 18 or later with npm
- PostgreSQL 16, either installed locally or started with Docker

## Recommended: run everything with Docker

From the repository root, run one command:

```powershell
docker compose up --build
```

This builds and starts all three services:

- PostgreSQL on port `5432`
- Spring Boot backend on port `8088`
- React frontend served by Nginx on port `43123`

Open the application at http://127.0.0.1:43123.

Stop the running stack with `Ctrl+C`, or from another terminal run:

```powershell
docker compose down
```

The database uses a named Docker volume so data survives container restarts. To stop the stack and remove the local database data as well:

```powershell
docker compose down -v
```

Compose reads the local database credentials from `.env`, which is ignored by Git. Copy `.env.example` to `.env` and replace the placeholder password before starting the stack. Do not use local development credentials for a shared or production deployment.

## Alternative: run locally

### 1. Start PostgreSQL

You can start only the database with Docker from the repository root:

```powershell
docker compose up -d postgres
```

The local Spring configuration defaults to the Compose database at `localhost:5432`. For a different database, set private values in your local PowerShell session without committing them:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/your_database"
$env:SPRING_DATASOURCE_USERNAME = "your_user"
$env:SPRING_DATASOURCE_PASSWORD = "your_password"
```

### 2. Start the backend

```powershell
cd BE
mvn spring-boot:run
```

The backend runs at http://localhost:8088.

### 3. Start the frontend

In a second terminal:

```powershell
cd FE
npm install
npm run dev
```

The Vite frontend runs at http://127.0.0.1:43123 and proxies `/api` requests to the backend.

## Swagger and OpenAPI

Swagger is the quickest way to explore and demonstrate the backend without writing a client request by hand.

- **Swagger UI:** http://localhost:8088/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8088/v3/api-docs

In Swagger UI, use the reference-data endpoints first to discover the IDs for buildings, theatres, surgeons, procedures, and patients. Then use `POST /api/cases` to book a case, `PATCH /api/cases/{id}/status` to progress it, and `GET /api/cases/{id}/bill` after discharge.

## What the system checks when booking

When `POST /api/cases` is called, the backend validates:

1. **Surgeon availability:** the same surgeon cannot have overlapping cases on the same surgery date. Discharged cases do not block a new booking.
2. **Theatre equipment:** the selected theatre must contain every equipment item required by the selected procedure.
3. **Recovery capacity:** the projected recovery window is checked in 15-minute intervals. A booking is rejected when projected occupancy reaches the configured recovery-bed capacity.
4. **Referenced resources:** the patient, procedure, theatre, and surgeon must all exist.
5. **Request values:** required fields are validated, and a custom duration cannot be less than 15 minutes.

The seeded environment contains two buildings, four theatres, three recovery beds, four surgeons, four procedure types, and six patients.

## Case lifecycle

Cases move through the following statuses in order:

```text
SCHEDULED -> IN_THEATRE -> IN_RECOVERY -> DISCHARGED
```

- `SCHEDULED`: the case is planned but has not started.
- `IN_THEATRE`: the patient is undergoing the procedure.
- `IN_RECOVERY`: the procedure is complete and the patient occupies recovery capacity.
- `DISCHARGED`: the patient has left recovery and the bill is available.

At discharge, the bill contains the procedure base fee, theatre charge, recovery-bed charge, and the total amount.

## Demo script

Use tomorrow's date. Start the application with Docker, open Swagger UI or the frontend, and use the seeded reference data.

### A. Discover reference IDs

Call these endpoints first:

```text
GET /api/surgeons
GET /api/procedures
GET /api/theatres
GET /api/patients
GET /api/recovery-beds
```

On a fresh seeded database, the records are named as follows:

- Surgeon: Dr. Anita Rao
- Procedure: Laparoscopic Cholecystectomy
- Procedure: Knee Arthroscopy
- Theatre: OT-1
- Theatre: OT-4

Use the IDs returned by the API rather than assuming database IDs.

### B. Book a valid case

Send `POST /api/cases` with the IDs from the reference responses:

```json
{
  "surgeryDate": "2026-09-08",
  "startTime": "08:00",
  "patientId": 1,
  "procedureId": 1,
  "theatreId": 1,
  "surgeonId": 1
}
```

Replace the date and IDs when using a different database. The case should be created with status `SCHEDULED`.

### C. Demonstrate the scheduling rules

1. Book the same surgeon again at `08:30` while the first case is scheduled. The request is rejected because the surgeon's cases overlap.
2. Book Knee Arthroscopy in `OT-4`. The request is rejected because `OT-4` does not have `ORTHO_DRILL` and `C_ARM`.
3. Create three cases whose projected recovery windows overlap, then attempt a fourth overlapping case. The fourth request is rejected when the three recovery beds are projected to be full.
4. Check occupancy directly with `GET /api/recovery/occupancy?date=YYYY-MM-DD&at=14:00`.

### D. Follow a case to billing

For the ID returned by the valid booking:

```http
PATCH /api/cases/{id}/status
Content-Type: application/json

{"status":"IN_THEATRE"}
```

Then repeat the request with `IN_RECOVERY`, and finally with `DISCHARGED`. Retrieve the bill:

```text
GET /api/cases/{id}/bill
```

The bill is available only after the case reaches `DISCHARGED`.

## API structure

All application endpoints are under `/api`:

| Method | Endpoint                                           | Purpose                               |
| ------ | -------------------------------------------------- | ------------------------------------- |
| GET    | `/api/buildings`                                   | List buildings                        |
| GET    | `/api/theatres`                                    | List theatres and equipment           |
| GET    | `/api/surgeons`                                    | List surgeons                         |
| GET    | `/api/procedures`                                  | List procedure types and requirements |
| GET    | `/api/patients`                                    | List patients                         |
| GET    | `/api/recovery-beds`                               | List recovery beds                    |
| GET    | `/api/cases?date=YYYY-MM-DD`                       | List cases for a surgery date         |
| POST   | `/api/cases`                                       | Book a case with schedule validation  |
| PATCH  | `/api/cases/{id}/status`                           | Advance a case through its lifecycle  |
| GET    | `/api/cases/{id}/bill`                             | Retrieve the discharge bill           |
| GET    | `/api/recovery/occupancy?date=YYYY-MM-DD&at=HH:mm` | View projected recovery occupancy     |

Errors are returned as JSON by the global exception handler, including validation errors, missing resources, and schedule conflicts.

## Data and security notes

- This project is a local development/demo application. Authentication and authorization are not included.
- Do not use real patient information or production database credentials.
- The Compose database credentials are development defaults only; replace them before any shared deployment.
- Keep private environment variables out of `README.md`, source files, screenshots, logs, and commits.
- The database volume persists between runs. Use `docker compose down -v` only when you intentionally want to delete local seeded data.
- The backend uses JPA schema updates for convenience in this demo. A production deployment should use controlled database migrations and stronger operational security.
- Use npm for the frontend; pnpm is not required.
