package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Tabla {@code cursos}. */
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long idCurso;

    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;

    // Datos que muestra la página Servicios (sección h de GIMNASIO.sql)
    @Column(name = "detalle", length = 300)
    private String detalle;

    @Column(name = "horario", nullable = false, length = 60)
    private String horario;

    @Column(name = "cupos", nullable = false)
    private Integer cupos;

    @Column(name = "imagen", nullable = false, length = 100)
    private String imagen;

    @OneToMany(mappedBy = "curso")
    private List<HistorialCurso> historialCursos = new ArrayList<>();

    public Curso() {
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Integer getCupos() {
        return cupos;
    }

    public void setCupos(Integer cupos) {
        this.cupos = cupos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<HistorialCurso> getHistorialCursos() {
        return historialCursos;
    }

    public void setHistorialCursos(List<HistorialCurso> historialCursos) {
        this.historialCursos = historialCursos;
    }
}
