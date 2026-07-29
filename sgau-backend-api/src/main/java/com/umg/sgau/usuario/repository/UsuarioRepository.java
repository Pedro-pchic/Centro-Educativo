package com.umg.sgau.usuario.repository;

import com.umg.sgau.usuario.entity.UsuarioEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long>{
	 Optional<UsuarioEntity> findByEmail(String email);

	    Optional<UsuarioEntity> findByUsername(String username);

	    boolean existsByEmail(String email);

	    boolean existsByUsername(String username);
}
