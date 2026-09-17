package com.umg.sgau.config;

import com.umg.sgau.security.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) -> {

                            response.setStatus(
                                HttpServletResponse.SC_UNAUTHORIZED
                            );

                            response.setContentType(
                                "application/json"
                            );

                            response.getWriter().write(
                                """
                                {
                                  "status": 401,
                                  "error": "Unauthorized",
                                  "message": "Se requiere autenticación válida"
                                }
                                """
                            );
                        }
                    )
                    .accessDeniedHandler((request, response, exception) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write(
                            """
                            {
                              "status": 403,
                              "error": "Forbidden",
                              "message": "No tiene permisos para este recurso"
                            }
                            """
                        );
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
}
