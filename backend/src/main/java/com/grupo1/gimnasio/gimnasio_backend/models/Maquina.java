package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Tabla {@code maquinas}. */
@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maquina")
    private Long idMaquina;

    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;

    @Column(name = "estado", length = 15)
    private String estado;

    @OneToMany(mappedBy = "maquina")
    private List<Rutina> rutinas = new ArrayList<>();

    public Maquina() {
    }

    public Long getIdMaquina() {
        return idMaquina;
    }

    public void setIdMaquina(Long idMaquina) {
        this.idMaquina = idMaquina;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Rutina> getRutinas() {
        return rutinas;
    }

    public void setRutinas(List<Rutina> rutinas) {
        this.rutinas = rutinas;
    }
}
