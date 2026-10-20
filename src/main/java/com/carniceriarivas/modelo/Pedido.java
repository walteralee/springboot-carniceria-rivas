package com.carniceriarivas.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido hecho desde el carrito (tabla pedido). Sus productos están en LineaPedido.
 */
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    @BatchSize(size = 10)
    private List<LineaPedido> lineas = new ArrayList<>();

    protected Pedido() {
        // Constructor vacío que necesita JPA
    }

    public Pedido(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // Añade la línea al pedido y actualiza el total
    public void agregarLinea(LineaPedido linea) {
        linea.asignarPedido(this);
        lineas.add(linea);
        total = total.add(linea.getSubtotal());
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }
}
