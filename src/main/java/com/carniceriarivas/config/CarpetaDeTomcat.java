package com.carniceriarivas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Tomcat (el servidor web que lleva Spring Boot) crea carpetas de trabajo al arrancar.
 * Por defecto van a la carpeta temporal del sistema y se quedan allí; con esto van a datos/temp.
 */
@Component
public class CarpetaDeTomcat implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final String carpetaDatos;

    public CarpetaDeTomcat(@Value("${carniceria.datos:datos}") String carpetaDatos) {
        this.carpetaDatos = carpetaDatos;
    }

    @Override
    public void customize(TomcatServletWebServerFactory tomcat) {
        File temporal = new File(carpetaDatos, "temp");

        File trabajo = new File(temporal, "tomcat");
        File documentos = new File(temporal, "tomcat-docbase");
        trabajo.mkdirs();
        documentos.mkdirs();

        tomcat.setBaseDirectory(trabajo);
        tomcat.setDocumentRoot(documentos);
    }
}
