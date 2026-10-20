package com.carniceriarivas.controlador;

import com.carniceriarivas.dto.ProductoRespuesta;
import com.carniceriarivas.modelo.Categoria;
import com.carniceriarivas.servicio.CatalogoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints del catálogo. Solo recibe la petición y llama al servicio.
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de la carnicería")
public class ProductoControlador {

    private final CatalogoServicio catalogo;

    public ProductoControlador(CatalogoServicio catalogo) {
        this.catalogo = catalogo;
    }

    // GET /api/productos            -> todo el catálogo
    // GET /api/productos?categoria=PLATO|CORTE
    @GetMapping
    @Operation(summary = "Lista el catálogo, opcionalmente filtrado por categoría (PLATO o CORTE)")
    public List<ProductoRespuesta> listar(@RequestParam(required = false) Categoria categoria) {
        return catalogo.listar(categoria);
    }
}
