package com.umg.sgau.security;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.umg.sgau.SgauBackendApiApplication;
import com.umg.sgau.carrera.entity.CarreraEntity;
import com.umg.sgau.carrera.repository.CarreraRepository;
import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.colegiatura.repository.ColegiaturaRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = SgauBackendApiApplication.class,
        properties = {
                "jwt.secret=clave-de-prueba-segura-de-al-menos-32-caracteres",
                "spring.datasource.url=jdbc:h2:mem:adversarial;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class AdversarialAuthorizationIntegrationTest {

    private static final String JWT_SECRET =
            "clave-de-prueba-segura-de-al-menos-32-caracteres";

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
    private ColegiaturaRepository colegiaturaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private UsuarioEntity admin;
    private UsuarioEntity usuarioEstudianteA;
    private UsuarioEntity usuarioEstudianteB;
    private UsuarioEntity usuarioDocenteA;
    private UsuarioEntity usuarioDocenteB;
    private UsuarioEntity usuarioEstudianteSinPerfil;
    private UsuarioEntity usuarioDocenteSinPerfil;
    private UsuarioEntity usuarioInactivo;

    private EstudianteEntity estudianteA;
    private EstudianteEntity estudianteB;
    private DocenteEntity docenteA;
    private DocenteEntity docenteB;
    private CarreraEntity carrera;
    private CursoEntity cursoA;
    private CursoEntity cursoB;
    private InscripcionEntity inscripcionA;
    private InscripcionEntity inscripcionAParaCrear;
    private InscripcionEntity inscripcionB;
    private NotaEntity notaA;
    private NotaEntity notaB;
    private ColegiaturaEntity colegiaturaA;
    private ColegiaturaEntity colegiaturaB;

    @BeforeEach
    void prepararDatos() {
        notaRepository.deleteAll();
        colegiaturaRepository.deleteAll();
        inscripcionRepository.deleteAll();
        cursoRepository.deleteAll();
        docenteRepository.deleteAll();
        estudianteRepository.deleteAll();
        carreraRepository.deleteAll();
        usuarioRepository.deleteAll();

        admin = usuarioRepository.save(usuario("admin", RolUsuario.ADMIN));
        usuarioEstudianteA = usuarioRepository.save(
                usuario("estudiante-a", RolUsuario.ESTUDIANTE));
        usuarioEstudianteB = usuarioRepository.save(
                usuario("estudiante-b", RolUsuario.ESTUDIANTE));
        usuarioDocenteA = usuarioRepository.save(
                usuario("docente-a", RolUsuario.DOCENTE));
        usuarioDocenteB = usuarioRepository.save(
                usuario("docente-b", RolUsuario.DOCENTE));
        usuarioEstudianteSinPerfil = usuarioRepository.save(
                usuario("estudiante-sin-perfil", RolUsuario.ESTUDIANTE));
        usuarioDocenteSinPerfil = usuarioRepository.save(
                usuario("docente-sin-perfil", RolUsuario.DOCENTE));
        usuarioInactivo = usuarioRepository.save(
                usuario("usuario-inactivo", RolUsuario.ESTUDIANTE));

        estudianteA = estudiante("Ana", "A", "estudiante.a@test.local",
                "EST-A", "CAR-A", usuarioEstudianteA);
        estudianteB = estudiante("Bruno", "B", "estudiante.b@test.local",
                "EST-B", "CAR-B", usuarioEstudianteB);
        estudianteA = estudianteRepository.save(estudianteA);
        estudianteB = estudianteRepository.save(estudianteB);

        docenteA = docente("Diana", "A", "docente.a@test.local",
                "docente.a.personal@test.local", "1000000000001", usuarioDocenteA);
        docenteB = docente("Diego", "B", "docente.b@test.local",
                "docente.b.personal@test.local", "1000000000002", usuarioDocenteB);
        docenteA = docenteRepository.save(docenteA);
        docenteB = docenteRepository.save(docenteB);

        carrera = carreraRepository.save(CarreraEntity.builder()
                .codigo("CAR-TEST")
                .nombre("Carrera de prueba")
                .activo(true)
                .build());

        cursoA = cursoRepository.save(CursoEntity.builder()
                .codigo("CURSO-A")
                .nombre("Curso A")
                .creditos(3)
                .carrera(carrera)
                .docente(docenteA)
                .activo(true)
                .build());
        cursoB = cursoRepository.save(CursoEntity.builder()
                .codigo("CURSO-B")
                .nombre("Curso B")
                .creditos(3)
                .carrera(carrera)
                .docente(docenteB)
                .activo(true)
                .build());

        inscripcionA = inscripcionRepository.save(inscripcion(estudianteA, cursoA));
        inscripcionAParaCrear = inscripcionRepository.save(inscripcion(estudianteA, cursoA));
        inscripcionB = inscripcionRepository.save(inscripcion(estudianteB, cursoB));

        notaA = notaRepository.save(nota(inscripcionA, "2026-1", "APROBADO"));
        notaB = notaRepository.save(nota(inscripcionB, "2026-1", "APROBADO"));

        colegiaturaA = colegiaturaRepository.save(
                colegiatura(estudianteA, "ENERO", "2026-1"));
        colegiaturaB = colegiaturaRepository.save(
                colegiatura(estudianteB, "FEBRERO", "2026-1"));
    }

    @Test
    void estudianteSoloPuedeVerSusRecursosYNoColeccionesGlobales() throws Exception {
        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(estudianteA.getId()));

        mockMvc.perform(get("/api/estudiantes/me/inscripciones")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].estudianteId", not(hasItem(estudianteB.getId()))));

        mockMvc.perform(get("/api/estudiantes/me/notas")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estudianteId").value(estudianteA.getId()));

        mockMvc.perform(get("/api/estudiantes/me/colegiaturas")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idEstudiante").value(estudianteA.getId()));

        mockMvc.perform(get("/api/estudiantes/me/promedio")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(85.0));

        mockMvc.perform(get("/api/estudiantes/{id}", estudianteB.getId())
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/inscripciones/{id}", inscripcionB.getId())
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/notas/{id}", notaB.getId())
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/colegiaturas/{id}", colegiaturaB.getId())
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/notas")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void docenteSoloPuedeConsultarYModificarRecursosDeSusCursos() throws Exception {
        mockMvc.perform(get("/api/docentes/me")
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(docenteA.getId()));

        mockMvc.perform(get("/api/docentes/me/cursos")
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(cursoA.getId()));

        mockMvc.perform(get("/api/cursos/{id}", cursoA.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cursos/{id}", cursoB.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/inscripciones/curso/{id}", cursoA.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].estudianteId", not(hasItem(estudianteB.getId()))));

        mockMvc.perform(get("/api/inscripciones/curso/{id}", cursoB.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/inscripciones/{id}", inscripcionA.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/inscripciones/{id}", inscripcionB.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/notas/curso/{id}", cursoA.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estudianteId").value(estudianteA.getId()));

        mockMvc.perform(get("/api/notas/curso/{id}", cursoB.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/notas/{id}", notaA.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notas/{id}", notaB.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken(usuarioDocenteA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest(inscripcionAParaCrear.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/notas")
                        .header("Authorization", bearerToken(usuarioDocenteA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest(inscripcionB.getId())))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/notas/{id}", notaA.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest(inscripcionA.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/notas/{id}", notaB.getId())
                        .header("Authorization", bearerToken(usuarioDocenteA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notaRequest(inscripcionB.getId())))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/docentes")
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/cursos")
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void rolesYPerfilesNoPuedenCruzarNiOmitirLaAsociacion() throws Exception {
        mockMvc.perform(get("/api/docentes/me")
                        .header("Authorization", bearerToken(usuarioEstudianteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", bearerToken(usuarioDocenteA)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", bearerToken(usuarioEstudianteSinPerfil)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/docentes/me")
                        .header("Authorization", bearerToken(usuarioDocenteSinPerfil)))
                .andExpect(status().isForbidden());

        String tokenAntesDeDesactivar = bearerToken(usuarioInactivo);
        usuarioInactivo.setActivo(false);
        usuarioRepository.save(usuarioInactivo);

        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", tokenAntesDeDesactivar))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", bearerToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void autenticacionRechazaTokensAusentesInvalidosAlteradosYExpirados() throws Exception {
        mockMvc.perform(get("/api/estudiantes/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());

        String token = jwtService.generateToken(userDetails(usuarioEstudianteA));
        String tokenAlterado = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", "Bearer " + tokenAlterado))
                .andExpect(status().isUnauthorized());

        String tokenExpirado = new JwtService(JWT_SECRET, -1L)
                .generateToken(userDetails(usuarioEstudianteA));

        mockMvc.perform(get("/api/estudiantes/me")
                        .header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());
    }

    private UsuarioEntity usuario(String username, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode("Secreto123"));
        usuario.setEmail(username + "@test.local");
        usuario.setNombre(username);
        usuario.setApellido("Prueba");
        usuario.setActivo(true);
        usuario.setRol(rol);
        return usuario;
    }

    private EstudianteEntity estudiante(
            String nombre,
            String apellido,
            String email,
            String codigo,
            String carnet,
            UsuarioEntity usuario) {
        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setNombre(nombre);
        estudiante.setApellido(apellido);
        estudiante.setEmail(email);
        estudiante.setCodigoEstudiante(codigo);
        estudiante.setCarnet(carnet);
        estudiante.setActivo(true);
        estudiante.setUsuario(usuario);
        return estudiante;
    }

    private DocenteEntity docente(
            String nombre,
            String apellido,
            String emailInstitucional,
            String emailPersonal,
            String dpi,
            UsuarioEntity usuario) {
        DocenteEntity docente = new DocenteEntity();
        docente.setNombre(nombre);
        docente.setApellido(apellido);
        docente.setEmailInstitucional(emailInstitucional);
        docente.setEmailPersonal(emailPersonal);
        docente.setDpi(dpi);
        docente.setFechaContratacion(LocalDate.of(2020, 1, 1));
        docente.setActivo(true);
        docente.setUsuario(usuario);
        return docente;
    }

    private InscripcionEntity inscripcion(
            EstudianteEntity estudiante,
            CursoEntity curso) {
        InscripcionEntity inscripcion = new InscripcionEntity();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(LocalDate.of(2026, 1, 15));
        inscripcion.setActivo(true);
        return inscripcion;
    }

    private NotaEntity nota(
            InscripcionEntity inscripcion,
            String ciclo,
            String estado) {
        NotaEntity nota = new NotaEntity();
        nota.setInscripcion(inscripcion);
        nota.setCicloAcademico(ciclo);
        nota.setZona(new BigDecimal("55.00"));
        nota.setExamenFinal(new BigDecimal("30.00"));
        nota.setEstado(estado);
        nota.setActivo(true);
        nota.setFechaRegistro(LocalDate.of(2026, 2, 1));
        return nota;
    }

    private ColegiaturaEntity colegiatura(
            EstudianteEntity estudiante,
            String mes,
            String ciclo) {
        ColegiaturaEntity colegiatura = new ColegiaturaEntity();
        colegiatura.setEstudiante(estudiante);
        colegiatura.setMes(mes);
        colegiatura.setCiclo(ciclo);
        colegiatura.setMonto(new BigDecimal("1000.00"));
        colegiatura.setPagado(false);
        colegiatura.setActivo(true);
        return colegiatura;
    }

    private String notaRequest(Long inscripcionId) {
        return """
                {
                  "inscripcionId": %d,
                  "cicloAcademico": "2026-1",
                  "zona": 50.00,
                  "examenFinal": 30.00,
                  "estado": "APROBADO",
                  "fechaRegistro": "2026-02-10"
                }
                """.formatted(inscripcionId);
    }

    private UserDetails userDetails(UsuarioEntity usuario) {
        return User.withUsername(usuario.getUsername())
                .password("irrelevante")
                .authorities("ROLE_" + usuario.getRol().name())
                .build();
    }

    private String bearerToken(UsuarioEntity usuario) {
        return "Bearer " + jwtService.generateToken(userDetails(usuario));
    }
}
