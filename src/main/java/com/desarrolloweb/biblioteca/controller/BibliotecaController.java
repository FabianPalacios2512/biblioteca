
package com.desarrolloweb.biblioteca.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.desarrolloweb.biblioteca.model.entity.Usuario;
import com.desarrolloweb.biblioteca.model.entity.Libro;
import com.desarrolloweb.biblioteca.model.entity.Prestamo;
import com.desarrolloweb.biblioteca.model.service.BibliotecaServiceIface;

@Controller
@RequestMapping("/biblioteca")
@SessionAttributes({"usuario", "libro", "prestamo"})
public class BibliotecaController {

    private final BibliotecaServiceIface bibliotecaService;

    public BibliotecaController(BibliotecaServiceIface bibliotecaService) {
        this.bibliotecaService = bibliotecaService;
    }

    // --- MENÚ PRINCIPAL ---
    @GetMapping({"", "/", "/menu"})
    public String menuPrincipal() {
        return "menu/menu_principal";
    }

    // --- USUARIOS ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = bibliotecaService.buscarUsuariosTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("titulo", "Listado de Usuarios");
        return "usuario/listado_usuarios";
    }

    @GetMapping("/usuario/{id}")
    public String consultarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = bibliotecaService.buscarUsuarioPorId(id);
        java.util.List<Prestamo> prestamos = bibliotecaService.buscarPrestamosPorUsuario(id);
        java.util.List<java.util.Map<String, Object>> librosPrestados = new java.util.ArrayList<>();
        java.time.LocalDate hoy = java.time.LocalDate.now();
        for (Prestamo p : prestamos) {
            java.util.Map<String, Object> libroInfo = new java.util.HashMap<>();
            libroInfo.put("titulo", p.getLibro().getTitulo());
            libroInfo.put("fechaDevolucion", p.getFechaDevolucion());
            libroInfo.put("diasRestantes", java.time.temporal.ChronoUnit.DAYS.between(hoy, p.getFechaDevolucion()));
            librosPrestados.add(libroInfo);
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("librosPrestados", librosPrestados);
        model.addAttribute("titulo", "Consulta de Usuario");
        return "usuario/consulta_usuario";
    }

    @GetMapping("/usuario/nuevo")
    public String formUsuarioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("accion", "Crear");
        model.addAttribute("titulo", "Nuevo Usuario");
        return "usuario/formulario_usuario";
    }

    @PostMapping("/usuario/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            bibliotecaService.guardarUsuario(usuario);
            return "redirect:/biblioteca/usuarios";
        } catch (IllegalStateException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("accion", usuario.getId() == null ? "Crear" : "Editar");
            model.addAttribute("titulo", usuario.getId() == null ? "Nuevo Usuario" : "Editar Usuario");
            model.addAttribute("errorUsuario", e.getMessage());
            return "usuario/formulario_usuario";
        }
    }

    @GetMapping("/usuario/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            bibliotecaService.eliminarUsuarioPorId(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado correctamente.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/biblioteca/usuarios";
    }

    // --- LIBROS ---
    @GetMapping("/libros")
    public String listarLibros(@RequestParam(defaultValue = "0") int page, Model model) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 7);
        org.springframework.data.domain.Page<Libro> pageLibros = bibliotecaService.buscarLibrosPaginados(pageable);
        model.addAttribute("libros", pageLibros.getContent());
        model.addAttribute("page", pageLibros);
        model.addAttribute("titulo", "Listado de Libros");
        return "libro/listado_libros";
    }

    @GetMapping("/libro/{id}")
    public String consultarLibro(@PathVariable Long id, Model model) {
        Libro libro = bibliotecaService.buscarLibroPorId(id);
        model.addAttribute("libro", libro);
        model.addAttribute("titulo", "Consulta de Libro");
        return "libro/consulta_libro";
    }

    @GetMapping("/libro/nuevo")
    public String formLibroNuevo(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("accion", "Crear");
        model.addAttribute("titulo", "Nuevo Libro");
        return "libro/formulario_libro";
    }

    @PostMapping("/libro/guardar")
    public String guardarLibro(@ModelAttribute Libro libro) {
        bibliotecaService.guardarLibro(libro);
        return "redirect:/biblioteca/libros";
    }

    @GetMapping("/libro/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id) {
        bibliotecaService.eliminarLibroPorId(id);
        return "redirect:/biblioteca/libros";
    }

    // --- PRÉSTAMOS ---
    @GetMapping("/prestamos")
    public String listarPrestamos(Model model) {
        List<Prestamo> prestamos = bibliotecaService.buscarPrestamosTodos();
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("titulo", "Listado de Préstamos");
        return "prestamo/listado_prestamos";
    }

    @GetMapping("/prestamo/{id}")
    public String consultarPrestamo(@PathVariable Long id, Model model) {
        Prestamo prestamo = bibliotecaService.buscarPrestamoPorId(id);
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("titulo", "Consulta de Préstamo");
        return "prestamo/consulta_prestamo";
    }

    @GetMapping("/prestamo/nuevo")
    public String formPrestamoNuevo(Model model) {
        Prestamo prestamo = new Prestamo();
        prestamo.setFechaPrestamo(java.time.LocalDate.now());
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("accion", "Crear");
        model.addAttribute("usuarios", bibliotecaService.buscarUsuariosTodos());
        model.addAttribute("libros", bibliotecaService.buscarLibrosDisponibles());
        model.addAttribute("titulo", "Nuevo Préstamo");
        return "prestamo/formulario_prestamo";
    }

    @PostMapping("/prestamo/guardar")
    public String guardarPrestamo(@ModelAttribute Prestamo prestamo, Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            bibliotecaService.guardarPrestamo(prestamo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Datos guardados correctamente.");
            return "redirect:/biblioteca/prestamos";
        } catch (IllegalStateException e) {
            // Si el usuario tiene el máximo de préstamos, mostrar mensaje en el formulario
            Prestamo nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setFechaPrestamo(java.time.LocalDate.now());
            nuevoPrestamo.setUsuario(prestamo.getUsuario());
            nuevoPrestamo.setLibro(prestamo.getLibro());
            nuevoPrestamo.setFechaDevolucion(prestamo.getFechaDevolucion());
            model.addAttribute("prestamo", nuevoPrestamo);
            model.addAttribute("accion", "Crear");
            model.addAttribute("usuarios", bibliotecaService.buscarUsuariosTodos());
            model.addAttribute("libros", bibliotecaService.buscarLibrosDisponibles());
            model.addAttribute("titulo", "Nuevo Préstamo");
            model.addAttribute("errorPrestamo", e.getMessage());
            return "prestamo/formulario_prestamo";
        }
    }
    // ...existing code...

    @GetMapping("/prestamo/eliminar/{id}")
    public String eliminarPrestamo(@PathVariable Long id) {
        bibliotecaService.eliminarPrestamoPorId(id);
        return "redirect:/biblioteca/prestamos";
    }
}