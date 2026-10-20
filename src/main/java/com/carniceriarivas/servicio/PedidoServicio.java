package com.carniceriarivas.servicio;

import com.carniceriarivas.dto.PedidoPeticion;
import com.carniceriarivas.dto.PedidoRespuesta;
import com.carniceriarivas.error.PeticionNoValida;
import com.carniceriarivas.error.RecursoNoEncontrado;
import com.carniceriarivas.modelo.LineaPedido;
import com.carniceriarivas.modelo.Pedido;
import com.carniceriarivas.modelo.Producto;
import com.carniceriarivas.repositorio.PedidoRepositorio;
import com.carniceriarivas.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reglas de los pedidos: se comprueba que los productos existen, se juntan las líneas repetidas
 * y el total se calcula con los precios del catálogo (nunca con los que envíe el navegador).
 */
@Service
public class PedidoServicio {

    static final int CANTIDAD_MAXIMA = 99;

    private final ProductoRepositorio productos;
    private final PedidoRepositorio pedidos;
    private final Clock reloj;

    public PedidoServicio(ProductoRepositorio productos, PedidoRepositorio pedidos, Clock reloj) {
        this.productos = productos;
        this.pedidos = pedidos;
        this.reloj = reloj;
    }

    @Transactional
    public PedidoRespuesta crear(PedidoPeticion peticion) {
        // 1) Juntar las líneas del mismo producto (por si llega dos veces) respetando el orden del carrito
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (PedidoPeticion.Linea linea : peticion.lineas()) {
            cantidades.merge(linea.productoId(), linea.cantidad(), Integer::sum);
        }

        if (cantidades.values().stream().anyMatch(cantidad -> cantidad > CANTIDAD_MAXIMA)) {
            throw new PeticionNoValida("Como máximo se pueden pedir " + CANTIDAD_MAXIMA + " unidades de cada producto.");
        }

        // 2) Buscar los productos en el catálogo
        Map<Long, Producto> catalogo = productos.findAllById(cantidades.keySet()).stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));

        for (Long id : cantidades.keySet()) {
            if (!catalogo.containsKey(id)) {
                throw new RecursoNoEncontrado("No existe ningún producto con id " + id + ".");
            }
        }

        // 3) Crear el pedido con sus líneas; el total lo va sumando el propio pedido
        Pedido pedido = new Pedido(LocalDateTime.now(reloj));
        cantidades.forEach((id, cantidad) -> pedido.agregarLinea(new LineaPedido(catalogo.get(id), cantidad)));

        return PedidoRespuesta.de(pedidos.save(pedido));
    }

    // Los 10 últimos pedidos, del más nuevo al más antiguo
    @Transactional(readOnly = true)
    public List<PedidoRespuesta> listarRecientes() {
        return pedidos.findTop10ByOrderByIdDesc().stream().map(PedidoRespuesta::de).toList();
    }
}
