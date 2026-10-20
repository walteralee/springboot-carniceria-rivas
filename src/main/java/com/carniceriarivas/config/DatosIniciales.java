package com.carniceriarivas.config;

import com.carniceriarivas.modelo.Categoria;
import com.carniceriarivas.modelo.Producto;
import com.carniceriarivas.repositorio.ProductoRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

/**
 * Configuración general: el reloj de la aplicación y el catálogo inicial.
 */
@Configuration
public class DatosIniciales {

    // Reloj del sistema (en los tests se puede sustituir por uno fijo)
    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    // La primera vez que se arranca, con la base de datos vacía, se carga el catálogo de la carnicería
    @Bean
    public CommandLineRunner cargarCatalogo(ProductoRepositorio productos) {
        return args -> {
            if (productos.count() > 0) {
                return;
            }

            productos.saveAll(List.of(
                    // Platos (precio por ración)
                    new Producto("Entrecot a la Parrilla",
                            "Delicioso entrecot de ternera, asado a la perfección, con jugos naturales y un toque de sal gruesa. Ideal para los amantes de la carne.",
                            new BigDecimal("18.50"), "imagenes/pl1.png", Categoria.PLATO),
                    new Producto("Solomillo a la Plancha",
                            "Tierna pieza de solomillo de ternera, dorada a la plancha y servida con un toque de mantequilla de hierbas. Sabor intenso y jugoso.",
                            new BigDecimal("22.00"), "imagenes/pl2.png", Categoria.PLATO),
                    new Producto("Costillas BBQ de Ternera",
                            "Costillas de ternera ahumadas y glaseadas con nuestra salsa BBQ casera, tiernas y llenas de sabor en cada bocado.",
                            new BigDecimal("16.90"), "imagenes/pl3.png", Categoria.PLATO),
                    new Producto("Chuletón a la Brasa",
                            "Chuletón de ternera asado a la brasa, con corte grueso y jugoso, acompañado de su propio jugo natural y un toque de pimienta negra.",
                            new BigDecimal("27.50"), "imagenes/pl4.png", Categoria.PLATO),

                    // Cortes del mostrador (precio por kilo)
                    new Producto("Lomo Alto",
                            "Lomo alto de ternera fresca, con vetas de grasa ideales para cocinar a la parrilla o en sartén. Calidad premium del carnicero.",
                            new BigDecimal("24.90"), "imagenes/c1.png", Categoria.CORTE),
                    new Producto("Entrecot",
                            "Entrecot fresco de vaca, cortado en pieza gruesa, perfecto para preparar en casa al gusto del comensal.",
                            new BigDecimal("21.50"), "imagenes/c2.png", Categoria.CORTE),
                    new Producto("Solomillo de Ternera",
                            "Solomillo tierno y jugoso, de vaca de primera calidad. Ideal para preparar filetes o medallones en casa.",
                            new BigDecimal("34.00"), "imagenes/c3.png", Categoria.CORTE),
                    new Producto("Chuletón",
                            "Chuletón de vaca fresco, con su hueso y grasa natural. Cortado y listo para cocinar según el gusto del chef.",
                            new BigDecimal("29.90"), "imagenes/c1.png", Categoria.CORTE)
            ));
        };
    }
}
