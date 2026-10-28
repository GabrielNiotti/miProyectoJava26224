package com.techlab.laperfu.service;


import java.util.ArrayList;
import java.util.List;

import com.techlab.laperfu.pedidos.Pedido;

public class PedidoService {
    private final List<Pedido> historialPedidos = new ArrayList<>();

    // Guarda el pedido ya confirmado en el historial de la aplicación
    public void registrarPedido(Pedido pedido) {
        if (pedido == null || pedido.getLineas().isEmpty()) {
            throw new IllegalArgumentException("No se puede registrar un pedido vacío.");
        }
        historialPedidos.add(pedido);
    }

    // Devuelve todos los pedidos realizados
    public List<Pedido> listarTodos() {
        return new ArrayList<>(historialPedidos);
    }
}
