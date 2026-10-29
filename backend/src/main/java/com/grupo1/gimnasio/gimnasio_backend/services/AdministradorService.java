package com.grupo1.gimnasio.gimnasio_backend.services;

import com.grupo1.gimnasio.gimnasio_backend.dto.AdministradorDTO;

import java.util.List;

/** Gestión de cuentas de administrador. Implementación: AdministradorServiceImpl. */
public interface AdministradorService {

    List<AdministradorDTO> listar();

    AdministradorDTO crear(AdministradorDTO datos);

    /** No permite borrar la propia cuenta ni al último administrador (409). */
    void eliminar(long codAdministrador, long codEnSesion);
}
