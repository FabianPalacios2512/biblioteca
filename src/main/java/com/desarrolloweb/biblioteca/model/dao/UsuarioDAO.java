package com.desarrolloweb.biblioteca.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.desarrolloweb.biblioteca.model.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
	@Query("SELECT u FROM Usuario u WHERE u.identificacion = :identificacion")
	Usuario findByIdentificacion(@Param("identificacion") String identificacion);
}