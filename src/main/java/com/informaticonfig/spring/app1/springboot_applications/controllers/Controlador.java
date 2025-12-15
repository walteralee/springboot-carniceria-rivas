package com.informaticonfig.spring.app1.springboot_applications.controllers;

import com.informaticonfig.spring.app1.springboot_applications.model.Producto;
import com.informaticonfig.spring.app1.springboot_applications.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class Controlador {

    private final ProductoRepository repo;

    public Controlador(ProductoRepository repo) {
        this.repo = repo;
    }

    // Insertar varios productos
    @PostMapping("/comprar")
    public List<Producto> comprar(@RequestBody List<Producto> productos) {
        return productos.stream().map(repo::save).toList();
    }

}
