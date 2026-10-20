package com.carniceriarivas.error;

/**
 * La petición no cumple una regla de la tienda. Se responde con 400.
 */
public class PeticionNoValida extends RuntimeException {

    public PeticionNoValida(String mensaje) {
        super(mensaje);
    }
}
