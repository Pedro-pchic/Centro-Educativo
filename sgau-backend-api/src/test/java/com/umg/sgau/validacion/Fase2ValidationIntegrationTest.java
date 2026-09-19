package com.umg.sgau.validacion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.umg.sgau.SgauBackendApiApplication;
import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.carrera.repository.CarreraRepository;
import com.umg.sgau.curso.entity.CursoEntity;
import com.umg.sgau.curso.repository.CursoRepository;
import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;
import com.umg.sgau.inscripcion.entity.InscripcionEntity;
import com.umg.sgau.inscripcion.repository.InscripcionRepository;
import com.umg.sgau.nota.entity.NotaEntity;
import com.umg.sgau.nota.repository.NotaRepository;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import com.umg.sgau.security.JwtService;
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
                "spring.datasource.url=jdbc:h2:mem:fase2;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class Fase2ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private UsuarioEntity admin;
    private EstudianteEntity estudiante;
    private DocenteEntity docente;
    private InscripcionEntity inscripcion;
    private NotaEntity nota;

    @BeforeEach
    void prepararDatos() {
        notaRepository.deleteAll();
        inscripcionRepository.deleteAll();
        cursoRepository.deleteAll();
        docenteRepository.deleteAll();
        estudianteRepository.deleteAll();
        carreraRepository.deleteAll();
        usuarioRepository.deleteAll();

        admin = usuarioRepository.save(usuario("admin", RolUsuario.ADMIN));

        estudiante = new EstudianteEntity();
        estudiante.setNombre("Ana");
        estudiante.setApellido("Prueba");
        estudiante.setEmail("ana.fase2@test.local");
        estudiante.setCodigoEstudiante("EST-F2");
        estudiante.setCarnet("CAR-F2");
        estudiante = estudianteRepository.save(estudiante);

        docente = new DocenteEntity();
        docente.setNombre("Carlos");
        docente.setApellido("Prueba");
        docente.setEmailInstitucional("docente.fase2@test.local");
        docente.setEmailPersonal("docente.personal.fase2@test.local");
        docente.setDpi("2000000000001");
        docente.setFechaContratacion(LocalDate.of(2020, 1, 1));
        docente = docenteRepository.save(docente);

        CarreraEntity carrera = carreraRepository.save(CarreraEntity.builder()
                .codigo("CAR-F2")
                .nombre("Carrera Fase 2")
                .build());

        CursoEntity curso = cursoRepository.save(CursoEntity.builder()
                .codigo("CUR-F2")
                .nombre("Curso Fase 2")
                .creditos(3)
                .carrera(carrera)
                .docente(docente)
                .build());

        inscripcion = new InscripcionEntity();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(LocalDate.of(2026, 1, 15));
        inscripcion = inscripcionRepository.save(inscripcion);

        nota = new NotaEntity();
        nota.setInscripcion(inscripcion);
        nota.setCicloAcademico("2026-1");
        nota.setZona(new BigDecimal("40.00"));
        nota.setExamenFinal(new BigDecimal("50.00"));
        nota.setEstado("APROBADO");
        nota.setActivo(true);
        nota = notaRepository.save(nota);
    }

    @Test
    void requestsDeEstudianteDocenteYUsuarioInvalidosDevuelven400() throws Exception {
        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/docentes")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void notaRechazaValoresNegativosEnCreateYUpdate() throws Exception {
        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("-1", "50", inscripcion.getId())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/notas/{id}", nota.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("40", "-1", inscripcion.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void notaAceptaLimitesAcademicosYConservaNotaFinalEnCreateYUpdate()
            throws Exception {
        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("0", "0", crearInscripcionSinNota())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notaFinal").value(0));

        mockMvc.perform(put("/api/notas/{id}", nota.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("70", "30", inscripcion.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaFinal").value(100));
    }

    @Test
    void notaRechazaValoresSobreLosMaximosEnCreateYUpdate() throws Exception {
        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("71", "30", crearInscripcionSinNota())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/notas/{id}", nota.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("70", "31", inscripcion.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void docenteEliminadoSeConservaComoInactivoYNoSeAdministraComoActivo()
            throws Exception {
        mockMvc.perform(delete("/api/docentes/{id}", docente.getId())
                        .header("Authorization", bearerToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/docentes/{id}", docente.getId())
                        .header("Authorization", bearerToken()))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/docentes/{id}", docente.getId())
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Nuevo",
                                  "apellido": "Nombre",
                                  "dpi": "2000000000001",
                                  "emailInstitucional": "nuevo.fase2@test.local",
                                  "emailPersonal": "nuevo.personal.fase2@test.local",
                                  "fechaContratacion": "2020-01-01"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void datosValidosContinuanSiendoAceptados() throws Exception {
        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest("45", "30", crearInscripcionSinNota())))
                .andExpect(status().isCreated());
    }

    private Long crearInscripcionSinNota() {
        CursoEntity cursoNuevo = cursoRepository.save(CursoEntity.builder()
                .codigo("CUR-F2-B")
                .nombre("Curso Fase 2 B")
                .creditos(3)
                .carrera(inscripcion.getCurso().getCarrera())
                .docente(docente)
                .build());

        InscripcionEntity nueva = new InscripcionEntity();
        nueva.setEstudiante(estudiante);
        nueva.setCurso(cursoNuevo);
        nueva.setFechaInscripcion(LocalDate.of(2026, 2, 1));
        return inscripcionRepository.save(nueva).getId();
    }

    private String notaRequest(String zona, String examen, Long inscripcionId) {
        return """
                {
                  "inscripcionId": %d,
                  "cicloAcademico": "2026-1",
                  "zona": %s,
                  "examenFinal": %s,
                  "estado": "APROBADO"
                }
                """.formatted(inscripcionId, zona, examen);
    }

    private UsuarioEntity usuario(String username, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode("Secreto123"));
        usuario.setEmail(username + "@fase2.test");
        usuario.setNombre(username);
        usuario.setApellido("Prueba");
        usuario.setActivo(true);
        usuario.setRol(rol);
        return usuario;
    }

    private String bearerToken() {
        return "Bearer " + jwtService.generateToken(
                User.withUsername(admin.getUsername())
                        .password("irrelevante")
                        .authorities("ROLE_ADMIN")
                        .build());
    }
}
