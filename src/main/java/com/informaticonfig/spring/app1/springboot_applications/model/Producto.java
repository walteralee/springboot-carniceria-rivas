package com.informaticonfig.spring.app1.springboot_applications.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(nullable = false)
    private Double precio;

    public Producto() {}

    public Producto(String titulo, String urlImagen, Double precio) {
        this.titulo = titulo;
        this.urlImagen = urlImagen;
        this.precio = precio;
    }

    public Long getId() { return id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getUrlImagen() { return urlImagen; }

    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Double getPrecio() { return precio; }

    public void setPrecio(Double precio) { this.precio = precio; }
}
