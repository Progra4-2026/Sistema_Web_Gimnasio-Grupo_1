package com.grupo1.gimnasio.gimnasio_backend.config;

import com.grupo1.gimnasio.gimnasio_backend.controllers.Encabezados;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing): permite que el frontend React, servido desde otro
 * origen (Vite en el puerto 5173, o Apache/XAMPP en el 80), consuma la API REST del puerto 8080.
 *
 * Una sola configuración para todo el backend: SecurityConfig la registra con
 * http.cors(...), así el filtro de Spring Security agrega los encabezados CORS incluso a las
 * respuestas 401/403 y a las peticiones preflight (OPTIONS) que el navegador hace antes de
 * un POST, PUT o DELETE.
 *
 * Reglas:
 *  - Solo los orígenes de la lista blanca (app.cors.origenes-permitidos). Nunca "*", porque
 *    con allowCredentials(true) el navegador envía la cookie de sesión y un comodín
 *    permitiría que cualquier sitio actuara en nombre del usuario.
 *  - Solo los métodos y encabezados que usa el frontend.
 *  - allowCredentials(true): deja pasar la cookie JSESSIONID del login.
 *  - maxAge: el navegador guarda la respuesta del preflight 1 hora y no la repite en cada llamada.
 */
@Configuration
public class CorsConfig {

    private final List<String> origenesPermitidos;

    public CorsConfig(@Value("${app.cors.origenes-permitidos}") List<String> origenesPermitidos) {
        this.origenesPermitidos = origenesPermitidos;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(origenesPermitidos);
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Content-Type", "Accept"));
        configuracion.setExposedHeaders(List.of("Location", Encabezados.REGISTROS_ELIMINADOS));
        configuracion.setAllowCredentials(true);
        configuracion.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", configuracion);
        return fuente;
    }
}
