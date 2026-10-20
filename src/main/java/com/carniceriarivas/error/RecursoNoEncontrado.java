package com.carniceriarivas.error;

/**
 * Lo pedido no existe (por ejemplo, un producto que no está en el catálogo). Se responde con 404.
 */
public class RecursoNoEncontrado extends RuntimeException {

    public RecursoNoEncontrado(String mensaje) {
        super(mensaje);
    }
}
