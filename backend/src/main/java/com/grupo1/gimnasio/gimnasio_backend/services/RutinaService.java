package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.RutinaDTO;

import java.util.List;

/** Lógica de negocio de las rutinas. Implementación: RutinaServiceImpl. */
public interface RutinaService {

    List<RutinaDTO> listar();

    List<RutinaDTO> listarDeCliente(int cedulaCliente);

    RutinaDTO obtener(int id);

    /** El instructor que la crea queda fijo como dueño; nunca se toma del cuerpo de la petición. */
    RutinaDTO crear(RutinaDTO rutina, int codInstructor);

    RutinaDTO actualizar(int id, RutinaDTO cambios);

    void eliminar(int id);
}
