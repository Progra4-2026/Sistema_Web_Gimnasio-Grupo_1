package com.grupo1.gimnasio.gimnasio_backend.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/** Tipo cursoSoap del XSD. */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cursoSoap", propOrder = {"id", "descripcion", "detalle", "horario", "cupos"})
public class CursoSoap {

    private int id;
    private String descripcion;
    private String detalle;
    private String horario;
    private int cupos;

    public CursoSoap() {
    }

    public CursoSoap(int id, String descripcion, String detalle, String horario, int cupos) {
        this.id = id;
        this.descripcion = descripcion;
        this.detalle = detalle;
        this.horario = horario;
        this.cupos = cupos;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getHorario() {
        return horario;
    }

    public int getCupos() {
        return cupos;
    }
}
