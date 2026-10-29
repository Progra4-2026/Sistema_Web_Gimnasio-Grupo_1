package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;

/**
 * Tabla {@code administradores}: tercer rol del sistema (gestiona usuarios y parámetros).
 * El rol no se guarda en una columna: lo determina en cuál tabla coincide el login.
 */
@Entity
@Table(name = "administradores")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_administrador")
    private Long codAdministrador;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "e_mail", length = 100)
    private String email;

    /** Contraseña cifrada con BCrypt (hash + salt). Nunca en texto plano. */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    public Administrador() {
    }

    public Long getCodAdministrador() {
        return codAdministrador;
    }

    public void setCodAdministrador(Long codAdministrador) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
