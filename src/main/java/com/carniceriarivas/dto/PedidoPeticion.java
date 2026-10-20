package com.carniceriarivas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Cuerpo de POST /api/pedidos: los productos del carrito y cuántos de cada uno.
 * El precio no se envía: lo pone el servidor a partir del catálogo.
 */
public record PedidoPeticion(
        @NotEmpty(message = "El carrito está vacío.")
        List<@Valid @NotNull(message = "Hay una línea vacía en el pedido.") Linea> lineas) {

    public record Linea(
            @NotNull(message = "Falta el producto en una línea del pedido.")
            Long productoId,

            @NotNull(message = "Falta la cantidad en una línea del pedido.")
            @Min(value = 1, message = "La cantidad mínima es 1.")
            @Max(value = 99, message = "La cantidad máxima es 99.")
            Integer cantidad) {
    }
}
