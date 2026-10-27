package com.grupo1.gimnasio.gimnasio_backend.config;

import com.grupo1.gimnasio.gimnasio_backend.controllers.Encabezados;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Permite que las páginas servidas por Vite (puerto 5173) o por Apache/XAMPP
 * (puerto 80) consuman la API REST en el puerto 8080 mediante fetch.
 * allowCredentials(true) deja pasar la cookie de sesión (JSESSIONID) del login.
 *
 * El bean CorsConfigurationSource se expone aparte porque el filtro de Spring
 * Security corre ANTES del WebMvcConfigurer de abajo: si SecurityConfig no usa
 * esta misma configuración, una petición bloqueada por Security nunca llega a
 * recibir los encabezados CORS y el navegador lo reporta como error de CORS
 * en vez de 401/403.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost",
                "http://127.0.0.1"));
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Content-Type", "Accept"));
        configuracion.setExposedHeaders(List.of("Location", Encabezados.REGISTROS_ELIMINADOS));
        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", configuracion);
        return fuente;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost",
                        "http://127.0.0.1")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept")
                .exposedHeaders("Location", Encabezados.REGISTROS_ELIMINADOS)
                .allowCredentials(true);
    }
}
