package com.grupo1.gimnasio.gimnasio_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Mapea la tabla "parametros_sistema": configuración clave/valor que gestiona el administrador. */
@Entity
@Table(name = "parametros_sistema")
public class ParametroSistema {

    @Id
    private String clave;

    private String valor;

    private String descripcion;

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
