package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Tabla {@code consultas}: mensajes enviados desde el formulario de Contactos. */
@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idConsulta;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "e_mail", nullable = false, length = 100)
    private String email;

    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;

    @Column(name = "asunto", nullable = false, length = 20)
    private String asunto;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    public Consulta() {
    }

    public Long getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Long idConsulta) {
        this.idConsulta = idConsulta;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
