package com.desarrolloweb.biblioteca.model.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;

import com.desarrolloweb.biblioteca.model.dao.LibroDAO;
import com.desarrolloweb.biblioteca.model.dao.PrestamoDAO;
import com.desarrolloweb.biblioteca.model.dao.UsuarioDAO;
import com.desarrolloweb.biblioteca.model.entity.Libro;
import com.desarrolloweb.biblioteca.model.entity.Prestamo;
import com.desarrolloweb.biblioteca.model.entity.Usuario;

@Service
public class BibliotecaService implements BibliotecaServiceIface {
    @Override
    @Transactional(readOnly = true)
    public Page<Libro> buscarLibrosPaginados(Pageable pageable) {
        return libroDAO.findAll(pageable);
    }
    private final LibroDAO libroDAO;
    private final UsuarioDAO usuarioDAO;
    private final PrestamoDAO prestamoDAO;
    private final MessageSource messageSource;

    public BibliotecaService(LibroDAO libroDAO, UsuarioDAO usuarioDAO, PrestamoDAO prestamoDAO, MessageSource messageSource) {
        this.libroDAO = libroDAO;
        this.usuarioDAO = usuarioDAO;
        this.prestamoDAO = prestamoDAO;
        this.messageSource = messageSource;
    }

    // Usuario
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosTodos() { return usuarioDAO.findAll(); }
    @Override
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) { return usuarioDAO.findById(id).orElse(null); }
    @Override
    @Transactional
    public void guardarUsuario(Usuario usuario) {
        Usuario existente = usuarioDAO.findByIdentificacion(usuario.getIdentificacion());
        if (existente != null && (usuario.getId() == null || !existente.getId().equals(usuario.getId()))) {
            throw new IllegalStateException(messageSource.getMessage("usuario.identificacion.duplicada", null, java.util.Locale.getDefault()));
        }
        usuarioDAO.save(usuario);
    }
    @Override
    @Transactional
    public void eliminarUsuarioPorId(Long id) {
        long prestamos = prestamoDAO.countByUsuarioId(id);
        if (prestamos > 0) {
            throw new IllegalStateException(messageSource.getMessage("usuario.eliminar.con.prestamos", null, java.util.Locale.getDefault()));
        }
        usuarioDAO.deleteById(id);
    }

    // Libro
    @Override
    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosTodos() { return libroDAO.findAll(); }
    @Override
    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosDisponibles() { return libroDAO.findLibrosDisponibles(); }
    @Override
    @Transactional(readOnly = true)
    public Libro buscarLibroPorId(Long id) { return libroDAO.findById(id).orElse(null); }
    @Override
    @Transactional
    public void guardarLibro(Libro libro) { libroDAO.save(libro); }
    @Override
    @Transactional
    public void eliminarLibroPorId(Long id) { libroDAO.deleteById(id); }

    // Préstamo
    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> buscarPrestamosTodos() { return prestamoDAO.findAll(); }
    @Override
    @Transactional(readOnly = true)
    public Prestamo buscarPrestamoPorId(Long id) { return prestamoDAO.findById(id).orElse(null); }
    @Override
    @Transactional
    public void guardarPrestamo(Prestamo prestamo) {
        Long usuarioId = prestamo.getUsuario().getId();
        Long libroId = prestamo.getLibro().getId();
        long prestamosActivos = prestamoDAO.countByUsuarioId(usuarioId);
        if (prestamosActivos >= 5) {
            throw new IllegalStateException(messageSource.getMessage("prestamo.max.activos", null, java.util.Locale.getDefault()));
        }
        if (prestamoDAO.existsByUsuarioIdAndLibroId(usuarioId, libroId)) {
            throw new IllegalStateException(messageSource.getMessage("prestamo.duplicado", null, java.util.Locale.getDefault()));
        }
        if (prestamo.getFechaPrestamo() == null || prestamo.getFechaDevolucion() == null) {
            throw new IllegalStateException(messageSource.getMessage("prestamo.fechas.obligatorias", null, java.util.Locale.getDefault()));
        }
        if (prestamo.getFechaPrestamo().isAfter(prestamo.getFechaDevolucion())) {
            throw new IllegalStateException(messageSource.getMessage("prestamo.devolucion.anterior", null, java.util.Locale.getDefault()));
        }
        if (prestamo.getFechaPrestamo().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException(messageSource.getMessage("prestamo.prestamo.anterior", null, java.util.Locale.getDefault()));
        }
        prestamoDAO.save(prestamo);
    }
    @Override
    @Transactional
    public void eliminarPrestamoPorId(Long id) { prestamoDAO.deleteById(id); }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Prestamo> buscarPrestamosPorUsuario(Long usuarioId) {
        return prestamoDAO.findByUsuarioId(usuarioId);
    }
}