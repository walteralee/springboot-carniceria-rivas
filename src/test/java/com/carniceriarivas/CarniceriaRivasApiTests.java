package com.carniceriarivas;

import com.carniceriarivas.modelo.Producto;
import com.carniceriarivas.repositorio.PedidoRepositorio;
import com.carniceriarivas.repositorio.ProductoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración: arrancan la aplicación de verdad con una base de datos SQLite temporal
 * (con el catálogo inicial cargado) y prueban la API como lo haría la web.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CarniceriaRivasApiTests {

    private static final Path BASE_DE_DATOS = crearArchivoTemporal();

    @DynamicPropertySource
    static void baseDeDatosTemporal(DynamicPropertyRegistry registro) {
        registro.add("spring.datasource.url", () -> "jdbc:sqlite:" + BASE_DE_DATOS.toString().replace('\\', '/'));
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductoRepositorio productos;

    @Autowired
    private PedidoRepositorio pedidos;

    @BeforeEach
    void empezarSinPedidos() {
        pedidos.deleteAll();
    }

    //--------------------------------------------------------------------------------------------
    // CATÁLOGO

    @Test
    void el_catalogo_inicial_tiene_4_platos_y_4_cortes() throws Exception {
        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)));

        mvc.perform(get("/api/productos").param("categoria", "PLATO"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].nombre").value("Entrecot a la Parrilla"))
                .andExpect(jsonPath("$[0].precio").value(18.50));

        mvc.perform(get("/api/productos").param("categoria", "CORTE"))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void una_categoria_que_no_existe_da_400() throws Exception {
        mvc.perform(get("/api/productos").param("categoria", "PESCADO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("categoria")));
    }

    //--------------------------------------------------------------------------------------------
    // PEDIDOS

    @Test
    void el_total_lo_calcula_el_servidor_con_los_precios_del_catalogo() throws Exception {
        long entrecot = id("Entrecot a la Parrilla");   // 18.50
        long solomillo = id("Solomillo a la Plancha");  // 22.00

        // El navegador intenta mandar un precio falso: se ignora
        crearPedido("""
                { "lineas": [
                    { "productoId": %d, "cantidad": 2, "precio": 0.01 },
                    { "productoId": %d, "cantidad": 1 }
                ] }""".formatted(entrecot, solomillo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.total").value(59.00))
                .andExpect(jsonPath("$.lineas", hasSize(2)))
                .andExpect(jsonPath("$.lineas[0].nombre").value("Entrecot a la Parrilla"))
                .andExpect(jsonPath("$.lineas[0].cantidad").value(2))
                .andExpect(jsonPath("$.lineas[0].subtotal").value(37.00));

        assertThat(pedidos.count()).isEqualTo(1);
    }

    @Test
    void las_lineas_del_mismo_producto_se_juntan() throws Exception {
        long chuleton = id("Chuletón a la Brasa"); // 27.50

        crearPedido("""
                { "lineas": [
                    { "productoId": %d, "cantidad": 1 },
                    { "productoId": %d, "cantidad": 2 }
                ] }""".formatted(chuleton, chuleton))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lineas", hasSize(1)))
                .andExpect(jsonPath("$.lineas[0].cantidad").value(3))
                .andExpect(jsonPath("$.total").value(82.50));
    }

    @Test
    void no_se_puede_comprar_con_el_carrito_vacio() throws Exception {
        crearPedido("{ \"lineas\": [] }")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El carrito está vacío."));

        assertThat(pedidos.count()).isZero();
    }

    @Test
    void la_cantidad_tiene_que_estar_entre_1_y_99() throws Exception {
        long id = id("Lomo Alto");

        crearPedido("{ \"lineas\": [ { \"productoId\": %d, \"cantidad\": 0 } ] }".formatted(id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La cantidad mínima es 1."));

        crearPedido("{ \"lineas\": [ { \"productoId\": %d, \"cantidad\": 100 } ] }".formatted(id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La cantidad máxima es 99."));

        // Dos líneas de 50 del mismo producto también superan el máximo
        crearPedido("""
                { "lineas": [
                    { "productoId": %d, "cantidad": 50 },
                    { "productoId": %d, "cantidad": 50 }
                ] }""".formatted(id, id))
                .andExpect(status().isBadRequest());

        assertThat(pedidos.count()).isZero();
    }

    @Test
    void un_producto_que_no_existe_da_404_y_no_se_guarda_nada() throws Exception {
        long entrecot = id("Entrecot a la Parrilla");

        crearPedido("""
                { "lineas": [
                    { "productoId": %d, "cantidad": 1 },
                    { "productoId": 9999, "cantidad": 1 }
                ] }""".formatted(entrecot))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe ningún producto con id 9999."));

        assertThat(pedidos.count()).isZero();
    }

    @Test
    void un_json_mal_escrito_da_400() throws Exception {
        crearPedido("{ esto no es json")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El cuerpo de la petición no es un JSON válido."));
    }

    @Test
    void los_pedidos_recientes_salen_del_mas_nuevo_al_mas_antiguo_con_sus_lineas() throws Exception {
        long costillas = id("Costillas BBQ de Ternera");
        long solomillo = id("Solomillo de Ternera");

        crearPedido("{ \"lineas\": [ { \"productoId\": %d, \"cantidad\": 1 } ] }".formatted(costillas));
        crearPedido("{ \"lineas\": [ { \"productoId\": %d, \"cantidad\": 2 } ] }".formatted(solomillo));

        mvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lineas[0].nombre").value("Solomillo de Ternera"))
                .andExpect(jsonPath("$[0].total").value(68.00))
                .andExpect(jsonPath("$[1].lineas[0].nombre").value("Costillas BBQ de Ternera"));
    }

    //--------------------------------------------------------------------------------------------
    // WEB Y DOCUMENTACIÓN

    @Test
    void la_aplicacion_sirve_la_web_y_la_documentacion_de_la_api() throws Exception {
        mvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(resultado -> assertThat(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8))
                        .contains("Carnicería Rivas"));

        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/pedidos")));
    }

    //--------------------------------------------------------------------------------------------
    // AYUDAS

    private ResultActions crearPedido(String json) throws Exception {
        return mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private long id(String nombre) {
        return productos.findAll().stream()
                .filter(p -> p.getNombre().equals(nombre))
                .map(Producto::getId)
                .findFirst()
                .orElseThrow();
    }

    private static Path crearArchivoTemporal() {
        try {
            Path archivo = Files.createTempFile("carniceria-test-", ".db");
            archivo.toFile().deleteOnExit();
            return archivo;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
