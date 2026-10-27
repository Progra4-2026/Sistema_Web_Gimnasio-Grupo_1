package com.grupo1.gimnasio.gimnasio_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Mapea la tabla "rutinas" (database/GIMNASIO.sql): un cliente usando una máquina
 * bajo la supervisión de un instructor. Las llaves foráneas se guardan como Integer
 * simple (sin @ManyToOne), igual que el resto de las entidades del proyecto.
 */
@Entity
@Table(name = "rutinas")
public class Rutina {

    @Id
    @Column(name = "id_rutina")
    private Integer idRutina;

    private Integer cliente;

    private Integer instructor;

    private Integer maquina;

    private LocalDate fecha;

    private Integer horas;

    public Integer getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(Integer idRutina) {
        this.idRutina = idRutina;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public Integer getInstructor() {
        return instructor;
    }

    public void setInstructor(Integer instructor) {
        this.instructor = instructor;
    }

    public Integer getMaquina() {
        return maquina;
    }

    public void setMaquina(Integer maquina) {
        this.maquina = maquina;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }
}
