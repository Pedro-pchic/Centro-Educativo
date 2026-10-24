package com.umg.sgau.auth.service;

import com.umg.sgau.auth.dto.LoginRequestDTO;
import com.umg.sgau.auth.dto.LoginResponseDTO;
import com.umg.sgau.auth.dto.PublicKeyResponseDTO;
import com.umg.sgau.auth.exception.InvalidEncryptedPasswordException;
import com.umg.sgau.security.CustomUserDetailsService;
import com.umg.sgau.security.JwtService;
import com.umg.sgau.security.RsaCryptoService;
import com.umg.sgau.usuario.entity.RolUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RsaCryptoService rsaCryptoService;
    private final boolean allowLegacyPassword;

    public AuthService(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RsaCryptoService rsaCryptoService,
            @Value("${app.auth.login.allow-legacy-password:false}")
            boolean allowLegacyPassword) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rsaCryptoService = rsaCryptoService;
        this.allowLegacyPassword = allowLegacyPassword;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        String password = resolvePassword(request);
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            if (!userDetails.isEnabled()
                    || !passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Credenciales inválidas");
            }

            String token = jwtService.generateToken(userDetails);
            RolUsuario rol = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring("ROLE_".length()))
                    .map(RolUsuario::valueOf)
                    .findFirst()
                    .orElseThrow(() -> new BadCredentialsException("Usuario sin rol válido"));

            return new LoginResponseDTO(token, "Bearer", userDetails.getUsername(), rol);
        } catch (UsernameNotFoundException exception) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    public PublicKeyResponseDTO publicKey() {
        return new PublicKeyResponseDTO(
                rsaCryptoService.getPublicKeyBase64(),
                "RSA-OAEP",
                "SHA-256",
                "SPKI");
    }

    private String resolvePassword(LoginRequestDTO request) {
        boolean encryptedFieldPresent = request.getPasswordEncrypted() != null;
        boolean legacyFieldPresent = request.getPassword() != null;

        if (encryptedFieldPresent && legacyFieldPresent) {
            throw new InvalidEncryptedPasswordException();
        }
        if (encryptedFieldPresent && hasText(request.getPasswordEncrypted())) {
            return rsaCryptoService.decryptPassword(request.getPasswordEncrypted());
        }
        if (legacyFieldPresent && allowLegacyPassword && hasText(request.getPassword())) {
            return request.getPassword();
        }
        throw new InvalidEncryptedPasswordException();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
