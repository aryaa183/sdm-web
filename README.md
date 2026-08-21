# Medigrid — Smart Disaster Management System

A full-stack rebuild of a console-based disaster response simulator: region-based
hospital alerts, severity-triaged patient admission with cross-region bed
overflow, and treatment billing — now a REST API + React dashboard instead of
a terminal menu.

**Stack:** React 19 + TypeScript (Vite) · Spring Boot 3 + Java 21 · H2 (in-memory)

## Where this came from

The original project was a single-file Java console app (`newdisaster` package,
compiled as `sdm.jar`) with three classes — `DisasterManagement`, `Hospital`,
`Patient` — and a `main()` method driving a text menu: alert a region, enter
patients one at a time or in bulk, run admission, print bills.

This rebuild keeps every piece of that logic — same regions, same hospitals
and bed counts, same severity-priority-queue admission algorithm, same GST +
service-fee billing formula — but restructures it as a proper layered backend
(entities → repositories → services → controllers) behind a REST API, with a
React frontend on top.

## Project structure

```
sdm-web/
├── backend/                  Spring Boot API
│   └── src/main/java/com/aryaa/sdm/
│       ├── model/            Region, Hospital, Patient, Bill, Severity, PatientStatus
│       ├── repository/       Spring Data JPA repositories
│       ├── service/          RegionService, PatientIntakeService, AdmissionService, BillingService
│       ├── controller/       REST endpoints
│       ├── exception/        Global error handling
│       └── config/           CORS + startup data seeding
└── frontend/                 React + TypeScript (Vite)
    └── src/
        ├── api/               Typed fetch client
        ├── components/        StatusBar, SeverityBadge
        ├── pages/              NetworkPage, TriagePage, BillingPage
        └── types/              Shared DTO types
```

## Running it locally

**Backend** (needs Java 21 + Maven; pulls dependencies from Maven Central on first run):

```bash
cd backend
mvn spring-boot:run
```

Starts on `http://localhost:8080`. The five original regions (Dehradun,
Haridwar, Rishikesh, Nainital, Chamoli) and their hospitals are seeded fresh
into an in-memory H2 database every time it starts — nothing to configure.

**Frontend:**

```bash
cd frontend
npm install
cp .env.example .env    # points at http://localhost:8080 by default
npm run dev
```

Opens on `http://localhost:5173`.

> The frontend was built and type-checked (`tsc -b`, `vite build`) in this
> environment and compiles cleanly. The Java backend could **not** be compiled
> here — this sandbox has no access to Maven Central — so run `mvn spring-boot:run`
> locally before relying on it, and let me know if anything doesn't compile.

## API overview

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/regions` | List all regions with hospitals + connections |
| POST | `/api/regions/{name}/alert` | Alert a region + its connected regions |
| GET | `/api/patients?status=WAITING` | List patients, optionally by status |
| POST | `/api/patients` | Register a single patient |
| POST | `/api/patients/bulk` | Register several patients from a list of severities |
| POST | `/api/patients/admit?region={name}` | Run triage/admission for all waiting patients |
| POST | `/api/patients/{id}/bill` | Generate (or fetch) a patient's bill |

## What changed from the original, and why

- **Ints → enums.** Severity was a raw `int`; it's now a `Severity` enum
  (`LOW`/`MEDIUM`/`HIGH`) that carries its own billing cost, so the mapping
  from severity to price lives in one place instead of being re-derived
  wherever it was needed.
- **Console I/O → REST + persistence.** All state (regions, hospitals, patients,
  bills) now lives in a database instead of in-memory arrays that vanished
  when the program exited.
- **Bug fix in bulk patient naming.** The original bulk-entry naming scheme,
  ported naively, would have given every patient in one batch the same
  `Patient_N` name (all reads of "how many patients exist" happen before any
  of them are saved). Fixed by computing the starting number once and
  incrementing locally per patient in the batch.
- **Structured errors.** Invalid input, missing regions/patients, and
  double-billing an unadmitted patient now return proper HTTP status codes
  and JSON error bodies instead of stack traces or silent console output.
- **Tests.** `AdmissionServiceTest` and `BillingServiceTest` cover the two
  pieces of real logic in the app: severity-ordered triage with cross-region
  overflow, and the GST/service-fee billing math.

## Design notes (frontend)

The UI is themed as an operations console rather than a generic admin panel:
dark teal-tinted palette, monospace numeric readouts, and severity color-coding
(green/amber/red) used consistently everywhere a `Severity` value appears —
never as decoration. The Network page's region map is a live SVG built from
the actual API data (node = region, ring fill = bed occupancy, edges = the
real `connectedRegions` overflow graph) — clicking a node is how you actually
trigger an alert, not just an illustration.
