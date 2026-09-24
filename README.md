# Sistema Web Gimnasio PowerFit — Grupo 1

Sistema web para la gestión de un gimnasio: inscripciones, cursos, rutinas, máquinas e instructores. Proyecto del curso **Programación 4 (EIF209)**, Escuela de Informática, Universidad Nacional — II Ciclo 2026.

## Integrantes

| Nombre completo | Rol |
|---|---|
| Christopher Blanco Solano | Desarrollador |
| Alexander Dittel Escobar | Desarrollador |
| Aslehy Claret Aguilar Perez | Desarrollador |

## Descripción del sistema

El sistema permite a los clientes inscribirse a rutinas personalizadas o cursos grupales, consultar los cursos disponibles y enviar consultas al gimnasio; y al personal administrar la información de clientes, instructores, cursos, máquinas y rutinas.

| Capa | Tecnología | Carpeta |
|---|---|---|
| Base de datos | MySQL 8 | `database/` |
| Backend (API REST) | Java + Spring Boot, servidor Tomcat embebido (puerto 8080) | `backend/` |
| Frontend SPA | React + Vite (puerto 5173) | `frontend/src/` |
| Páginas del sitio (HTML5, CSS3 y JavaScript) | HTML/CSS + módulos ES6 servidos por Vite | `frontend/public/` |

Avance por entregable:
- **Entregable 1:** acta de constitución, ambiente de desarrollo y modelo de datos (`docs/`, `database/GIMNASIO.sql`).
- **Entregable 2:** páginas HTML5/CSS3 responsive: Inicio, Contactos, Servicios y Blog.
- **Entregable 3:** JavaScript del lado del cliente: validación de formularios, eventos y manipulación del DOM, `fetch` con `async/await` contra el backend, `localStorage` / `sessionStorage` y mensajes de éxito/error sin recargar la página.

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

## 5. Ver las páginas del sitio (Entregables 2 y 3)

Con el mismo servidor de Vite corriendo (`npm run dev`), las páginas se sirven directamente desde `frontend/public/`, sin pasar por React:
- http://localhost:5173/html/inicio.html
- http://localhost:5173/html/blog.html
- http://localhost:5173/html/contacto.html
- http://localhost:5173/html/servicios.html

Estructura:

| Carpeta | Contenido |
|---|---|
| `frontend/public/html/` | páginas HTML |
| `frontend/public/css/` | estilos (`global.css` + uno por página) |
| `frontend/public/assets/` | imágenes |
| `frontend/public/js/` | JavaScript del Entregable 3 (módulos ES6) |

Módulos JavaScript (`frontend/public/js/`):

| Archivo | Responsabilidad |
|---|---|
| `validaciones.js` | Validación de campos en el cliente (obligatorios, correo, teléfono, longitudes, patrones, fechas) con mensajes bajo cada campo |
| `api.js` | Llamadas `fetch` al backend con `async/await` y manejo de errores |
| `storage.js` | `localStorage` (preferencias, datos recordados) y `sessionStorage` (borradores, caché de cursos) |
| `formularios.js` | Utilidades de formularios: mensajes de éxito/error, creación de campos, mostrar/ocultar contraseña |
| `contacto.js` | Lógica de la página Contactos (formulario de consulta) |
| `servicios.js` | Lógica de la página Servicios (inscripción, cursos, campos dinámicos, formularios de mantenimiento) |

**Para que los formularios y la lista de cursos funcionen, el backend tiene que estar corriendo** (paso 3). Sin él, las páginas cargan igual, pero al enviar un formulario se muestra un mensaje de error de conexión y la sección de Cursos se queda con las tarjetas de ejemplo del HTML.

> **Nota:** si abrís una ruta que no existe (por ejemplo la vieja `/main.html` o `/inicio.html` sin `/html/`), Vite no da 404 sino que devuelve `index.html` y vas a ver la app de React en lugar de la página estática.

Estas páginas son independientes del SPA de React — conviven en el mismo repositorio pero no comparten código ni build.

## Endpoints disponibles

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/health` | Verifica que el backend esté activo. Responde `{"status":"ok"}` |
| GET | `/api/cursos` | Lista de cursos (descripción, horario, cupos, imagen) |
| POST | `/api/consultas` | Recibe el formulario de Contactos. `201` si es válido, `400` con los errores por campo si no |
| POST | `/api/inscripciones` | Recibe el formulario de Inscripción. `201` si es válido, `400` si hay datos inválidos, `409` si la cédula ya está inscrita |

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