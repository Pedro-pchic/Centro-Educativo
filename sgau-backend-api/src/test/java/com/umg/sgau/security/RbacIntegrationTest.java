package com.umg.sgau.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.umg.sgau.SgauBackendApiApplication;
import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.carrera.repository.CarreraRepository;
import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.curso.repository.CursoRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.inscripcion.repository.InscripcionRepository;
import com.umg.sgau.nota.repository.NotaRepository;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.repository.UsuarioRepository;

@SpringBootTest(
        classes = SgauBackendApiApplication.class,
        properties = {
                "jwt.secret=clave-de-prueba-segura-de-al-menos-32-caracteres",
                "spring.datasource.url=jdbc:h2:mem:rbac;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class RbacIntegrationTest {

    private static final String PASSWORD = "Secreto123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private NotaRepository notaRepository;

    private InscripcionEntity inscripcion;

    @BeforeEach
    void prepararDatos() {
        notaRepository.deleteAll();
        inscripcionRepository.deleteAll();
        cursoRepository.deleteAll();
        carreraRepository.deleteAll();
        estudianteRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuarioRepository.save(usuario("admin", RolUsuario.ADMIN));
        usuarioRepository.save(usuario("docente", RolUsuario.DOCENTE));
        usuarioRepository.save(usuario("estudiante", RolUsuario.ESTUDIANTE));

        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setCodigoEstudiante("EST-1");
        estudiante.setCarnet("CAR-1");
        estudiante.setEmail("estudiante@prueba.test");
        estudiante.setNombre("Ana");
        estudiante.setApellido("López");
        estudiante = estudianteRepository.save(estudiante);

        CarreraEntity carrera = carreraRepository.save(CarreraEntity.builder()
                .codigo("CAR-1")
                .nombre("Ingeniería")
                .build());

        CursoEntity curso = cursoRepository.save(CursoEntity.builder()
                .codigo("CUR-1")
                .nombre("Programación")
                .creditos(4)
                .carrera(carrera)
                .build());

        inscripcion = new InscripcionEntity();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(LocalDate.of(2026, 8, 1));
        inscripcion = inscripcionRepository.save(inscripcion);
    }

    @ParameterizedTest
    @CsvSource({
            "admin, ADMIN",
            "docente, DOCENTE",
            "estudiante, ESTUDIANTE"
    })
    void loginDevuelveElRolCorrecto(String username, String rol) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.rol").value(rol))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminPuedeCrearEstudiante() throws Exception {
        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", bearerToken("admin", RolUsuario.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigoEstudiante": "EST-2",
                                  "carnet": "CAR-2",
                                  "email": "ana2@prueba.test",
                                  "nombre": "Ana",
                                  "apellido": "García"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void docentePuedeConsultarEstudiantes() throws Exception {
        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", bearerToken("docente", RolUsuario.DOCENTE)))
                .andExpect(status().isOk());
    }

    @Test
    void docenteNoPuedeEliminarEstudiante() throws Exception {
        mockMvc.perform(delete("/api/estudiantes/1")
                        .header("Authorization", bearerToken("docente", RolUsuario.DOCENTE)))
                .andExpect(status().isForbidden());
    }

    @Test
    void estudiantePuedeConsultarNotasPeroNoCrearEstudiantes() throws Exception {
        mockMvc.perform(get("/api/notas")
                        .header("Authorization", bearerToken("estudiante", RolUsuario.ESTUDIANTE)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", bearerToken("estudiante", RolUsuario.ESTUDIANTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void docentePuedeConsultarNotas() throws Exception {
        mockMvc.perform(get("/api/notas")
                        .header("Authorization", bearerToken("docente", RolUsuario.DOCENTE)))
                .andExpect(status().isOk());
    }

    @Test
    void docentePuedeCrearYEditarNotas() throws Exception {
        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken("docente", RolUsuario.DOCENTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inscripcionId": %d,
                                  "cicloAcademico": "2026-2",
                                  "zona": 50,
                                  "examenFinal": 40,
                                  "estado": "APROBADO"
                                }
                                """.formatted(inscripcion.getId())))
                .andExpect(status().isCreated());

        Long notaId = notaRepository.findAll().get(0).getId();
        mockMvc.perform(put("/api/notas/{id}", notaId)
                        .header("Authorization", bearerToken("docente", RolUsuario.DOCENTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inscripcionId": %d,
                                  "cicloAcademico": "2026-2",
                                  "zona": 55,
                                  "examenFinal": 40,
                                  "estado": "APROBADO"
                                }
                                """.formatted(inscripcion.getId())))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
            "docente, DOCENTE",
            "estudiante, ESTUDIANTE"
    })
    void docenteYEstudianteNoPuedenAdministrarUsuarios(String username, RolUsuario rol)
            throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", bearerToken(username, rol)))
                .andExpect(status().isForbidden());
    }

    private UsuarioEntity usuario(String username, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(PASSWORD));
        usuario.setEmail(username + "@prueba.test");
        usuario.setNombre(username);
        usuario.setApellido("Prueba");
        usuario.setActivo(true);
        usuario.setRol(rol);
        return usuario;
    }

    private String bearerToken(String username, RolUsuario rol) {
        return "Bearer " + jwtService.generateToken(
                org.springframework.security.core.userdetails.User.withUsername(username)
                        .password("irrelevante")
                        .authorities("ROLE_" + rol.name())
                        .build());
    }
}
