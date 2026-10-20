package com.carniceriarivas.dto;

import com.carniceriarivas.modelo.Categoria;
import com.carniceriarivas.modelo.Producto;

import java.math.BigDecimal;

/**
 * Producto tal y como lo devuelve la API.
 */
public record ProductoRespuesta(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String imagen,
        Categoria categoria) {

    public static ProductoRespuesta de(Producto producto) {
        return new ProductoRespuesta(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getImagen(),
                producto.getCategoria());
    }
}
