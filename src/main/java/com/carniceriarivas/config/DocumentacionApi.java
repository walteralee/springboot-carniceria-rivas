package com.carniceriarivas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Título y descripción que muestra Swagger en /swagger-ui.html
 */
@Configuration
public class DocumentacionApi {

    @Bean
    public OpenAPI informacionDeLaApi() {
        return new OpenAPI().info(new Info()
                .title("Carnicería Rivas API")
                .version("1.0.0")
                .description("Catálogo de la carnicería y pedidos hechos desde el carrito. "
                        + "El total de cada pedido lo calcula el servidor con los precios del catálogo.")
                .contact(new Contact()
                        .name("Walter Alejandro Cutiño Ledo")
                        .url("https://github.com/walteralee")));
    }
}
