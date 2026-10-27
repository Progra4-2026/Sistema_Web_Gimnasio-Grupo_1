package com.grupo1.gimnasio.gimnasio_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** Mapea la tabla "instructores" (database/GIMNASIO.sql). */
@Entity
@Table(name = "instructores")
public class Instructor {

    @Id
    @Column(name = "cod_instructor")
    private Integer codInstructor;

    private String nombre;

    @Column(name = "apellido_1")
    private String apellido1;

    @Column(name = "apellido_2")
    private String apellido2;

    private String direccion;

    @Column(name = "e_mail")
    private String email;

    @Column(name = "tel_cel")
    private Integer telCel;

    @Column(name = "tel_habitacion")
    private Integer telHabitacion;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    private String contrasena;

    public Integer getCodInstructor() {
        return codInstructor;
    }

    public void setCodInstructor(Integer codInstructor) {
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String nombreCompleto() {
        StringBuilder sb = new StringBuilder(nombre == null ? "" : nombre);
        if (apellido1 != null && !apellido1.isBlank()) sb.append(' ').append(apellido1);
        if (apellido2 != null && !apellido2.isBlank()) sb.append(' ').append(apellido2);
        return sb.toString();
    }
}
