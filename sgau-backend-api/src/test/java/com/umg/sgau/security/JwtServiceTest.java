package com.umg.sgau.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

class JwtServiceTest {

    private static final String SECRET =
            "clave-de-prueba-segura-de-al-menos-32-caracteres";

    @Test
    void generaYValidaTokenConSubjectYExpiracion() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        var user = User.withUsername("admin")
                .password("hash")
                .authorities("USER")
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("admin", jwtService.extractUsername(token));
        assertTrue(jwtService.extractExpiration(token).after(new Date()));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void rechazaTokenDeOtroUsuario() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        var emisor = User.withUsername("admin").password("hash").authorities("USER").build();
        var otro = User.withUsername("docente").password("hash").authorities("USER").build();

        assertFalse(jwtService.isTokenValid(jwtService.generateToken(emisor), otro));
    }
}
