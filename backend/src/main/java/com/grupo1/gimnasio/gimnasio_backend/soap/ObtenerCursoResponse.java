package com.grupo1.gimnasio.gimnasio_backend.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/** Elemento obtenerCursoResponse del XSD. */
@XmlRootElement(name = "obtenerCursoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObtenerCursoResponse {

    private CursoSoap curso;

    public ObtenerCursoResponse() {
    }

    public ObtenerCursoResponse(CursoSoap curso) {
        this.curso = curso;
    }

    public CursoSoap getCurso() {
        return curso;
    }
}
