package com.grupo1.gimnasio.gimnasio_backend.controllers;

import com.grupo1.gimnasio.gimnasio_backend.dto.CursoDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * GET /api/cursos — Lista de cursos para la sección Cursos.
 * TEMPORAL (Entregable 3): datos en memoria para probar el fetch del frontend.
 * En el Entregable 4 se reemplaza por CursoService + CursoRepository (JPA).
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private static final List<CursoDTO> CURSOS = List.of(
            new CursoDTO(1, "Spinning", "Clase cardiovascular de alta intensidad en bicicleta estática.",
                    "Lun/Mié/Vie, 6:00 a.m.", 8, "clase_spinning.jpg"),
            new CursoDTO(2, "Yoga funcional", "Ejercicios de flexibilidad, respiración y relajación.",
                    "Mar/Jue, 7:00 p.m.", 12, "clase_yoga.jpg"),
            new CursoDTO(3, "CrossFit", "Entrenamiento funcional de alta intensidad en grupo.",
                    "Lun a Vie, 5:30 p.m.", 0, "clase_crossfit.jpg"),
            new CursoDTO(4, "Clases grupales", "Aeróbicos y circuito funcional para todos los niveles.",
                    "Sáb, 9:00 a.m.", 15, "clases_grupales.jpg")
    );

    @GetMapping
    public List<CursoDTO> listar() {
        return CURSOS;
    }
}
