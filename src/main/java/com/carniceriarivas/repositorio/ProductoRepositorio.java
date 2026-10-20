package com.carniceriarivas.repositorio;

import com.carniceriarivas.modelo.Categoria;
import com.carniceriarivas.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla producto. Spring Data genera las consultas a partir del nombre de los métodos.
 */
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    List<Producto> findAllByOrderByIdAsc();

    List<Producto> findByCategoriaOrderByIdAsc(Categoria categoria);
}
