package com.carniceriarivas.controlador;

import com.carniceriarivas.dto.PedidoPeticion;
import com.carniceriarivas.dto.PedidoRespuesta;
import com.carniceriarivas.servicio.PedidoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de los pedidos. Solo recibe la petición y llama al servicio.
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Compras hechas desde el carrito")
public class PedidoControlador {

    private final PedidoServicio pedidos;

    public PedidoControlador(PedidoServicio pedidos) {
        this.pedidos = pedidos;
    }

    // POST /api/pedidos  { "lineas": [ { "productoId": 1, "cantidad": 2 } ] }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un pedido con los productos del carrito. El total lo calcula el servidor")
    public PedidoRespuesta crear(@Valid @RequestBody PedidoPeticion peticion) {
        return pedidos.crear(peticion);
    }

    // GET /api/pedidos -> los 10 últimos
    @GetMapping
    @Operation(summary = "Lista los 10 últimos pedidos")
    public List<PedidoRespuesta> recientes() {
        return pedidos.listarRecientes();
    }
}
