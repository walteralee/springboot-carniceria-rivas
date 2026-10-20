package com.carniceriarivas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

/**
 * Cuando la aplicación termina de arrancar, abre la web en el navegador.
 * Solo si carniceria.abrir-navegador=true (lo activan el .exe de Windows y run.bat / run.sh).
 */
@Component
public class AbrirNavegador {

    private static final Logger log = LoggerFactory.getLogger(AbrirNavegador.class);

    private final boolean activado;

    public AbrirNavegador(@Value("${carniceria.abrir-navegador:false}") boolean activado) {
        this.activado = activado;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void abrir(ApplicationReadyEvent evento) {
        int puerto = evento.getApplicationContext() instanceof ServletWebServerApplicationContext web
                ? web.getWebServer().getPort()
                : 8080;
        String url = "http://localhost:" + puerto;

        System.out.println();
        System.out.println("  Carniceria Rivas funcionando en " + url);
        System.out.println("  Cierra esta ventana para apagarla.");
        System.out.println();

        if (!activado) {
            return;
        }

        String sistema = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        ProcessBuilder comando;
        if (sistema.contains("win")) {
            comando = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
        } else if (sistema.contains("mac")) {
            comando = new ProcessBuilder("open", url);
        } else {
            comando = new ProcessBuilder("xdg-open", url);
        }

        try {
            comando.start();
        } catch (IOException e) {
            log.info("No se pudo abrir el navegador. Entra a mano en {}", url);
        }
    }
}
