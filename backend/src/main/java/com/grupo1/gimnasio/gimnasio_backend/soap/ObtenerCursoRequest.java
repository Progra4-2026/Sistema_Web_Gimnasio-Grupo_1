package com.grupo1.gimnasio.gimnasio_backend.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/** Elemento obtenerCursoRequest del XSD. */
@XmlRootElement(name = "obtenerCursoRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObtenerCursoRequest {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
