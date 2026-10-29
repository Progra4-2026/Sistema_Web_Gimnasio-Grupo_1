package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;

/**
 * Tabla {@code parametros_sistema}: configuración clave/valor que gestiona el administrador.
 * Las claves las crea GIMNASIO.sql; desde la API solo se cambia el valor.
 */
@Entity
@Table(name = "parametros_sistema")
public class ParametroSistema {

    @Id
    @Column(name = "clave", length = 50)
    private String clave;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public ParametroSistema() {
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
