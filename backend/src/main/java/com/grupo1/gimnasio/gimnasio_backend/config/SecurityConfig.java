package com.grupo1.gimnasio.gimnasio_backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;

/**
 * Autenticación por sesión (HttpSession + cookie JSESSIONID, no JWT/stateless) con
 * BCrypt para las contraseñas y @PreAuthorize para la autorización por rol.
 *
 * El login sigue siendo el endpoint propio de AutenticacionController (no se usa
 * formLogin()): ese controlador construye el SecurityContext a mano tras validar
 * las credenciales y lo guarda en la sesión con el mismo SecurityContextRepository
 * que se registra aquí, para que las peticiones siguientes lo recuperen de la cookie.
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
                // API JSON pura, sin formularios HTML servidos por el backend: el whitelist de
                // orígenes de CorsConfig ya bloquea el envío cross-site silencioso que CSRF evita.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .securityContext(sc -> sc.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/health", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cursos/**", "/api/maquinas/**").permitAll()
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
     * Mismo formato {"mensaje": "..."} que ControladorBase, escrito a mano: estas respuestas
     * las genera el filtro de Spring Security (ExceptionTranslationFilter), antes de llegar
     * al DispatcherServlet, así que no pasan por ningún @ExceptionHandler ni por el conversor
     * JSON de Spring MVC.
     */
    private static void escribirError(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.getWriter().write("{\"mensaje\":\"" + mensaje.replace("\"", "'") + "\"}");
    }
}
