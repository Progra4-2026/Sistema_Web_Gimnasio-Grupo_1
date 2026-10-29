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
- **Entregable 4:** backend con Spring Boot + JPA/Hibernate: entidades, repositorios, servicios, sesiones HTTP, servicios REST y SOAP, patrones Singleton, Observer e Iterator, validación y manejo central de errores.
- **Entregable 5:** SPA en React con React Router, componentes reutilizables, Context, capas `components/`, `services/` y `models/`, y guard de rutas.
- **Entregable 6 (entrega final):** login para los tres roles, contraseñas con BCrypt, autorización por roles, CORS, cabeceras de seguridad, documentación técnica y manual de usuario.

## Documentación

| Documento | Ubicación |
|---|---|
| Documentación técnica (arquitectura, componentes, despliegue, casos de uso, instalación) | [`docs/Documentacion_Tecnica.pdf`](docs/Documentacion_Tecnica.pdf) |
| Manual de usuario (capturas de cada vista y pasos de las operaciones principales) | [`docs/Manual_Usuario.pdf`](docs/Manual_Usuario.pdf) |
| Servicio SOAP (WSDL, XSD y ejemplos) | [`docs/soap/`](docs/soap/README.md) |
| Script de la base de datos (tablas y datos de prueba) | [`database/GIMNASIO.sql`](database/GIMNASIO.sql) |

Las fuentes HTML de los PDF están en `docs/documentacion/`.

## Requisitos previos

Antes de clonar, asegurate de tener instalado:

- **JDK 26** ([Homebrew](https://brew.sh): `brew install openjdk@26` en Mac, o descarga desde [Oracle/OpenJDK](https://jdk.java.net/26/))
- **IntelliJ IDEA** (Community o Ultimate)
- **Node.js** v20 o superior (incluye npm) — [nodejs.org](https://nodejs.org)
- **MySQL** 8 o superior, corriendo localmente
- **Git**

## Despliegue paso a paso

El sistema tiene tres partes que se levantan **en este orden**: base de datos → backend → frontend. Cada una corre en su propio puerto:

| Parte | Puerto | Se levanta con |
|---|---|---|
| MySQL | 3306 | el servicio de MySQL instalado |
| Backend (Spring Boot) | 8080 | IntelliJ o `./mvnw spring-boot:run` |
| Frontend (Vite / Node.js) | 5173 | `npm run dev` |

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

Deberías ver: `administradores`, `cliente`, `consultas`, `credenciales`, `cursos`, `historial_curso`, `instructores`, `maquinas`, `parametros_sistema`, `rutinas`.

> **Si ya tenías la base creada de un entregable anterior**, volvé a ejecutar el script: el Entregable 6 agrega `administradores`, `parametros_sistema` y la columna `password_hash` de `instructores`.

## 3. Levantar el backend (Spring Boot)

1. Abrí la carpeta `backend/` con IntelliJ (`File → Open`).
2. Configurá las credenciales de tu MySQL local. **No edites `application.properties`**: el backend las lee de un archivo `backend/.env`, que no se sube al repositorio.
   - Si tu MySQL usa usuario `root` con contraseña `root`, no tenés que hacer nada: son los valores por defecto.
   - Si no, copiá la plantilla y poné tus datos:

```bash
cp backend/.env.example backend/.env
```

   Contenido de `backend/.env`:

```
DB_URL=jdbc:mysql://localhost:3306/gimnasio_db
DB_USER=root
DB_PASSWORD=tu_contraseña_de_mysql
```

   > El `.env` tiene que estar dentro de `backend/`, porque el backend lo busca en la carpeta desde donde se ejecuta. Por eso en el paso 1 se abre `backend/` en IntelliJ, no la raíz del proyecto.

3. Dejá que IntelliJ descargue las dependencias de Maven (barra de progreso inferior).
4. Ejecutá la clase principal `GimnasioBackendApplication.java` (botón ▶️ o `Run`).
   - Sin IntelliJ, desde una terminal en `backend/`: `./mvnw spring-boot:run` (en Windows: `mvnw.cmd spring-boot:run`).
5. El servidor queda escuchando en `http://localhost:8080`. Al arrancar, completa las contraseñas de los usuarios de prueba (ver [Login y roles](#login-y-roles-entregable-6)).

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

Deberías ver la página de **Inicio** de Gimnasio PowerFit. Toda la aplicación es una SPA de React: las secciones (Servicios, Contactos, Blog, Mantenimiento…) cambian sin recargar la página.

## 5. Verificar que todo funciona

1. En `http://localhost:5173`, entrá a **Servicios → Cursos**: tiene que aparecer la lista de cursos (viene del backend y de MySQL).
2. Presioná **Ingresar** e iniciá sesión como administrador: usuario `1`, contraseña `admin123`. Tenés que llegar a **Mantenimiento**.
3. Cerrá sesión e ingresá como instructor (`100` / `100`) → **Panel instructor**, y como cliente (`201` / `201`) → **Mis rutinas**.

El paso a paso de cada operación, con capturas, está en el [manual de usuario](docs/Manual_Usuario.pdf).

### Problemas frecuentes

| Problema | Solución |
|---|---|
| El backend no arranca: `Access denied for user 'root'` | La contraseña de MySQL no es `root`: creá `backend/.env` (paso 3.2). |
| El backend no arranca: `Unknown database 'gimnasio_db'` o falta una tabla | Ejecutá `database/GIMNASIO.sql` (paso 2). Si venías de un entregable anterior, ejecutalo de nuevo. |
| `Port 8080 was already in use` o `Port 5173 is in use` | Ya hay otra instancia corriendo: cerrala o detené el proceso que usa ese puerto. |
| La página muestra "No se pudo conectar con el servidor" | El backend no está corriendo o no terminó de arrancar (revisá `http://localhost:8080/api/health`). |
| El login falla con un usuario de prueba | Reiniciá el backend: al arrancar completa las contraseñas de prueba que falten. |
| `npm run dev` falla con módulos faltantes | Ejecutá `npm install` dentro de `frontend/`. |

## 6. Despliegue para producción (opcional)

En lugar de los servidores de desarrollo, se pueden generar los artefactos finales:

```bash
cd backend && ./mvnw clean package && java -jar target/*.jar
```

```bash
cd frontend && npm run build
```

`npm run build` deja el frontend compilado en `frontend/dist/`, que puede servir cualquier servidor web (por ejemplo Apache de XAMPP o `npm run preview`). El servidor debe redirigir las rutas de la SPA a `index.html`, y la URL del frontend tiene que estar en `app.cors.origenes-permitidos` (`backend/src/main/resources/application.properties`). En producción el sitio debe publicarse con HTTPS: así Spring Security envía también la cabecera `Strict-Transport-Security`.

## Anexo: páginas HTML de los Entregables 2 y 3

Las páginas estáticas de los primeros entregables se conservan como referencia. No forman parte de la SPA. Con el servidor de Vite corriendo (`npm run dev`), se sirven directamente desde `frontend/public/`:
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

## Login y roles (Entregable 6)

El sistema tiene 3 roles. Todos inician sesión en `/login` con el mismo formulario; la contraseña se guarda cifrada con **BCrypt** (hash + salt, nunca texto plano):

| Rol | Usuario | Dónde está su contraseña | Qué puede hacer |
|---|---|---|---|
| Cliente | su cédula | `credenciales.password_hash` | Ver **solo sus propias rutinas** (Servicios → Rutinas), ver/editar/eliminar solo su inscripción |
| Instructor | su código | `instructores.password_hash` | Gestionar **máquinas y rutinas** (Panel instructor, `/instructor`) |
| Administrador | su código | `administradores.password_hash` | Gestionar **usuarios** (clientes, instructores, administradores), cursos y **parámetros del sistema** (Mantenimiento, `/mantenimiento`) |

Datos de prueba: al arrancar, `config/CargaInicialDatosRunner` completa las contraseñas que falten (nunca reemplaza una existente) y crea un administrador si no hay ninguno:

| Rol | Usuario | Contraseña |
|---|---|---|
| Cliente | `200` (o cualquier cédula de `GIMNASIO.sql`) | `200` (su propia cédula) |
| Instructor | `100` (o cualquier código de `GIMNASIO.sql`) | `100` (su propio código) |
| Administrador | `1` | `admin123` |

La autorización se verifica en el **backend** (Spring Security: reglas por URL en `SecurityConfig` + `@PreAuthorize` por rol en cada controlador) y el frontend solo oculta lo que el rol no puede usar (`RutaPrivada` con `roles`). Sin sesión la API responde `401`; con sesión pero sin el rol, `403`.

## Seguridad (Entregable 6)

- **Autenticación:** sesión HTTP (`HttpSession` + cookie `JSESSIONID` HttpOnly, 30 min de inactividad). Al iniciar sesión se renueva el id de sesión (evita fijación de sesión) y se guarda el `SecurityContext` con el rol.
- **Contraseñas:** `BCryptPasswordEncoder`. El mismo mensaje "Usuario o contraseña incorrectos." para usuario inexistente o contraseña errónea.
- **Inyección SQL:** todo el acceso a datos pasa por el ORM (repositorios `JpaRepository`). No hay SQL armado concatenando texto: Hibernate usa sentencias preparadas con parámetros `?`, visibles en la consola gracias a `spring.jpa.show-sql=true` (para ver también los valores: `logging.level.org.hibernate.orm.jdbc.bind=TRACE`).
- **CORS:** una sola configuración en `config/CorsConfig.java`, aplicada por Spring Security. Los orígenes permitidos están en `app.cors.origenes-permitidos` (`application.properties`); con cookies de sesión nunca se usa `*`.
- **Cabeceras HTTP de seguridad:** `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Content-Security-Policy`, `Referrer-Policy` y `Strict-Transport-Security` (esta última solo sobre HTTPS). En la API se configuran en `config/SecurityConfig.java`; en el frontend, en `frontend/vite.config.js`. Para verlas: `curl -I http://localhost:8080/api/health` y `curl -I http://localhost:5173/`.
- **Documentación técnica:** `docs/Documentacion_Tecnica.pdf` (arquitectura, diagramas de componentes y de despliegue, casos de uso, seguridad e instalación). Fuente editable: `docs/documentacion/Documentacion_Tecnica.html`.

## Endpoints disponibles

Acceso: **Público** = sin sesión · **Cliente** / **Instructor** / **Admin** = rol requerido.

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/health` | Público | Verifica que el backend esté activo. Responde `{"status":"ok"}` |
| POST | `/api/auth/login` | Público | Inicia sesión (usuario y contraseña, cualquier rol). `200` o `401` |
| GET | `/api/auth/sesion` | Público | Usuario con sesión iniciada. `401` sin sesión |
| POST | `/api/auth/logout` | Público | Cierra la sesión. `204` |
| GET | `/api/cursos`, `/api/cursos/{id}` | Público | Cursos. `404` si no existe |
| POST / PUT / DELETE | `/api/cursos[/{id}]` | Admin | Crea `201`, actualiza `200` o elimina `204` un curso (`409` si tiene inscritos o historial) |
| POST | `/api/inscripciones` | Público | Inscribe un cliente (crea su contraseña BCrypt). `201`, `400`, `404` o `409` |
| GET | `/api/inscripciones` | Admin | Lista de clientes |
| GET | `/api/inscripciones/estadisticas` | Admin | Conteo de inscripciones que lleva el Observer |
| GET | `/api/inscripciones/mia` | Cliente | La inscripción del cliente con sesión iniciada |
| GET / PUT / DELETE | `/api/inscripciones/{cedula}` | Admin o el propio cliente | Consulta, actualiza datos de contacto o desinscribe (`409` si tiene rutinas o historial) |
| GET | `/api/maquinas`, `/api/maquinas/{id}` | Público | Máquinas del gimnasio |
| POST / PUT / DELETE | `/api/maquinas[/{id}]` | Instructor | CRUD de máquinas (`409` al eliminar si está en rutinas) |
| GET | `/api/rutinas/mias` | Cliente | Solo las rutinas del cliente con sesión iniciada |
| GET / POST / PUT / DELETE | `/api/rutinas[/{id}]` | Instructor | CRUD de rutinas (al editar solo cambian fecha y horas) |
| GET / POST / PUT / DELETE | `/api/admin/instructores[/{cod}]` | Admin | CRUD de instructores (`409` al eliminar si tiene rutinas o historial) |
| GET / POST / DELETE | `/api/admin/administradores[/{cod}]` | Admin | Administradores (no se puede eliminar la propia cuenta ni el último) |
| GET / PUT | `/api/admin/parametros[/{clave}]` | Admin | Parámetros del sistema (solo cambia el valor) |
| POST | `/api/consultas` | Público | Recibe el formulario de Contactos. `201` o `400` |
| GET | `/api/consultas` | Admin | Lista de consultas de Contactos |

**Servicio SOAP** (ejemplo para comparar con REST): `POST /ws`, WSDL en `http://localhost:8080/ws/cursos.wsdl`, operaciones `obtenerCurso` y `listarCursos`. Documentación, WSDL y XSD en [`docs/soap/`](docs/soap/README.md).

Los errores siempre responden JSON con `{"mensaje": "...", "errores": {campo: mensaje}}`, sin trazas ni errores de MySQL (también los `401`/`403` de Spring Security).

## Backend: estructura y patrones (Entregable 4)

Paquetes en `backend/src/main/java/com/grupo1/gimnasio/gimnasio_backend/`:

| Paquete | Contenido |
|---|---|
| `controllers/` | Controladores REST (reciben las peticiones y devuelven JSON) |
| `services/` y `services/impl/` | Interfaces de servicio y sus implementaciones (reglas de negocio) |
| `repositories/` | Repositorios Spring Data JPA (`JpaRepository`) |
| `models/` | Entidades JPA mapeadas a las tablas de `GIMNASIO.sql` |
| `dto/` | Datos que entran y salen de la API, con validación (`@NotBlank`, `@Email`, `@Size`, `@Pattern`...) |
| `config/` | Spring Security (`SecurityConfig`), CORS (`CorsConfig`), SOAP (`SoapConfig`) y carga de contraseñas de prueba (`CargaInicialDatosRunner`) |
| `exceptions/` | Excepciones de negocio y el manejador central `@RestControllerAdvice` |
| `observer/` | Patrón Observer de las inscripciones |
| `sesion/` | Registro de sesiones HTTP (Singleton) |
| `soap/` | Servicio SOAP de cursos (endpoint y clases JAXB del XSD) |

| Patrón | Dónde |
|---|---|
| MVC | `models/` (modelo), `controllers/` (controlador), JSON y páginas HTML (vista) |
| Singleton | `sesion/GestorSesiones` (instancia única) y los servicios con `@Scope(SINGLETON)` |
| Observer | `observer/PublicadorInscripciones` (Subject) avisa a `BitacoraInscripciones` y `EstadisticasInscripciones` en cada inscripción o desinscripción |
| Iterator | Los `listar()` de los servicios (cursos, inscripciones, máquinas, rutinas, instructores, administradores, parámetros) recorren las entidades con `java.util.Iterator`; en el frontend, `models/Coleccion.js` implementa `[Symbol.iterator]` |

> **Si ya tenías la base creada antes del Entregable 4**, volvé a ejecutar `database/GIMNASIO.sql` (paso 2): el script agrega columnas y tablas nuevas (`credenciales`, `consultas`) que el backend necesita.

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