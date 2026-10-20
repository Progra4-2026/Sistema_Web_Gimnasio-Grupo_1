package com.grupo1.gimnasio.gimnasio_backend.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/** Elemento listarCursosResponse del XSD. */
@XmlRootElement(name = "listarCursosResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListarCursosResponse {

    @XmlElement(name = "curso")
    private List<CursoSoap> cursos = new ArrayList<>();

    public List<CursoSoap> getCursos() {
        return cursos;
    }
}
