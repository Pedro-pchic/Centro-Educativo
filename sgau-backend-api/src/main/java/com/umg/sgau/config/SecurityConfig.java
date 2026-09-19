package com.umg.sgau.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umg.sgau.error.ApiErrorResponse;
import com.umg.sgau.security.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
            String allowedOrigins) {
        if (allowedOrigins.contains("*")) {
            throw new IllegalArgumentException(
                    "app.cors.allowed-origins no puede usar wildcard");
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(Customizer.withDefaults())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) -> {
                            escribirError(
                                    request,
                                    response,
                                    HttpStatus.UNAUTHORIZED,
                                    "Se requiere autenticación válida",
                                    objectMapper);
                        }
                    )
                    .accessDeniedHandler((request, response, exception) -> {
                        escribirError(
                                request,
                                response,
                                HttpStatus.FORBIDDEN,
                                "No tiene permisos para este recurso",
                                objectMapper);
                    })
            )

            .authorizeHttpRequests(auth -> auth

                .dispatcherTypeMatchers(
                    DispatcherType.ERROR
                )
                .permitAll()

                .requestMatchers(
                    "/error"
                )
                .permitAll()

                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**"
                )
                .permitAll()

                .requestMatchers("/api/usuarios/**")
                .hasRole("ADMIN")

                .requestMatchers(
                    "/api/estudiantes/me",
                    "/api/estudiantes/me/**"
                )
                .hasRole("ESTUDIANTE")

                .requestMatchers(
                    "/api/docentes/me",
                    "/api/docentes/me/**"
                )
                .hasRole("DOCENTE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/estudiantes/*"
                )
                .hasAnyRole("ADMIN", "ESTUDIANTE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/docentes/*"
                )
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/cursos/*"
                )
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/estudiantes/**",
                    "/api/docentes/**",
                    "/api/cursos/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/carreras/**")
                .hasAnyRole("ADMIN", "DOCENTE", "ESTUDIANTE")

                .requestMatchers(HttpMethod.GET, "/api/inscripciones")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/inscripciones/paginadas")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/inscripciones/estudiante/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/inscripciones/curso/**")
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(HttpMethod.GET, "/api/inscripciones/*")
                .hasAnyRole("ADMIN", "DOCENTE", "ESTUDIANTE")

                .requestMatchers(HttpMethod.GET, "/api/notas")
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/notas/curso/**",
                    "/api/notas/inscripcion/**",
                    "/api/notas/estudiante/**"
                )
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(HttpMethod.GET, "/api/notas/*")
                .hasAnyRole("ADMIN", "DOCENTE", "ESTUDIANTE")

                .requestMatchers(HttpMethod.GET, "/api/colegiaturas")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/colegiaturas/*")
                .hasAnyRole("ADMIN", "ESTUDIANTE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/notas/**"
                )
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/notas/**"
                )
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/notas/**"
                )
                .hasAnyRole("ADMIN", "DOCENTE")

                .requestMatchers(
                    "/api/estudiantes/**",
                    "/api/docentes/**",
                    "/api/cursos/**",
                    "/api/inscripciones/**",
                    "/api/colegiaturas/**",
                    "/api/notas/**"
                )
                .hasRole("ADMIN")

                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    private void escribirError(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String message,
            ObjectMapper objectMapper) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                new ApiErrorResponse(
                        java.time.Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        null));
    }
}
