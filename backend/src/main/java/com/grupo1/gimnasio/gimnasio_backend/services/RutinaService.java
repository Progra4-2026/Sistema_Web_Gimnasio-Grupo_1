package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.RutinaDTO;

import java.util.List;

/** Lógica de negocio de las rutinas. Implementación: RutinaServiceImpl. */
public interface RutinaService {

    List<RutinaDTO> listar();

    /** Solo las rutinas del cliente indicado (el controlador pasa la cédula de la sesión). */
    List<RutinaDTO> listarDeCliente(long cedula);

    RutinaDTO obtener(long id);

    /** El instructor de la rutina es siempre el que está en sesión. */
    RutinaDTO crear(RutinaDTO rutina, long codInstructor);

    /** Solo cambian fecha y horas: cliente, máquina e instructor son llaves foráneas (400 si cambian). */
    RutinaDTO actualizar(long id, RutinaDTO cambios);

    void eliminar(long id);
}
