package com.desarrolloweb.biblioteca.model.service;

import java.util.List;
import com.desarrolloweb.biblioteca.model.entity.Libro;
import com.desarrolloweb.biblioteca.model.entity.Prestamo;
import com.desarrolloweb.biblioteca.model.entity.Usuario;

public interface BibliotecaServiceIface {
    org.springframework.data.domain.Page<Libro> buscarLibrosPaginados(org.springframework.data.domain.Pageable pageable);
    List<Usuario> buscarUsuariosTodos();
    Usuario buscarUsuarioPorId(Long id);
    void guardarUsuario(Usuario usuario);
    void eliminarUsuarioPorId(Long id);

    List<Libro> buscarLibrosTodos();
    List<Libro> buscarLibrosDisponibles();
    Libro buscarLibroPorId(Long id);
    void guardarLibro(Libro libro);
    void eliminarLibroPorId(Long id);

    List<Prestamo> buscarPrestamosTodos();
    Prestamo buscarPrestamoPorId(Long id);
    /**
     * Guarda un préstamo si el usuario tiene menos de 5 préstamos activos.
     * Lanza IllegalStateException si el usuario ya tiene 5 préstamos.
     */
    void guardarPrestamo(Prestamo prestamo);
    void eliminarPrestamoPorId(Long id);
        List<Prestamo> buscarPrestamosPorUsuario(Long usuarioId);
}