package com.carniceriarivas.dto;

import com.carniceriarivas.modelo.LineaPedido;
import com.carniceriarivas.modelo.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pedido tal y como lo devuelve la API, con sus líneas y el total calculado por el servidor.
 */
public record PedidoRespuesta(
        Long id,
        LocalDateTime fecha,
        BigDecimal total,
        List<Linea> lineas) {

    public record Linea(
            Long productoId,
            String nombre,
            BigDecimal precioUnitario,
            int cantidad,
            BigDecimal subtotal) {

        static Linea de(LineaPedido linea) {
            return new Linea(
                    linea.getProductoId(),
                    linea.getNombre(),
                    linea.getPrecioUnitario(),
                    linea.getCantidad(),
                    linea.getSubtotal());
        }
    }

    public static PedidoRespuesta de(Pedido pedido) {
        return new PedidoRespuesta(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getTotal(),
                pedido.getLineas().stream().map(Linea::de).toList());
    }
}
