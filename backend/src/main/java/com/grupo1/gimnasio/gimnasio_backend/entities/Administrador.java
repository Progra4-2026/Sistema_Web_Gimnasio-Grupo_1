package com.grupo1.gimnasio.gimnasio_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Mapea la tabla "administradores" (database/GIMNASIO.sql). */
@Entity
@Table(name = "administradores")
public class Administrador {

    @Id
    @Column(name = "cod_administrador")
    private Integer codAdministrador;

    private String nombre;

    @Column(name = "e_mail")
    private String email;

    private String contrasena;

    public Integer getCodAdministrador() {
        return codAdministrador;
    }

    public void setCodAdministrador(Integer codAdministrador) {
        this.codAdministrador = codAdministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
