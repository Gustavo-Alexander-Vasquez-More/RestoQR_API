package com.restoqr.restoApi.controller;

import com.restoqr.restoApi.models.Order;
import com.restoqr.restoApi.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        return orderOpt
                .<ResponseEntity<?>>map(order -> ResponseEntity.ok(order))
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("mensaje", "La orden no existe.");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @GetMapping("/")
    public ResponseEntity<?> getOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudieron obtener las órdenes.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countOrders() {
        try {
            long count = orderRepository.count();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo obtener el conteo de órdenes.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.status(201).body(savedOrder);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo crear la orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        try {
            if (!orderRepository.existsById(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La orden no existe.");
                return ResponseEntity.status(404).body(response);
            }
            orderRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden con id " + id + " eliminada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar la orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody Order updatedOrder) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "La orden no existe.");
                return ResponseEntity.status(404).body(response);
            }
            order.setNumberTable(updatedOrder.getNumberTable());
            order.setProducts(updatedOrder.getProducts());
            order.setStatus(updatedOrder.getStatus());
            order.setTotalPrice(updatedOrder.getTotalPrice());
            orderRepository.save(order);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden actualizada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo actualizar la orden.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/findByNumOrden/{numOrden}")
    public ResponseEntity<?> getOrderByNumOrden(@PathVariable int numOrden) {
        Optional<Order> orderOpt = orderRepository.findByNumOrden(numOrden);
        return orderOpt
                .<ResponseEntity<?>>map(order -> ResponseEntity.ok(order))
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("mensaje", "La orden no existe.");
                    return ResponseEntity.status(404).body(response);
                });
    }
}
