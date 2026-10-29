package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;

/**
 * Tabla {@code credenciales} (sección h de GIMNASIO.sql).
 * Relación 1 a 1 con Cliente: comparte la llave (la cédula) mediante @MapsId.
 * Guarda la contraseña ya cifrada con BCrypt (hash + salt, PasswordEncoder de Spring Security), nunca en texto plano.
 */
@Entity
@Table(name = "credenciales")
public class Credencial {

    @Id
    @Column(name = "cedula")
    private Long cedula;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "cedula")
    private Cliente cliente;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    public Credencial() {
    }

    public Long getCedula() {
        return cedula;
    }

    public void setCedula(Long cedula) {
        this.cedula = cedula;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
