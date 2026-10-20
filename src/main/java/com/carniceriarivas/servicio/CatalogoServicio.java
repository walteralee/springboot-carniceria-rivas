package com.carniceriarivas.servicio;

import com.carniceriarivas.dto.ProductoRespuesta;
import com.carniceriarivas.modelo.Categoria;
import com.carniceriarivas.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consulta del catálogo de productos.
 */
@Service
public class CatalogoServicio {

    private final ProductoRepositorio productos;

    public CatalogoServicio(ProductoRepositorio productos) {
        this.productos = productos;
    }

    // Todo el catálogo, o solo una categoría si se indica
    @Transactional(readOnly = true)
    public List<ProductoRespuesta> listar(Categoria categoria) {
        var lista = categoria == null
                ? productos.findAllByOrderByIdAsc()
                : productos.findByCategoriaOrderByIdAsc(categoria);

        return lista.stream().map(ProductoRespuesta::de).toList();
    }
}
