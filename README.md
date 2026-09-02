# Sistema Web Gimnasio - Grupo 1

Proyecto para el curso **Programación 4 (EIF209)**, Escuela de Informática, Universidad Nacional.
Profesora: Marianella Solano Orias.

## Integrantes

| Nombre completo | Rol |
|---|--|
| Christopher Blanco Solano | Desarrollador |
| Alexander Dittel Escobar | Desarrollador |
| Aslehy Claret Aguilar Perez | Desarrollador |

## Descripción

Sistema web para la gestión de un gimnasio (inscripciones, cursos, rutinas, máquinas e instructores), desarrollado con backend en **Spring Boot** y frontend **SPA en React**.

## Tecnologías utilizadas

- **Backend:** Java 26, Spring Boot, Spring Data JPA, Maven
- **Base de datos:** MySQL 9.6
- **Frontend:** React + Vite
- **Control de versiones:** Git / GitHub

## Estructura del repositorio

```
Sistema_Web_Gimnasio-Grupo_1/
├── backend/     # Proyecto Spring Boot (API REST)
├── frontend/    # Proyecto React (SPA)
└── database/
    └── GIMNASIO.sql   # Script de creación de tablas y datos de prueba
```

## Requisitos previos

Antes de clonar, asegurate de tener instalado:

- **JDK 26** ([Homebrew](https://brew.sh): `brew install openjdk@26` en Mac, o descarga desde [Oracle/OpenJDK](https://jdk.java.net/26/))
- **IntelliJ IDEA** (Community o Ultimate)
- **Node.js** v20 o superior (incluye npm) — [nodejs.org](https://nodejs.org)
- **MySQL** 8 o superior, corriendo localmente
- **Git**

## 1. Clonar el repositorio

```bash
git clone https://github.com/Progra4-2026/Sistema_Web_Gimnasio-Grupo_1.git
cd Sistema_Web_Gimnasio-Grupo_1
```

## 2. Levantar la base de datos (MySQL)

Con el servidor MySQL corriendo, ejecutá el script — crea la base de datos, las tablas y carga los datos de prueba en un solo paso:

```bash
mysql -u root -p < database/GIMNASIO.sql
```

Verificá que las tablas se crearon correctamente:

```bash
mysql -u root -p gimnasio_db -e "SHOW TABLES;"
```

Deberías ver: `cliente`, `cursos`, `Curso_cliente`, `historial_curso`, `instructores`, `maquinas`, `rutinas`, `Rutina_cliente`.

## 3. Levantar el backend (Spring Boot)

1. Abrí la carpeta `backend/` con IntelliJ (`File → Open`).
2. Verificá/editá `backend/src/main/resources/application.properties` con las credenciales de tu MySQL local:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/gimnasio_db
   spring.datasource.username=root
   spring.datasource.password=TU_PASSWORD
   ```

3. Dejá que IntelliJ descargue las dependencias de Maven (barra de progreso inferior).
4. Ejecutá la clase principal `GimnasioBackendApplication.java` (botón ▶️ o `Run`).
5. El servidor queda escuchando en `http://localhost:8080`.

**Verificación:** abrí `http://localhost:8080/api/health` en el navegador. Debe responder:

```json
{"status":"ok"}
```

El backend usa Spring Boot con **Tomcat embebido** como servidor web — no requiere XAMPP ni configuración adicional de Apache; IntelliJ/Maven levantan el servidor al ejecutar la aplicación.

## 4. Levantar el frontend (React + Vite)

En una terminal aparte:

```bash
cd frontend
npm install
npm run dev
```

Abrí en el navegador la URL que indique la terminal (por defecto `http://localhost:5173`).

Deberías ver el componente principal mostrando **"Sistema Web Gimnasio - Grupo 1"**.

## Endpoints disponibles

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/health` | Verifica que el backend esté activo. Responde `{"status":"ok"}` |

## Flujo de trabajo en GitHub

Este repositorio usa la rama `main` protegida. Todo cambio se integra mediante **Pull Request** con al menos una revisión de otro integrante antes de mergear — no se permiten commits directos a `main`.

```bash
git checkout -b nombre-de-la-rama
# ... cambios ...
git add .
git commit -m "Descripción del cambio"
git push origin nombre-de-la-rama
# Abrir Pull Request en GitHub y solicitar revisión
```

## Repositorio

[https://github.com/Progra4-2026/Sistema_Web_Gimnasio-Grupo_1](https://github.com/Progra4-2026/Sistema_Web_Gimnasio-Grupo_1)
