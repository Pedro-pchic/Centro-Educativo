package com.umg.sgau.usuario.service;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import java.util.List;

public interface UsuarioService {
	UsuarioEntity crear(UsuarioEntity usuario);
	UsuarioEntity obtenerPorId(Long id);
	List<UsuarioEntity> obtenerTodos();
	UsuarioEntity actualizar(Long id, UsuarioEntity usuario);
	void eliminar(Long id);
}
