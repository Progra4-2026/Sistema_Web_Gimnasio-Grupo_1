package com.grupo1.gimnasio.gimnasio_backend.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;

/**
 * Seguridad del backend con Spring Security.
 *
 * Autenticación por sesión (HttpSession + cookie JSESSIONID). El login es el endpoint propio
 * de AutenticacionController: AutenticacionServiceImpl valida las credenciales, construye el
 * SecurityContext con el rol y lo guarda en la sesión con el SecurityContextRepository de aquí.
 *
 * Contraseñas: BCrypt (hash + salt) mediante el PasswordEncoder de esta clase.
 *
 * Autorización en dos niveles:
 *  1. Por URL (authorizeHttpRequests): lo público queda abierto y todo lo demás exige sesión.
 *  2. Por rol en cada controlador (@PreAuthorize, habilitado con @EnableMethodSecurity).
 *
 * Cabeceras HTTP de seguridad (headers), en TODAS las respuestas de la API:
 *  - X-Frame-Options: DENY → nadie puede cargar la API dentro de un iframe (clickjacking).
 *  - X-Content-Type-Options: nosniff → el navegador no "adivina" el tipo; un JSON nunca se ejecuta.
 *  - Strict-Transport-Security (HSTS) → obliga a usar HTTPS durante un año. Spring solo la envía
 *    cuando la petición llega por HTTPS ("cuando aplique"): en desarrollo con http://localhost no
 *    aparece; en producción con HTTPS sí.
 *  - Content-Security-Policy y Referrer-Policy → la API solo devuelve datos, así que no permite
 *    cargar recursos ni ser incrustada, y no filtra la URL de origen a otros sitios.
 *
 * CORS: se aplica la única configuración de CorsConfig.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource,
                                           SecurityContextRepository securityContextRepository) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .headers(cabeceras -> cabeceras
                        .frameOptions(opciones -> opciones.deny())
                        .contentTypeOptions(Customizer.withDefaults())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31_536_000))
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'none'; frame-ancestors 'none'"))
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.NO_REFERRER)))
                // API JSON sin formularios HTML servidos por el backend: la lista blanca de orígenes
                // de CorsConfig ya bloquea los envíos cross-site que CSRF evitaría.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .securityContext(sc -> sc.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(auth -> auth
                        // Reenvíos internos de Spring (página de error): no deben pedir sesión de nuevo
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error", "/api/health", "/api/auth/**").permitAll()
                        // Servicio SOAP de cursos (solo lectura): público como GET /api/cursos
                        .requestMatchers("/ws", "/ws/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cursos", "/api/cursos/**",
                                "/api/maquinas", "/api/maquinas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/consultas", "/api/inscripciones").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((peticion, respuesta, ex) ->
                                escribirError(respuesta, 401, "Iniciá sesión para continuar."))
                        .accessDeniedHandler((peticion, respuesta, ex) ->
                                escribirError(respuesta, 403, "No tenés permiso para realizar esta acción.")));
        return http.build();
    }

    /**
     * Mismo formato {"mensaje": "..."} que ManejadorGlobalExcepciones, escrito a mano: estas
     * respuestas las genera el filtro de Spring Security antes de llegar a los controladores.
     */
    private static void escribirError(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.getWriter().write("{\"mensaje\":\"" + mensaje.replace("\"", "'") + "\"}");
    }
}
