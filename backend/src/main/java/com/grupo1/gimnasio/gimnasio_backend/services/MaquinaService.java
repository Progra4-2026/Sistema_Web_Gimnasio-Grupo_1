package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.MaquinaDTO;

import java.util.List;

/** Lógica de negocio de las máquinas (las gestiona el instructor). Implementación: MaquinaServiceImpl. */
public interface MaquinaService {

    List<MaquinaDTO> listar();

    MaquinaDTO obtener(long id);

    MaquinaDTO crear(MaquinaDTO maquina);

    /** No permite cambiar el id (llave primaria). */
    MaquinaDTO actualizar(long id, MaquinaDTO cambios);

    /** No permite borrar una máquina usada en rutinas (409). */
    void eliminar(long id);
}
