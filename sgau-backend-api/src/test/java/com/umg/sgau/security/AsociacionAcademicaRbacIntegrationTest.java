package com.umg.sgau.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.umg.sgau.SgauBackendApiApplication;
import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = SgauBackendApiApplication.class,
        properties = {
                "jwt.secret=clave-de-prueba-segura-de-al-menos-32-caracteres",
                "spring.datasource.url=jdbc:h2:mem:asociacion;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class AsociacionAcademicaRbacIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private UsuarioEntity admin;
    private UsuarioEntity usuarioEstudiante;
    private UsuarioEntity usuarioDocente;
    private EstudianteEntity estudiante;
    private DocenteEntity docente;

    @BeforeEach
    void prepararDatos() {
        estudianteRepository.deleteAll();
        docenteRepository.deleteAll();
        usuarioRepository.deleteAll();

        admin = usuarioRepository.save(usuario("admin", RolUsuario.ADMIN));
        usuarioEstudiante = usuarioRepository.save(usuario("estudiante", RolUsuario.ESTUDIANTE));
        usuarioDocente = usuarioRepository.save(usuario("docente", RolUsuario.DOCENTE));

        estudiante = new EstudianteEntity();
        estudiante.setNombre("Ana");
        estudiante.setApellido("López");
        estudiante.setEmail("ana@prueba.test");
        estudiante.setCodigoEstudiante("EST-1");
        estudiante.setCarnet("CAR-1");
        estudiante = estudianteRepository.save(estudiante);

        docente = new DocenteEntity();
        docente.setNombre("Carlos");
        docente.setApellido("Pérez");
        docente.setEmailInstitucional("carlos@prueba.test");
        docente.setEmailPersonal("carlos.personal@prueba.test");
        docente.setDpi("1234567890101");
        docente.setFechaContratacion(LocalDate.of(2020, 1, 1));
        docente = docenteRepository.save(docente);
    }

    @Test
    void adminPuedeAsociarPerfilesYLaAsociacionSePersiste() throws Exception {
        mockMvc.perform(post("/api/usuarios/asociar-estudiante")
                        .header("Authorization", bearerToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": %d,
                                  "estudianteId": %d
                                }
                                """.formatted(usuarioEstudiante.getId(), estudiante.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/usuarios/asociar-docente")
                        .header("Authorization", bearerToken(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": %d,
                                  "docenteId": %d
                                }
                                """.formatted(usuarioDocente.getId(), docente.getId())))
                .andExpect(status().isNoContent());

        assertEquals(usuarioEstudiante.getId(),
                estudianteRepository.findById(estudiante.getId()).orElseThrow()
                        .getUsuario().getId());
        assertEquals(usuarioDocente.getId(),
                docenteRepository.findById(docente.getId()).orElseThrow()
                        .getUsuario().getId());
    }

    @Test
    void usuarioNoAdminNoPuedeAsociarPerfiles() throws Exception {
        mockMvc.perform(post("/api/usuarios/asociar-estudiante")
                        .header("Authorization", bearerToken(usuarioEstudiante))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": %d,
                                  "estudianteId": %d
                                }
                                """.formatted(usuarioEstudiante.getId(), estudiante.getId())))
                .andExpect(status().isForbidden());
    }

    private UsuarioEntity usuario(String username, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode("Secreto123"));
        usuario.setEmail(username + "@prueba.test");
        usuario.setNombre(username);
        usuario.setApellido("Prueba");
        usuario.setActivo(true);
        usuario.setRol(rol);
        return usuario;
    }

    private String bearerToken(UsuarioEntity usuario) {
        return "Bearer " + jwtService.generateToken(
                User.withUsername(usuario.getUsername())
                        .password("irrelevante")
                        .authorities("ROLE_" + usuario.getRol().name())
                        .build());
    }
}
