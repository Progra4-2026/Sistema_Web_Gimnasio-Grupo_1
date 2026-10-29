package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Tabla {@code instructores}. */
@Entity
@Table(name = "instructores")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_instructor")
    private Long codInstructor;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "apellido_1", nullable = false, length = 30)
    private String apellido1;

    @Column(name = "apellido_2", nullable = false, length = 30)
    private String apellido2;

    @Column(name = "direccion", length = 50)
    private String direccion;

    @Column(name = "e_mail", length = 30)
    private String email;

    @Column(name = "tel_cel", nullable = false)
    private Integer telCel;

    @Column(name = "tel_habitacion", nullable = false)
    private Integer telHabitacion;

    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;

    /** Contraseña cifrada con BCrypt (hash + salt). Nunca en texto plano. */
    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @OneToMany(mappedBy = "instructor")
    private List<Rutina> rutinas = new ArrayList<>();

    @OneToMany(mappedBy = "instructor")
    private List<HistorialCurso> historialCursos = new ArrayList<>();

    public Instructor() {
    }

    public Long getCodInstructor() {
        return codInstructor;
    }

    public void setCodInstructor(Long codInstructor) {
        this.codInstructor = codInstructor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTelCel() {
        return telCel;
    }

    public void setTelCel(Integer telCel) {
        this.telCel = telCel;
    }

    public Integer getTelHabitacion() {
        return telHabitacion;
    }

    public void setTelHabitacion(Integer telHabitacion) {
        this.telHabitacion = telHabitacion;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** Nombre y apellidos en una sola cadena, para mostrar. */
    public String nombreCompleto() {
        return String.join(" ", nombre, apellido1, apellido2).trim().replaceAll("\\s+", " ");
    }

    public List<Rutina> getRutinas() {
        return rutinas;
    }

    public void setRutinas(List<Rutina> rutinas) {
        this.rutinas = rutinas;
    }

    public List<HistorialCurso> getHistorialCursos() {
        return historialCursos;
    }

    public void setHistorialCursos(List<HistorialCurso> historialCursos) {
        this.historialCursos = historialCursos;
    }
}
