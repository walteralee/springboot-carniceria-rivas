package com.carniceriarivas;

import com.carniceriarivas.config.CarpetaDeDatos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 *
 * Capas: controlador (HTTP) -> servicio (reglas) -> repositorio (JPA) -> SQLite.
 * La web (HTML, CSS y JS) está en src/main/resources/static y la sirve la propia aplicación.
 */
@SpringBootApplication
public class CarniceriaRivasApplication {

    public static void main(String[] args) {
        // Antes de arrancar Spring se decide dónde van la base de datos y los archivos temporales
        CarpetaDeDatos.preparar();
        SpringApplication.run(CarniceriaRivasApplication.class, args);
    }
}
