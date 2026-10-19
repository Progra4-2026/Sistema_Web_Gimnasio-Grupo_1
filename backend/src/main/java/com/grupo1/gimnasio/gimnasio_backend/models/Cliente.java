package com.grupo1.gimnasio.gimnasio_backend.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Tabla {@code cliente}. La cédula es la llave primaria y la asigna el usuario (no se genera). */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @Column(name = "cedula")
    private Long cedula;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "apellido_1", nullable = false, length = 30)
    private String apellido1;

    @Column(name = "apellido_2", nullable = false, length = 30)
    private String apellido2;

    @Column(name = "direccion", length = 50)
    private String direccion;

    @Column(name = "e_mail", length = 100)
    private String email;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(name = "celular", nullable = false)
    private Integer celular;

    @Column(name = "tel_habitacion", nullable = false)
    private Integer telHabitacion;

    // Datos de la inscripción web (sección h de GIMNASIO.sql)
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "tipo_inscripcion", length = 10)
    private String tipoInscripcion;

    @Column(name = "objetivo", length = 20)
    private String objetivo;

    // muchos a 1: varios clientes pueden estar inscritos en el mismo curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_inscrito")
    private Curso cursoInscrito;

    // 1 a 1: las credenciales de acceso del cliente (el lado dueño es Credencial)
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Credencial credencial;

    // 1 a muchos: un cliente tiene varias rutinas y varios registros de cursos
    @OneToMany(mappedBy = "cliente")
    private List<Rutina> rutinas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<HistorialCurso> historialCursos = new ArrayList<>();

    public Cliente() {
    }

    public Long getCedula() {
        return cedula;
    }

    public void setCedula(Long cedula) {
        this.cedula = cedula;
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

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Integer getCelular() {
        return celular;
    }

    public void setCelular(Integer celular) {
        this.celular = celular;
    }

    public Integer getTelHabitacion() {
        return telHabitacion;
    }

    public void setTelHabitacion(Integer telHabitacion) {
        this.telHabitacion = telHabitacion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipoInscripcion() {
        return tipoInscripcion;
    }

    public void setTipoInscripcion(String tipoInscripcion) {
        this.tipoInscripcion = tipoInscripcion;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Curso getCursoInscrito() {
        return cursoInscrito;
    }

    public void setCursoInscrito(Curso cursoInscrito) {
        this.cursoInscrito = cursoInscrito;
    }

    public Credencial getCredencial() {
        return credencial;
    }

    public void setCredencial(Credencial credencial) {
        this.credencial = credencial;
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
