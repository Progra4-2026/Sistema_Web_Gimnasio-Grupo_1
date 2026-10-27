package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.MaquinaDTO;

import java.util.List;

/** Lógica de negocio de las máquinas. Implementación: MaquinaServiceImpl. */
public interface MaquinaService {

    List<MaquinaDTO> listar();

    MaquinaDTO obtener(int id);

    MaquinaDTO crear(MaquinaDTO maquina);

    MaquinaDTO actualizar(int id, MaquinaDTO cambios);

    void eliminar(int id);
}
