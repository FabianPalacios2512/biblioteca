package com.desarrolloweb.biblioteca.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.desarrolloweb.biblioteca.model.entity.Libro;

@Repository
public interface LibroDAO extends JpaRepository<Libro, Long> {
	@Query("SELECT l FROM Libro l WHERE l.id NOT IN (SELECT p.libro.id FROM Prestamo p)")
	java.util.List<Libro> findLibrosDisponibles();
}