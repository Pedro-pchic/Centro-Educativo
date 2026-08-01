package com.umg.sgau.usuario.serviceimpl;

import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.UsuarioNoEncontradoException;
import com.umg.sgau.usuario.repository.UsuarioRepository;
import com.umg.sgau.usuario.service.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceimpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceimpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioEntity crear(UsuarioEntity usuario) {

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        return usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioEntity obtenerPorId(Long id) {

        Optional<UsuarioEntity> usuarioEncontrado =
                usuarioRepository.findById(id);

        if (usuarioEncontrado.isEmpty()) {
            throw new UsuarioNoEncontradoException(id);
        }

        UsuarioEntity usuario = usuarioEncontrado.get();

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new UsuarioNoEncontradoException(id);
        }

        return usuario;
    }

    @Override
    public List<UsuarioEntity> obtenerTodos() {

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        Boolean.TRUE.equals(usuario.getActivo()))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioEntity actualizar(
            Long id,
            UsuarioEntity usuario) {

        Optional<UsuarioEntity> usuarioExistente =
                usuarioRepository.findById(id);

        if (usuarioExistente.isEmpty()) {
            throw new UsuarioNoEncontradoException(id);
        }

        UsuarioEntity usuarioActual =
                usuarioExistente.get();

        if (!Boolean.TRUE.equals(usuarioActual.getActivo())) {
            throw new UsuarioNoEncontradoException(id);
        }

        usuarioActual.setUsername(usuario.getUsername());
        usuarioActual.setEmail(usuario.getEmail());
        usuarioActual.setNombre(usuario.getNombre());
        usuarioActual.setApellido(usuario.getApellido());

        return usuarioRepository.save(usuarioActual);
    }

    @Override
    public void eliminar(Long id) {

        Optional<UsuarioEntity> usuarioExistente =
                usuarioRepository.findById(id);

        if (usuarioExistente.isEmpty()) {
            throw new UsuarioNoEncontradoException(id);
        }

        UsuarioEntity usuario =
                usuarioExistente.get();

        usuario.setActivo(false);

        usuarioRepository.save(usuario);
    }

    public List<String> obtenerNombresUsuariosActivos() {

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        Boolean.TRUE.equals(usuario.getActivo()))
                .map(usuario ->
                        usuario.getNombre()
                        + " "
                        + usuario.getApellido())
                .collect(Collectors.toList());
    }
}