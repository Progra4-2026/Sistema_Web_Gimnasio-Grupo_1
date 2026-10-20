# Servicio SOAP de cursos

Servicio SOAP de ejemplo para comparar con los servicios REST de `/api`. Expone las mismas consultas
que `GET /api/cursos` y `GET /api/cursos/{id}`, reutilizando `CursoService`.

| | |
|---|---|
| Endpoint | `POST http://localhost:8080/ws` (Content-Type `text/xml`) |
| WSDL | `http://localhost:8080/ws/cursos.wsdl` (copia en `cursos.wsdl`) |
| XSD | `cursos.xsd` (en el código: `backend/src/main/resources/soap/cursos.xsd`) |
| Namespace | `http://grupo1.gimnasio.com/soap/cursos` |
| Código | `backend/.../soap/CursoSoapEndpoint.java`, `backend/.../config/SoapConfig.java` |

## Operaciones

### obtenerCurso

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:c="http://grupo1.gimnasio.com/soap/cursos">
   <soapenv:Body>
      <c:obtenerCursoRequest>
         <c:id>1</c:id>
      </c:obtenerCursoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

Si el curso no existe responde un `SOAP-ENV:Fault` con `faultcode` `Client` y el mensaje
"No existe un curso con identificador N." (equivale al 404 de REST).

### listarCursos

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:c="http://grupo1.gimnasio.com/soap/cursos">
   <soapenv:Body>
      <c:listarCursosRequest/>
   </soapenv:Body>
</soapenv:Envelope>
```

## Probarlo desde la terminal

```bash
curl -H 'Content-Type: text/xml' -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:c="http://grupo1.gimnasio.com/soap/cursos"><soapenv:Body><c:obtenerCursoRequest><c:id>1</c:id></c:obtenerCursoRequest></soapenv:Body></soapenv:Envelope>' http://localhost:8080/ws
```

También se puede importar el WSDL en SoapUI o Postman.

## SOAP vs. REST en este proyecto

| | REST (`/api/cursos`) | SOAP (`/ws`) |
|---|---|---|
| Formato | JSON | XML dentro de un sobre SOAP |
| Contrato | Implícito (documentado en el README) | Explícito y formal: WSDL + XSD |
| Operaciones | Verbos HTTP sobre recursos (GET, POST, PUT, DELETE) | Operaciones con nombre (`obtenerCurso`, `listarCursos`), siempre por POST |
| Errores | Códigos HTTP (404, 400, 409…) + JSON | `SOAP Fault` (`Client` / `Server`) |
| Uso en el sistema | Lo consume el frontend | Ejemplo de integración entre sistemas |
