# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Sistema web para la gestión de un gimnasio (inscripciones, cursos, rutinas, máquinas e instructores). Coursework project for Programación 4 (EIF209), Universidad Nacional. Backend in Spring Boot, frontend SPA in React, MySQL database.

## Repository layout

```
backend/     # Spring Boot REST API (Maven, Java 26)
frontend/    # React + Vite SPA
database/    # GIMNASIO.sql — schema + seed data + analysis queries
docs/        # Course deliverables (charter, data model)
```

## Commands

### Backend (from `backend/`)
- Run: open in IntelliJ and run `GimnasioBackendApplication`, or `./mvnw spring-boot:run`
- Build: `./mvnw clean install`
- Test (all): `./mvnw test`
- Test (single class): `./mvnw test -Dtest=GimnasioBackendApplicationTests`
- Server listens on `http://localhost:8080`; health check at `GET /api/health` → `{"status":"ok"}`

### Frontend (from `frontend/`)
- Install: `npm install`
- Dev server: `npm run dev` (http://localhost:5173)
- Build: `npm run build`
- Lint: `npm run lint`
- Preview production build: `npm run preview`

### Database
- Bootstrap (creates DB, tables, seed data in one script): `mysql -u root -p < database/GIMNASIO.sql`
- Database name: `gimnasio_db`. Re-running the script drops and recreates all tables (`DROP DATABASE IF EXISTS gimnasio_db`).
- `database/GIMNASIO.sql` also contains a set of analysis queries (sections a–o) demonstrating joins/subqueries/INTERSECT/EXCEPT over the schema — these are course exercises, not application code.

## Architecture

### Backend
- Layered Spring Boot app under `com.grupo1.gimnasio.gimnasio_backend`: `controllers/` (REST) → `services/` interfaces + `services/impl/` → `repositories/` (`JpaRepository`) → `models/` (JPA entities mirroring `database/GIMNASIO.sql`). `dto/` holds API records with Bean Validation annotations; `exceptions/` holds `ApiException` subclasses (each carries its HTTP status) and the central `@RestControllerAdvice` (`ManejadorGlobalExcepciones`).
- Course-required design patterns are named in code comments: Singleton (`sesion/GestorSesiones`, `@Scope(SINGLETON)` services), Observer (`observer/PublicadorInscripciones` notifies `ObservadorInscripciones` beans on inscription/unsubscription), Iterator (`listar()` in `CursoServiceImpl` / `InscripcionServiceImpl`).
- The `/api/inscripciones` API stores into `cliente` + `credenciales`: the full name is split into `nombre`/`apellido_1`/`apellido_2`, the phone goes to `celular`, and passwords are stored only as PBKDF2 hash+salt (`HashContrasenas`). Login uses `HttpSession`.
- Schema changes go in `database/GIMNASIO.sql` (section h holds the web-app columns/tables, added after the seed inserts because those inserts are positional). Keep entity `@Column` lengths/types in sync with the script: with `ddl-auto=update` Hibernate will ALTER columns that differ.
- `spring.jpa.hibernate.ddl-auto=update` in `application.properties` — Hibernate will alter the schema to match entities on startup, so entity field types/constraints should stay consistent with the SQL script to avoid drift.
- DB credentials are read from `backend/src/main/resources/application.properties` (local MySQL, currently `root`/`root`) — not committed with real secrets, but do not hardcode credentials elsewhere.

### Database schema
Core tables and relationships (see `database/GIMNASIO.sql` for full DDL):
- `cliente` (PK `cedula`) and `instructores` (PK `cod_instructor`) are independent entities.
- `rutinas` links `cliente` + `instructores` + `maquinas` (a client doing a routine on a machine with an instructor).
- `historial_curso` links `cliente` + `instructores` + `cursos` (a client's course history with an instructor).
- `Curso_cliente` / `Rutina_cliente` are derived tables (created via `CREATE TABLE ... AS SELECT`) for course exercises, not part of the core relational model.

### Frontend
- `frontend/src/` is a minimal Vite + React SPA (`App.jsx`, entry `main.jsx`); currently just a placeholder page — this is where the real application UI (backed by the Spring Boot API) is meant to grow.
- `frontend/public/html/*.html` (`inicio.html`, `servicios.html`, `contacto.html`, `blog.html`), their stylesheets in `frontend/public/css/`, images in `frontend/public/assets/` and JS modules in `frontend/public/js/` are the multi-page site ("Gimnasio PowerFit") from Entregables 2–3. They are not part of the React app or Vite's build/routing — don't assume changes there affect the SPA, and don't import them from React components. They are served by the Vite dev server at `http://localhost:5173/html/<page>.html`; a wrong path (e.g. the old `/main.html`) silently falls back to `index.html` and shows the React app instead of a 404.
- The React app has no router or API client yet. The pages in `frontend/public/html/` do call the backend through the ES6 modules in `frontend/public/js/` (`api.js` uses `fetch` with `credentials: 'include'` for the session cookie).

## Workflow

- `main` is protected. All changes go through a Pull Request with at least one review from another team member — no direct commits to `main`.
  ```bash
  git checkout -b nombre-de-la-rama
  git add .
  git commit -m "Descripción del cambio"
  git push origin nombre-de-la-rama
  # Open PR on GitHub and request review
  ```