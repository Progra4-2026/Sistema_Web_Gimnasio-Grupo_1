package com.grupo1.gimnasio.gimnasio_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Mapea la tabla "maquinas" (database/GIMNASIO.sql). */
@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @Column(name = "id_maquina")
    private Integer idMaquina;

    private String descripcion;

    private String estado;

    public Integer getIdMaquina() {
        return idMaquina;
    }

    public void setIdMaquina(Integer idMaquina) {
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
}
