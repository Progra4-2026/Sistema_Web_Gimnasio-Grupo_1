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
- Standard Spring Boot layout under `com.grupo1.gimnasio.gimnasio_backend`, with a `controllers` package for `@RestController` classes (currently just `HealthController`). No service/repository/entity layers exist yet — when adding JPA entities for `cliente`, `instructores`, `cursos`, `maquinas`, `rutinas`, `historial_curso`, mirror the schema in `database/GIMNASIO.sql`.
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
- `frontend/public/*.html` (`main.html`, `servicios.html`, `contacto.html`, `blog.html`) plus their matching `*.css` and `global.css` are a **separate static HTML/CSS mockup** of the site ("Gimnasio PowerFit"), not wired into the React app or Vite's build/routing. Treat them as design reference/prototype pages, not live application code — don't assume changes there affect the SPA, and don't import them from React components.
- No client-side router or API client is set up yet; there are no calls from the React app to the backend API.

## Workflow

- `main` is protected. All changes go through a Pull Request with at least one review from another team member — no direct commits to `main`.
  ```bash
  git checkout -b nombre-de-la-rama
  git add .
  git commit -m "Descripción del cambio"
  git push origin nombre-de-la-rama
  # Open PR on GitHub and request review
  ```