package com.desarrolloweb.biblioteca.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.desarrolloweb.biblioteca.model.entity.Prestamo;

@Repository
public interface PrestamoDAO extends JpaRepository<Prestamo, Long> {
	long countByUsuarioId(Long usuarioId);

	boolean existsByUsuarioIdAndLibroId(Long usuarioId, Long libroId);

	java.util.List<Prestamo> findByUsuarioId(Long usuarioId);
}