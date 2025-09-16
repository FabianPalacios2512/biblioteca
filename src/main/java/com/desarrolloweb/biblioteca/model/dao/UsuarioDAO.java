package com.desarrolloweb.biblioteca.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.desarrolloweb.biblioteca.model.entity.Usuario;

@Repository
public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
}