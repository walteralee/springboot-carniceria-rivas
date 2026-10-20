package com.carniceriarivas.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Decide en qué carpeta se guardan la base de datos SQLite y los archivos temporales:
 * <ul>
 *   <li>Versión de Windows (.exe): carpeta "datos" junto al programa.</li>
 *   <li>Desde el código (run.bat / mvnw) o Docker: carpeta "datos" en la carpeta de trabajo.</li>
 *   <li>Se puede cambiar con -Dcarniceria.datos=ruta.</li>
 * </ul>
 * Así todo lo que crea la aplicación queda en una sola carpeta y no se ensucia la carpeta temporal del sistema.
 */
public final class CarpetaDeDatos {

    public static final String PROPIEDAD = "carniceria.datos";

    private CarpetaDeDatos() {
    }

    public static Path preparar() {
        Path carpeta = elegirCarpeta().toAbsolutePath().normalize();
        Path temporal = carpeta.resolve("temp");

        try {
            Files.createDirectories(temporal);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de datos " + carpeta, e);
        }

        // application.properties usa esta propiedad para la ruta del archivo .db
        System.setProperty(PROPIEDAD, carpeta.toString().replace('\\', '/'));

        // Tomcat y el driver de SQLite dejan sus archivos temporales aquí, no en la carpeta temporal del sistema
        System.setProperty("java.io.tmpdir", temporal.toString());
        System.setProperty("org.sqlite.tmpdir", temporal.toString());

        return carpeta;
    }

    private static Path elegirCarpeta() {
        String configurada = System.getProperty(PROPIEDAD);
        if (configurada != null && !configurada.isBlank()) {
            return Path.of(configurada);
        }

        // jpackage pone en esta propiedad la ruta del .exe que se ha abierto
        String ejecutable = System.getProperty("jpackage.app-path");
        if (ejecutable != null) {
            return Path.of(ejecutable).toAbsolutePath().getParent().resolve("datos");
        }

        return Path.of("datos");
    }
}
