package com.grupo1.gimnasio.gimnasio_backend.soap;

import com.grupo1.gimnasio.gimnasio_backend.dto.CursoDTO;
import com.grupo1.gimnasio.gimnasio_backend.exceptions.RecursoNoEncontradoException;
import com.grupo1.gimnasio.gimnasio_backend.services.CursoService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Servicio SOAP de cursos — POST http://localhost:8080/ws  (WSDL en /ws/cursos.wsdl).
 *
 * Expone las mismas consultas que GET /api/cursos y GET /api/cursos/{id}, pero con mensajes XML
 * definidos por un contrato (soap/cursos.xsd), para comparar SOAP con REST.
 * Reutiliza CursoService: la lógica de negocio es la misma para los dos tipos de servicio.
 */
@Endpoint
public class CursoSoapEndpoint {

    public static final String NAMESPACE = "http://grupo1.gimnasio.com/soap/cursos";

    private final CursoService cursoService;

    public CursoSoapEndpoint(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "obtenerCursoRequest")
    @ResponsePayload
    public ObtenerCursoResponse obtenerCurso(@RequestPayload ObtenerCursoRequest peticion) {
        try {
            return new ObtenerCursoResponse(aSoap(cursoService.obtener(peticion.getId())));
        } catch (RecursoNoEncontradoException e) {
            throw new CursoNoEncontradoSoapException(e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "listarCursosRequest")
    @ResponsePayload
    public ListarCursosResponse listarCursos(@RequestPayload ListarCursosRequest peticion) {
        ListarCursosResponse respuesta = new ListarCursosResponse();
        for (CursoDTO curso : cursoService.listar()) {
            respuesta.getCursos().add(aSoap(curso));
        }
        return respuesta;
    }

    private static CursoSoap aSoap(CursoDTO c) {
        return new CursoSoap(c.id(), c.descripcion(), c.detalle(), c.horario(), c.cupos());
    }
}
