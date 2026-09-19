package com.umg.sgau.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.umg.sgau.SgauBackendApiApplication;
import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.carrera.repository.CarreraRepository;
import com.umg.sgau.security.JwtService;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
        classes = SgauBackendApiApplication.class,
        properties = {
                "jwt.secret=clave-de-prueba-segura-de-al-menos-32-caracteres",
                "spring.datasource.url=jdbc:h2:mem:errorcontract;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
@Import(ApiErrorContractIntegrationTest.ErrorEndpointConfiguration.class)
class ApiErrorContractIntegrationTest {

    private static final String PASSWORD = "Secreto123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private UsuarioEntity admin;
    private UsuarioEntity docente;

    @BeforeEach
    void prepararDatos() {
        carreraRepository.deleteAll();
        usuarioRepository.deleteAll();

        admin = usuarioRepository.save(usuario("admin-error", RolUsuario.ADMIN));
        docente = usuarioRepository.save(usuario("docente-error", RolUsuario.DOCENTE));
        carreraRepository.save(CarreraEntity.builder()
                .codigo("ERR-1")
                .nombre("Carrera de errores")
                .build());
    }

    @Test
    void validacionDevuelveContratoConErroresDeCampos() throws Exception {
        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", bearerToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Datos de entrada inválidos"))
                .andExpect(jsonPath("$.path").value("/api/estudiantes"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fieldErrors.codigoEstudiante").exists());
    }

    @Test
    void noAutenticadoDevuelve401ConContrato() throws Exception {
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/estudiantes"));
    }

    @Test
    void autenticadoSinPermisoDevuelve403ConContrato() throws Exception {
        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", bearerToken(docente)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value("/api/estudiantes"));
    }

    @Test
    void recursoInexistenteDevuelve404ConContrato() throws Exception {
        mockMvc.perform(get("/api/carreras/999999")
                        .header("Authorization", bearerToken(admin)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/carreras/999999"));
    }

    @Test
    void conflictoDeUnicidadDevuelve409ConContrato() throws Exception {
        mockMvc.perform(post("/api/carreras")
                        .header("Authorization", bearerToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "ERR-1",
                                  "nombre": "Otra carrera"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/api/carreras"));
    }

    @Test
    void errorInesperadoDevuelve500SinDetallesInternos() throws Exception {
        mockMvc.perform(get("/test/errors/unexpected")
                        .header("Authorization", bearerToken(admin)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Ocurrió un error interno."))
                .andExpect(jsonPath("$.path").value("/test/errors/unexpected"))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("internal-secret"))));
    }

    private String bearerToken(UsuarioEntity usuario) {
        String role = "ROLE_" + usuario.getRol().name();
        return "Bearer " + jwtService.generateToken(User.withUsername(usuario.getUsername())
                .password(PASSWORD)
                .authorities(role)
                .build());
    }

    private UsuarioEntity usuario(String username, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(PASSWORD));
        usuario.setEmail(username + "@error.test");
        usuario.setNombre(username);
        usuario.setApellido("Prueba");
        usuario.setActivo(true);
        usuario.setRol(rol);
        return usuario;
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ErrorEndpointConfiguration {

        @Bean
        ErrorEndpoint errorEndpoint() {
            return new ErrorEndpoint();
        }
    }

    @RestController
    static class ErrorEndpoint {

        @GetMapping("/test/errors/unexpected")
        void unexpected() {
            throw new IllegalStateException("internal-secret");
        }
    }
}
