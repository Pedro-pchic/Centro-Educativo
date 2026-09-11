package com.umg.sgau.auth.service;

import com.umg.sgau.auth.dto.LoginRequestDTO;
import com.umg.sgau.auth.dto.LoginResponseDTO;
import com.umg.sgau.security.CustomUserDetailsService;
import com.umg.sgau.security.JwtService;
import com.umg.sgau.usuario.entity.RolUsuario;
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

    public AuthService(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            if (!userDetails.isEnabled()
                    || !passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
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
}
