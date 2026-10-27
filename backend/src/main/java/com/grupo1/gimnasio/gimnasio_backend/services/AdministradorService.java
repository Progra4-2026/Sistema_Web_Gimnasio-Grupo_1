package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.AdministradorDTO;

import java.util.List;

/** Administración de cuentas de administrador. Implementación: AdministradorServiceImpl. */
public interface AdministradorService {

    List<AdministradorDTO> listar();

    AdministradorDTO crear(AdministradorDTO administrador);

    void eliminar(int codAdministrador);
}
