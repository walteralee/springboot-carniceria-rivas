package com.carniceriarivas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

/**
 * Producto del catálogo de la carnicería (tabla producto).
 */
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // Precio en euros por ración (platos) o por kilo (cortes)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Ruta de la imagen dentro de la web, por ejemplo "imagenes/pl1.png"
    private String imagen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    protected Producto() {
        // Constructor vacío que necesita JPA
    }

    public Producto(String nombre, String descripcion, BigDecimal precio, String imagen, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
