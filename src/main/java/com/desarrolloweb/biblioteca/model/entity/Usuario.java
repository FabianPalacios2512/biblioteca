package com.desarrolloweb.biblioteca.model.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false, unique = true)
    @NotEmpty(message = "usuario.identificacion.obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "usuario.identificacion.numeros")
    private String identificacion;

    @Column(length = 15, nullable = false)
    @NotEmpty(message = "usuario.telefono.obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "usuario.telefono.numeros")
    private String telefonoMovil;

    @Column(length = 80, nullable = false)
    @NotEmpty(message = "usuario.nombre.obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "usuario.nombre.letras")
    private String nombreCompleto;

    @Column(length = 100, nullable = false)
    @Email
    @NotEmpty
    private String correoElectronico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    
}