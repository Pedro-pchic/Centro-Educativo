package com.umg.sgau.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umg.sgau.SgauBackendApiApplication;
import com.umg.sgau.security.JwtService;
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = SgauBackendApiApplication.class,
        properties = {
                "jwt.secret=clave-de-prueba-segura-de-al-menos-32-caracteres",
                "spring.datasource.url=jdbc:h2:mem:authrsa;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "app.auth.rsa.allow-ephemeral=true",
                "app.auth.login.allow-legacy-password=false"
        })
@AutoConfigureMockMvc
class AuthRsaIntegrationTest {

    private static final String PASSWORD = "Secreto123";
    private static final OAEPParameterSpec OAEP_SHA256 = new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    private String publicKeyBase64;

    @BeforeEach
    void prepararDatos() throws Exception {
        usuarioRepository.deleteAll();
        usuarioRepository.save(usuario("admin", RolUsuario.ADMIN));
        usuarioRepository.save(usuario("docente", RolUsuario.DOCENTE));
        usuarioRepository.save(usuario("estudiante", RolUsuario.ESTUDIANTE));
        UsuarioEntity inactivo = usuario("inactivo", RolUsuario.ESTUDIANTE);
        inactivo.setActivo(false);
        usuarioRepository.save(inactivo);

        String response = mockMvc.perform(get("/api/auth/public-key"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        publicKeyBase64 = objectMapper.readTree(response).get("publicKey").asText();
    }

    @Test
    void publicaUnaClaveSpkiCompatibleConWebCrypto() throws Exception {
        mockMvc.perform(get("/api/auth/public-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicKey").isString())
                .andExpect(jsonPath("$.algorithm").value("RSA-OAEP"))
                .andExpect(jsonPath("$.hash").value("SHA-256"))
                .andExpect(jsonPath("$.format").value("SPKI"))
                .andExpect(jsonPath("$.privateKey").doesNotExist());

        PublicKey publicKey = decodePublicKey(publicKeyBase64);
        org.junit.jupiter.api.Assertions.assertTrue(
                ((java.security.interfaces.RSAPublicKey) publicKey)
                        .getModulus().bitLength() >= 2048);
    }

    @ParameterizedTest
    @CsvSource({
            "admin, ADMIN",
            "docente, DOCENTE",
            "estudiante, ESTUDIANTE"
    })
    void loginCifradoConservaElRolYGeneraJwt(String username, String role) throws Exception {
        String encryptedPassword = encrypt(PASSWORD, decodePublicKey(publicKeyBase64));

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "passwordEncrypted": "%s"
                                }
                                """.formatted(username, encryptedPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.rol").value(role))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.hash").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();
        org.junit.jupiter.api.Assertions.assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void passwordCifradaIncorrectaDevuelve401() throws Exception {
        String encryptedPassword = encrypt("incorrecta", decodePublicKey(publicKeyBase64));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "estudiante",
                                  "passwordEncrypted": "%s"
                                }
                                """.formatted(encryptedPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void ciphertextInvalidoDevuelve400SinDetalleCriptografico() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "estudiante",
                                  "passwordEncrypted": "no-es-base64"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solicitud inválida"));
    }

    @Test
    void ciphertextInvalidoNoRevelaSiElUsuarioExiste() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "usuario-que-no-existe",
                                  "passwordEncrypted": "no-es-base64"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solicitud inválida"));
    }

    @Test
    void ciphertextConLongitudIncorrectaDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "estudiante",
                                  "passwordEncrypted": "AA=="
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solicitud inválida"));
    }

    @Test
    void usuarioInexistenteConCiphertextValidoDevuelve401() throws Exception {
        String encryptedPassword = encrypt(PASSWORD, decodePublicKey(publicKeyBase64));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "usuario-que-no-existe",
                                  "passwordEncrypted": "%s"
                                }
                                """.formatted(encryptedPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void usuarioInactivoDevuelve401() throws Exception {
        String encryptedPassword = encrypt(PASSWORD, decodePublicKey(publicKeyBase64));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "inactivo",
                                  "passwordEncrypted": "%s"
                                }
                                """.formatted(encryptedPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void ambosCamposDePasswordSeRechazanAunqueUnoSeaLegacy() throws Exception {
        String encryptedPassword = encrypt(PASSWORD, decodePublicKey(publicKeyBase64));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "estudiante",
                                  "passwordEncrypted": "%s",
                                  "password": "%s"
                                }
                                """.formatted(encryptedPassword, PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solicitud inválida"));
    }

    @Test
    void passwordEncryptedAusenteOVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "estudiante" }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "estudiante",
                                  "passwordEncrypted": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contratoLegadoSeRechazaCuandoLaBanderaEstaDesactivada() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "estudiante",
                                  "password": "%s"
                                }
                                """.formatted(PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegidoSinPermisoDevuelve403() throws Exception {
        UsuarioEntity docente = usuarioRepository.findByUsername("docente").orElseThrow();
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + jwtService.generateToken(
                                org.springframework.security.core.userdetails.User
                                        .withUsername(docente.getUsername())
                                        .password("irrelevante")
                                        .authorities("ROLE_DOCENTE")
                                        .build())))
                .andExpect(status().isForbidden());
    }

    private UsuarioEntity usuario(String username, RolUsuario role) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(PASSWORD));
        usuario.setEmail(username + "@rsa.test");
        usuario.setNombre(username);
        usuario.setApellido("Prueba");
        usuario.setActivo(true);
        usuario.setRol(role);
        return usuario;
    }

    private String encrypt(String password, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, OAEP_SHA256);
        return Base64.getEncoder().encodeToString(
                cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
    }

    private PublicKey decodePublicKey(String key) throws Exception {
        return KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
    }
}
