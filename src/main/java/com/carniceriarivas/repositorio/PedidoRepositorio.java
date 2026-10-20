package com.carniceriarivas.repositorio;

import com.carniceriarivas.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla pedido (y a sus líneas).
 */
public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

    // Los 10 pedidos más recientes (sus líneas se cargan después en una sola consulta, ver @BatchSize en Pedido)
    List<Pedido> findTop10ByOrderByIdDesc();
}
